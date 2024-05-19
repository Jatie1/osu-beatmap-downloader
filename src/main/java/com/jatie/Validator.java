package com.jatie;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class Validator {
    public static boolean validateApiKey(String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://osu.ppy.sh/api/get_beatmaps?k=" + apiKey + "&s=1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public static boolean validateOsuDirectory(String osuDirectory) {
        File songsFolder = new File(osuDirectory + "\\Songs");
        return songsFolder.isDirectory() && osuDirectory.matches(".+\\\\+osu!$");
    }

    public static boolean validateLoginDetails(String username, String password) {

    }
}
