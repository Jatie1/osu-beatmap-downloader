package com.jatie;

import com.jatie.configfile.ConfigFile;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class Validator {
    public static boolean validateConfigFile(ConfigFile configFile) throws IOException {
        return validateApiKey(configFile.getApiKey())
                && validateOsuDirectory(configFile.getOsuDirectory())
                && validateLoginDetails(configFile.getUsername(), configFile.getPassword());
    }

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
        File dbFile = new File(osuDirectory + "\\osu!.db");
        return songsFolder.isDirectory()
                && dbFile.isFile()
                && osuDirectory.endsWith("\\osu!");
    }

    public static boolean validateLoginDetails(String username, String password) {
        return true;
    }
}
