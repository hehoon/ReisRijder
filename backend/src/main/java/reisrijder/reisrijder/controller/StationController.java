package reisrijder.reisrijder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reisrijder.reisrijder.ReisRijderApplication;
import reisrijder.reisrijder.model.StationModel;
import reisrijder.reisrijder.thirdparty.NSApi;

import java.time.Duration;
import java.util.*;
import java.util.function.UnaryOperator;

@CrossOrigin(origins = ReisRijderApplication.CROSS_ORIGIN)
@RestController
@RequestMapping("/api/station")
public class StationController {

    private static final StationModel[] ALL_STATIONS = fetchStations();

    private final LocalBucket bucket;

    public StationController() {
        final int API_LIMIT = 120;
        Bandwidth bandwidth = Bandwidth.classic(API_LIMIT, Refill.greedy(API_LIMIT, Duration.ofMinutes(1)));
        this.bucket = new LocalBucketBuilder().addLimit(bandwidth).build();
    }

    /**
     * Gets all available stations
     * @return An array of stations with their respective attributes
     * @apiNote 4 tokens per request
     */
    private static StationModel[] fetchStations() {
        // Map filter types to a UnaryOperator, which converts a raw string to a processed string
        final Map<String, UnaryOperator<String>> FILTER_FIELDS = Map.of(
                "Bagagekluizen", (String s) -> s.substring(s.lastIndexOf("station") + "station".length()).trim(),
                "OV-fiets", (String s) -> s.substring(Math.max(s.lastIndexOf("-") + "-".length(), s.lastIndexOf("bike") + "bike".length())).trim()
        );
        final List<String> BLACKLISTED_STATIONS = new ArrayList<>(List.of("GERP - OV-fiets - Openbare fietsenstalling gemeente Groningen : Fietsenstalling Europapark"));

        List<StationModel> stations = new ArrayList<>();

        // Fetch all the stations
        String url = "https://gateway.apiportal.ns.nl/places-api/v2/autosuggest?type=stationfacility";
        String response = NSApi.getNSApi(url);

        // Checking if the response was successful
        if (response == null) {
            System.err.println("Third party API response returned null");
            return listToArray(stations);
        }

        // Building the JSON response
        JsonNode payloadNode = NSApi.buildPayloadJson(response);
        if (payloadNode == null || !payloadNode.isArray()) {
            System.err.println("Third party API response is empty");
            return listToArray(stations);
        }

        // Looping through all the station facilities
        for (JsonNode stationNode : payloadNode) {

            // Getting the station facility and looping through all the filter fields to see if this facility type matches any
            String filterField = stationNode.get("name").asText();
            for (Map.Entry<String, UnaryOperator<String>> entry : FILTER_FIELDS.entrySet()) {

                // Checking if the station facility type matches
                if (entry.getKey().equals(filterField)) {
                    // Getting every station within this facility type
                    for (JsonNode locationNode : stationNode.get("locations")) {

                        // Get the raw station name
                        String rawName = locationNode.get("name").asText();
                        // Filter the station
                        if (BLACKLISTED_STATIONS.contains(rawName)) {
                            continue;
                        }
                        // Convert the station name
                        String name = entry.getValue().apply(rawName);

                        // Get any already existing station (check by name)
                        StationModel station = stations.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
                        if (station == null) {
                            // No station found, create one and add it to the stations list
                            double latitude = locationNode.get("lat").asDouble();
                            double longitude = locationNode.get("lng").asDouble();
                            station = new StationModel(name, latitude, longitude);
                            stations.add(station);
                        }
                        // Set new station attributes and keep old attributes
                        station.setOvBike(rawName.contains("OV-fiets") || station.isOvBike());
                        station.setOvEbike(rawName.contains("OV-ebike") || station.isOvEbike());
                        station.setBaggage(rawName.contains("Bagagekluizen") || station.isBaggage());
                    }
                }
            }
        }

        return listToArray(stations);
    }

    private static StationModel[] listToArray(List<StationModel> list) {
        return list.toArray(new StationModel[0]);
    }

    @GetMapping(value = "/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationModel[]> getStationsFromLocationWithParameters(
            @RequestParam(name = "lat") double latitude,
            @RequestParam(name = "lon") double longitude,
            @RequestParam(name = "ovbike", required = false) boolean ovBike,
            @RequestParam(name = "ovebike", required = false) boolean ovEbike,
            @RequestParam(name = "baggage", required = false) boolean baggage,
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        // Check rate limit
        if (!bucket.tryConsume(4)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        List<StationModel> stations = new ArrayList<>();
        try {
            for (StationModel station : ALL_STATIONS) {
                if (station.matchesParameters(ovBike, ovEbike, baggage)) {
                    // Cloning the station isn't necessary in this case... so let's do it anyways! :/
                    StationModel stationClone = station.clone();
                    stationClone.setDistance(latitude, longitude);
                    stations.add(stationClone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort stations from lowest to highest distance
        stations.sort(Comparator.comparingInt((StationModel::getDistanceMeters)));

        if (limit == null || limit <= 0) {
            return ResponseEntity.ok(listToArray(stations));
        }
        return ResponseEntity.ok(listToArray(stations.subList(0, Math.min(limit, stations.size()))));
    }
}