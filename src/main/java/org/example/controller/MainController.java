package org.example.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

@Controller
@RequestMapping("/difference")
public class MainController {

    @GetMapping("/{value}")
    public String difference(@PathVariable String value, Model model) throws IOException, ParseException {
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

        String url;

        if (valueToday > valueYesterday){
            url = getURLofGIF("rich");
        }else{
            url = getURLofGIF("broke");
        }

        model.addAttribute("url", url);
        return "gif";
    }

    public Double getValue(URL url, String value) throws IOException, ParseException {

        JSONObject jsonObject = getJson(url);

        //Get the required object from the above created object
        JSONObject obj = (JSONObject) jsonObject.get("rates");
        return (Double) obj.get(value);
    }

    public String getURLofGIF(String tag) throws IOException, ParseException {
        URL url = new URL("https://api.giphy.com/v1/gifs/random" +
                "?api_key=nxZ4jNQlzBg0SALb2om92bZmLDS2df98" +
                "&tag=" + tag + "&rating=g");

        JSONObject jsonObject = getJson(url);
        JSONObject obj = (JSONObject) jsonObject.get("data");
        obj = (JSONObject) obj.get("images");
        obj = (JSONObject) obj.get("fixed_width_still");
        String urlOfGif = (String) obj.get("url");

        return urlOfGif;
    }

    public JSONObject getJson(URL url) throws IOException, ParseException {
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

            JSONParser parse = new JSONParser();

            return (JSONObject) parse.parse(inline.toString());
        }
    }
}
