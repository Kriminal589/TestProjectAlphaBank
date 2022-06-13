package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args )
    {
        try {
            URL url = new URL("https://openexchangerates.org/api/latest.json" +
                    "?app_id=5b874df6ddef4016b24a8052735c8715");

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

                //Get the required data using its key
                System.out.println(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
