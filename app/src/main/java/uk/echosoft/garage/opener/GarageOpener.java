package uk.echosoft.garage.opener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

class GarageOpener {

    private final OkHttpClient client;
    private final String authToken;
    private final String uri;

    GarageOpener(String uri, String authToken) {
        this.client = new OkHttpClient();
        this.uri = uri;
        this.authToken = authToken;
    }

    String getGarageState() throws IOException, NotAuthenticatedException {
        Request request = new Request.Builder()
                .header("X-Auth-Token", authToken)
                .url(uri + "/garage/state")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        checkAuthentication(response);
        Type stringStringMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> state = new Gson().fromJson(response.body().charStream(), stringStringMap);
        return state.get("Description");
    }

    void toggleGarageDoor() throws IOException {
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .header("X-Auth-Token", this.authToken)
                .url(uri + "/garage/toggle")
                .post(RequestBody.create(jsonMediaType, ""))
                .build();

        client.newCall(request).execute();
    }

    String getOneTimePin() throws NotAuthenticatedException, IOException {
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .header("X-Auth-Token", authToken)
                .url(uri + "/user/one-time-pin")
                .post(RequestBody.create(jsonMediaType, ""))
                .build();

        Response response = client.newCall(request).execute();
        checkAuthentication(response);
        Type stringStringMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> pinData = new Gson().fromJson(response.body().charStream(), stringStringMap);
        String pin = pinData.get("pin");
        return String.format("%s/user/%s", uri, pin);
    }

    private void checkAuthentication(Response response) throws NotAuthenticatedException {
        if (response.code() == 401) {
            throw new NotAuthenticatedException();
        }
    }
}
