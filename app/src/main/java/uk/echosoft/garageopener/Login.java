package uk.echosoft.garageopener;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Login {

    static class LoginResult {
        private final boolean success;
        private final String authToken;

        private LoginResult(boolean success, String authToken) {
            this.success = success;
            this.authToken = authToken;
        }

        static LoginResult success(String authToken) {
            return new LoginResult(true, authToken);
        }

        static LoginResult failure() {
            return new LoginResult(false, null);
        }

        boolean isSuccess() {
            return success;
        }

        String getAuthToken() {
            return authToken;
        }

    }

    private final OkHttpClient client;
    private final String authenticationUrl;

    Login(String authenticationUrl) {
        this.client = new OkHttpClient();
        this.authenticationUrl = authenticationUrl;
    }

    LoginResult login(String email, String password) throws IOException {
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> data = new HashMap<>();
        data.put("Email", email);
        data.put("Password", password);
        RequestBody requestBody = RequestBody.create(jsonMediaType, new Gson().toJson(data));

        // Create request for remote resource.
        Request request = new Request.Builder()
                .url(authenticationUrl + "/user/login")
                .post(requestBody)
                .build();

        // Execute the request and retrieve the response.
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return LoginResult.success(response.header("X-Auth-Token"));
        } else {
            return LoginResult.failure();
        }
    }
}
