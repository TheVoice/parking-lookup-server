package org.code;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

@SpringBootApplication
@RestController
public class ParkingLookupServerApplication {

    @Value("${mapDatabase.url}")
    private String targetMapDatabaseUrl;

    /**
     *
     * @param latitude the latitude of the lookup spot
     * @param longitude the longitude of the lookup spot
     * @param gratuit is the search limited to free spots
     * @param limit the number of parkings in the response (0-100)
     * @return list of nearby parking spots in JSON format
     * @throws IOException the IOException
     * @throws URISyntaxException the UTISyntaxException
     */
    @GetMapping("/parkings")
    public JsonNode getNearbyParkingsAsJson(
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "gratuit", required = false, defaultValue = "false") Boolean gratuit,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) throws IOException, URISyntaxException {
        return getJSONFromRegionalEndpoint(latitude, longitude, gratuit, limit);
    }

    private JsonNode getJSONFromRegionalEndpoint(Double latitude, Double longitude, Boolean gratuit, Integer limit) throws IOException, URISyntaxException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;

        if(limit > 100 || limit < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit parameter should fall in range of [0-100]");
        }

        //Fetch all fields and add distance (in metres) to allow finding the closest spot
        String selectParameter = String.format("*, distance(meta_geo_point, geom'{\"type\": \"Point\",\"coordinates\":[%f,%f]}') as distance", longitude, latitude);
        String whereParameter = gratuit ? "gratuit = \"true\"" : "";

        URI uri = new URI(targetMapDatabaseUrl);
        uri = UriComponentsBuilder.fromUri(uri)
                .queryParam("select", selectParameter)
                .queryParam("order_by", "distance")
                .queryParam("where", whereParameter)
                .queryParam("limit", limit)
                .build().toUri();
        URL url = uri.toURL();



        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if(connection.getResponseCode() != 200) {
            throw new RuntimeException("Connection error");
        } else {
            StringBuilder rawData = new StringBuilder();
            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNext()) {
                    rawData.append(scanner.nextLine());
                }
            }


            rootNode = objectMapper.readTree(rawData.toString());
        }

        return rootNode.get("results");
    }

    public static void main(String[] args) {
        SpringApplication.run(ParkingLookupServerApplication.class, args);
    }
}
