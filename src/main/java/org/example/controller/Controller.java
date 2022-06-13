package org.example.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

@RestController
@RequestMapping("/difference")
public class Controller {

    @GetMapping("/{value}")
    public boolean difference(@PathVariable String value) throws IOException, ParseException {
        LocalDate date = LocalDate.now();
        LocalDate yesterday = date.minusDays(1);

        URL urlYesterday = new URL("https://openexchangerates.org/api/historical/" +
                yesterday + ".json" +
                "?app_id=5b874df6ddef4016b24a8052735c8715" +
                "&symbols=" + value);

        URL urlToday = new URL("https://openexchangerates.org/api/latest.json" +
                "?app_id=5b874df6ddef4016b24a8052735c8715" +
                "&symbols=" + value);

        Double valueYesterday = getValue(urlYesterday, value);
        Double valueToday = getValue(urlToday, value);

        return valueToday > valueYesterday;
    }

    public Double getValue(URL url, String value) throws IOException, ParseException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            scanner.close();

            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(inline.toString());

            //Get the required object from the above created object
            JSONObject obj = (JSONObject) data_obj.get("rates");
            return (Double) obj.get(value);
        }
    }
}
