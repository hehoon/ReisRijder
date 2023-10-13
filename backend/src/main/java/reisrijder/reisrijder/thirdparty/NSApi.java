package reisrijder.reisrijder.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reisrijder.reisrijder.ReisRijderApplication;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class NSApi {

    /**
     * Makes a connection to the NS API server
     * @param url The URL to connect to
     * @return The response from the NS API server
     */
    public static String getNSApi(String url) {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            // Setting up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", ReisRijderApplication.OCP_APIM_SUBSCRIPTION_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Getting the response from the third party api
            ignoreCertificates();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().value() >= 300) {
                return null;
            }

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode buildPayloadJson(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(response);
            return responseNode.get("payload");
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static void ignoreCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
