package com.company.smartCollections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SmartCollections {
    private static final String collections_path = "/Users/solorzke/Documents/Developer/Shopify-Programs/src/com/company/smartCollections/all-collections.json";
    private static final String domainKey = "domain";
    private static final String tokenKey = "token";
    private static final String shopifyAccessTokenKey = "X-Shopify-Access-Token";
    private static final String contentTypeKey = "Content-Type";

    public static void init() {
        createSmartCollections();
    }

    private static Map<String, String> askInfo() {
        Map <String, String> responses = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("************* Smart Collection Automator by Kevin Solorzano *************");
        System.out.println("> Before using this app, make sure you set up a private app on the shopify store that it will access. \\n Visit https://help.shopify.com/en/manual/apps/private-apps for further questions on set up.");
        System.out.println("> Enter the store's sub-domain (ex: https://{STORE}.myshopify.com): ");
        String domain = scanner.nextLine();
        System.out.println("Enter the access token of the private app after its been set up: ");
        String token = scanner.nextLine();
        scanner.close();
        responses.put(domainKey, domain);
        responses.put(tokenKey, token);
        return responses;
    }

    private static void createSmartCollections() {
        Map<String, String> responses = askInfo();
        JSONArray json = loadJSON(collections_path);
        String store = createShopifyStoreEndpoint(responses.get(domainKey));
        String token = responses.get(tokenKey);

        for(int i = 0; i < json.size(); i++) {
            JSONObject collection = (JSONObject) json.get(i);
            String stringified = collection.toJSONString();
            System.out.println("> Please wait... Creating smart collections for " + responses.get(domainKey));
            sendData(stringified, token, store);
        }

        System.out.println("> Shutting Down....");
    }

    private static void createInStockCollections() {
        Map<String, String> responses = askInfo();
        String store = createShopifyStoreEndpoint(responses.get(domainKey));
        String token = responses.get(tokenKey);

        for(String obj : createInStockCollectonsList()) {
            System.out.println("> Please wait... Creating in stock smart collections for " + responses.get(domainKey));
            sendData(obj, token, store);
        }
    }

    private static void sendData(String data, String token, String store) {
        try {
            URL url = new URL(store);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty(contentTypeKey, "application/json");
            http.setRequestProperty(shopifyAccessTokenKey, token);
            byte [] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            http.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createShopifyStoreEndpoint(String domain) {
        return "https://" + domain + ".myshopify.com/admin/api/2021-07/smart_collections.json";
    }

    private static JSONArray loadJSON(String path) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(path));
            jsonObject = (JSONObject) obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (JSONArray) jsonObject.get("collections");
    }

    private static Map<String, String> getHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put(shopifyAccessTokenKey, token);
        headers.put(contentTypeKey, "application/json");
        return headers;
    }

    private static List<String> createInStockCollectonsList() {
        List<String> data = new ArrayList<>();
        for(String collection : Collections.types) {
            String obj = "{\"smart_collection\":{\"disjunctive\":\"false\",\"rules\":[{\"condition\":\"" + collection + "\",\"column\":\"type\",\"relation\":\"equals\"},{\"condition\":\"availability_in-stock\",\"column\":\"tag\",\"relation\":\"equals\"}],\"title\":\"" + collection + " In Stock\"}}";
            data.add(obj);
        }
        return data;
    }

}

class Collections {
    static String [] types = new String[] {
        "Living Room Set",
        "Sofa",
        "Loveseat",
        "Sectional",
        "Recliner",
        "Sleeper",
        "Ottoman",
        "End Table",
        "Sofa Table",
        "Cocktail Table",
            "Bedroom Set",
            "Youth Bedroom Set",
            "Bed",
            "Bunk Bed",
            "Nightstand",
            "Dresser",
            "Dresser & Mirror",
            "Chest",
            "Mirror",
            "Vanity",
            "Dining Room Set",
            "Dining Table",
            "Dining Chair",
            "Dining Chair Set",
            "Bar Stool",
            "Bar Stool Set",
            "Buffet",
            "Server",
            "Mattress",
            "Mattress & Adjustable Base",
            "Adjustable Base",
            "Foundation",
            "Home Office Set",
            "Desk",
            "Desk Chair",
            "Bookcase",
            "File Cabinet",
            "Entertainment Center",
            "TV Stand",
            "Wall Art",
            "Wall Decor",
            "Throw",
            "Basket",
            "Pouf",
            "Sculpture",
            "Lamp",
            "Candle Holder",
    };
}