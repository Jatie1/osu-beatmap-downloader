package com.jatie;

import com.jatie.entity.ConfigFileProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.File;

public class Validator {
    public static boolean validateConfigFile(ConfigFileProperties configFileProperties) throws Exception {
        boolean isValid = true;
        if (!validateApiKey(configFileProperties.apiKey())) {
            System.out.println("Invalid API key from config file!");
            isValid = false;
        }
        if (!validateOsuDirectory(configFileProperties.osuDirectory())) {
            System.out.println("Invalid osu! directory from config file!");
            isValid = false;
        }
        return isValid;
    }

    public static boolean validateApiKey(String apiKey) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://osu.ppy.sh/api/get_beatmaps?k=" + apiKey + "&s=1"))
                    .HEAD()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;
        }
    }

    public static boolean validateOsuDirectory(String osuDirectory) {
        File songsFolder = new File(osuDirectory + File.separator + "Songs");
        File dbFile = new File(osuDirectory + File.separator + "osu!.db");

        return songsFolder.isDirectory() && dbFile.isFile() && osuDirectory.endsWith(File.separator + "osu!");
    }
}
