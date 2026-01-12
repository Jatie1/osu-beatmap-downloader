package com.jatie;

import com.jatie.configfile.ConfigFileManager;
import com.jatie.entity.Beatmap;
import com.jatie.entity.ConfigFileProperties;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class BeatmapDownloader {

    static void main() {
        try {
            OnScreen.disclaimer();
            ConfigFileProperties configFileProperties = ConfigFileManager.manageConfigFile();
            String[] yearRange = OnScreen.userEnterYearRange();
            boolean userIncludeRankedMaps = OnScreen.userEnterRankedStatusPreference("ranked");
            boolean userIncludeApprovedMaps = OnScreen.userEnterRankedStatusPreference("approved");
            boolean userIncludeQualifiedMaps = OnScreen.userEnterRankedStatusPreference("qualified");
            boolean userIncludeLovedMaps = OnScreen.userEnterRankedStatusPreference("loved");
            Set<Integer> beatmapSetIdsFromDB = OsuDatabaseReader.getBeatmapSetIdsFromDBFile(configFileProperties);
            Set<Beatmap> beatmapsToDownload = OsuApiCaller.getAllBeatmapsFromApi(yearRange[0], yearRange[1], configFileProperties.apiKey(), userIncludeRankedMaps, userIncludeApprovedMaps, userIncludeQualifiedMaps, userIncludeLovedMaps);
            beatmapsToDownload.removeIf(beatmap -> beatmapSetIdsFromDB.contains(beatmap.setId())); // Filter out all the beatmaps from the osu!.db file
            Downloader.downloadBeatmaps(beatmapsToDownload, configFileProperties.osuDirectory(), configFileProperties.sessionCookie());
        } catch (Exception e) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("error.txt", false))) {
                e.printStackTrace(writer);
            } catch (IOException ie) {
                System.out.println(ie.getMessage());
            }
        }
    }
}