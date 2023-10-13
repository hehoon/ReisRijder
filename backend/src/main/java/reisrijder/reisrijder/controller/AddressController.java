package reisrijder.reisrijder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reisrijder.reisrijder.ReisRijderApplication;
import reisrijder.reisrijder.model.AddressModel;
import reisrijder.reisrijder.model.request.AddressAutoComplete;
import reisrijder.reisrijder.model.request.AddressLocation;
import reisrijder.reisrijder.thirdparty.NSApi;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = ReisRijderApplication.CROSS_ORIGIN)
@RestController
@RequestMapping("/api/address")
public class AddressController {

    // Cache the most relevant addresses
    private static final Cache<String, AddressModel[]> CACHE_AUTO_COMPLETE = Caffeine.newBuilder().maximumSize(10000).build();
    private static final Cache<Double, AddressModel> CACHE_LOCATION = Caffeine.newBuilder().maximumSize(100).build();

    private final LocalBucket bucket;

    public AddressController() {
        final int API_LIMIT = 120;
        Bandwidth bandwidth = Bandwidth.classic(API_LIMIT, Refill.greedy(API_LIMIT, Duration.ofMinutes(1)));
        this.bucket = new LocalBucketBuilder().addLimit(bandwidth).build();
    }

    /**
     * Find a short list of existing address based on the requested string given
     * @param request See AddressAutoComplete
     * @return A list of addresses, see AddressModel
     * @apiNote 1 token per request
     */
    @PostMapping(value = "/autocomplete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressModel[]> getAddressFromText(@RequestBody AddressAutoComplete request) {
        // Check rate limit
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        List<AddressModel> addresses = new ArrayList<>();
        try {
            // Get the address for auto-completion
            String lookupAddress = request.getAddress();
            if (lookupAddress == null) {
                return ResponseEntity.badRequest().build();
            }

            // Check cached addresses
            AddressModel[] cachedAddresses = CACHE_AUTO_COMPLETE.getIfPresent(lookupAddress);
            if (cachedAddresses != null) {
                return ResponseEntity.ok(cachedAddresses);
            }

            // Getting auto-completion suggestions from third party api
            String url = String.format("https://gateway.apiportal.ns.nl/places-api/v2/autosuggest?q=%s&type=address", lookupAddress);
            String response = NSApi.getNSApi(url);

            // Checking if the response was successful
            if (response == null) {
                return ResponseEntity.badRequest().build();
            }

            // Building the response for "/autocomplete" endpoint
            JsonNode payloadNode = NSApi.buildPayloadJson(response);
            if (payloadNode == null || payloadNode.get(0) == null) {
                // Empty response
                return ResponseEntity.badRequest().build();
            }

            JsonNode locationsNode = payloadNode.get(0).get("locations");
            if (locationsNode != null && locationsNode.isArray()) {
                for (JsonNode location : locationsNode) {
                    AddressModel address = new AddressModel();
                    address.setName(location.get("name").asText());
                    address.setLatitude(location.get("lat").asDouble());
                    address.setLongitude(location.get("lng").asDouble());
                    addresses.add(address);
                }
            }

            // Enter current list of addresses in the cache
            CACHE_AUTO_COMPLETE.put(lookupAddress, listToArray(addresses));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(listToArray(addresses));
    }

    /**
     * Find a public location based on the requested location given
     * @param request See AddressLocation
     * @return An address, see AddressModel
     * @apiNote 3 tokens per request
     */
    @PostMapping(value = "/location", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressModel> getAddressFromCoords(@RequestBody AddressLocation request) {
        // Check rate limit
        if (!bucket.tryConsume(3)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        AddressModel address = null;
        try {
            // Get the location
            final double UNIQUE_VALUE = request.getUniqueValue();
            if (UNIQUE_VALUE == 0) {
                return ResponseEntity.badRequest().build();
            }

            // Check cached addresses
            AddressModel cachedAddresses = CACHE_LOCATION.getIfPresent(UNIQUE_VALUE);
            if (cachedAddresses != null) {
                return ResponseEntity.ok(cachedAddresses);
            }

            double latitude = request.getLatitude();
            double longitude = request.getLongitude();

            // Getting location suggestion from third party api
            String url = String.format("https://gateway.apiportal.ns.nl/places-api/v2/places/nearest?lat=%f&lng=%f&limit=1&radius=1000", latitude, longitude);
            String response = NSApi.getNSApi(url);

            // Checking if the response was successful
            if (response == null) {
                return ResponseEntity.badRequest().build();
            }

            JsonNode payloadNode = NSApi.buildPayloadJson(response);
            if (payloadNode == null || !payloadNode.has("name")) {
                // Empty response
                return ResponseEntity.badRequest().build();
            }

            String name = payloadNode.get("name").asText();
            double lat = payloadNode.get("lat").asDouble();
            double lon = payloadNode.get("lng").asDouble();

            address = new AddressModel(name, lat, lon);

            // Enter current address in the cache
            CACHE_LOCATION.put(UNIQUE_VALUE, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(address);
    }

    private AddressModel[] listToArray(List<AddressModel> list) {
        return list.toArray(new AddressModel[0]);
    }
}