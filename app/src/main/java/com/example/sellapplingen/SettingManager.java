package com.example.sellapplingen;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


public class SettingManager {

    private static final String API_URL = "http://131.173.65.77:8080/api/settings";

    private static final Gson gson = new Gson();

    public class Parameter {
        public static final String STORE_NAME = "storeName";
        public static final String OWNER = "owner";
        public static final String TELEPHONE = "telephone";
        public static final String EMAIL = "email";
        public static final String LOGO = "logo";
        public static final String BACKGROUND_IMAGE = "backgroundImage";
        public static final String PASSWORD = "password";
    }


    public static Settings getSettings(String token) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;

        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonResponse = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error appropriately
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Settings settings = gson.fromJson(jsonResponse, Settings.class);
            return settings;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CompletableFuture<Boolean> setSettings(String token, String parameter, String value) throws IOException, JSONException {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("parameter", parameter);
                    jsonObject.put("value", value);

                    String jsonString = jsonObject.toString();
                    byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);

                    try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                        os.write(postData);
                        os.flush();
                    }

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        InputStream inputStream = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();
                        inputStream.close();

                        String responseString = responseBuilder.toString();
                        JSONObject responseJson = new JSONObject(responseString);

                        System.out.println("Server Antwort: " + responseString);

                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            });
        }

    public void setAddressAsync(final String token, final String address) {
        /*new Thread(() -> {
            try {
                boolean success = setSettings(token, "address", address, null, null);
                // Hier kannst du das Ergebnis verwenden, z.B. eine Benachrichtigung anzeigen
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // Hier kannst du einen Fehler behandeln, z.B. eine Fehlermeldung anzeigen
            }
        }).start(); */
    }




    public static boolean setAddress(String token, String street, String houseNumber, String zip) throws IOException, JSONException {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("token", token);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject addressJson = new JSONObject();
        addressJson.put("street", street);
        addressJson.put("houseNumber", houseNumber);
        addressJson.put("zip", zip);

        jsonParam.put("address", addressJson);

        String jsonString = jsonParam.toString();

        HttpURLConnection conn = null;
        DataOutputStream os = null;

        try {
            URL url = new URL("http://131.173.65.77:8080/api/setAddress");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);

            os = new DataOutputStream(conn.getOutputStream());
            os.write(postData);
            os.flush();

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                os.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}