package com.jatie;

import com.jatie.entity.Beatmap;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Downloader {

    private static final Set<Integer> BROKEN_BEATMAPS = new HashSet<>();

    public static void downloadBeatmaps(Set<Beatmap> missingBeatmaps, String osuDirectory, String sessionCookie, int downloadStrategy) throws Exception {
        System.out.println("\nStarting downloads!");

        long startTime = System.currentTimeMillis();
        String songsDirectory = osuDirectory + File.separator + "Songs";

        switch (downloadStrategy) {
            case 1:
                downloadAllBeatmapsFromUrl(missingBeatmaps, songsDirectory, sessionCookie);
            case 2:

        }

        Duration duration = Duration.ofMillis(System.currentTimeMillis() - startTime);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        System.out.println("\nCompleted all downloads in " + days + "d " + hours + "h " + minutes + "m " + seconds + "s! Check failedbeatmaps.txt for all beatmaps that couldn't be downloaded!");
    }

    private static void downloadAllBeatmapsFromUrl(Set<Beatmap> beatmapsToDownload, String songsDirectory, String sessionCookie) throws Exception {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL)
                .build()) {

            int count = 1;
            for (Beatmap beatmap : beatmapsToDownload) {
                System.out.println("(" + count++ + "/" + beatmapsToDownload.size() + ") Downloading " + beatmap.setId() + " " + beatmap.artistName() + " - " + beatmap.songName());

                if (shouldBeatmapBeDownloaded(beatmap)) {
                    if (!downloadSingleBeatmapFromUrl(beatmap, songsDirectory, sessionCookie, client)) {
                        writeFailedBeatmap(beatmap, "DOWNLOAD ISSUE");
                    }
                } else {
                    writeFailedBeatmap(beatmap, "DMCA/BROKEN");
                }
            }
        }
    }

    private static boolean downloadSingleBeatmapFromUrl(Beatmap beatmap, String songsDirectory, String sessionCookie, HttpClient client) {

        String sanitizedArtistName = beatmap.artistName().replaceAll("[\\\\/:*?\"<>|]", "");
        String sanitizedSongName = beatmap.songName().replaceAll("[\\\\/:*?\"<>|]", "");
        Path outputFile = Paths.get(songsDirectory + File.separator + beatmap.setId() + " " + sanitizedArtistName + " - " + sanitizedSongName + ".osz");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://osu.ppy.sh/beatmapsets/" + beatmap.setId() + "/download?noVideo=1"))
                    .header("Referer", "https://osu.ppy.sh/beatmapsets/" + beatmap.setId())
                    .header("Cookie", "osu_session=" + sessionCookie)
                    .GET()
                    .build();

            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(outputFile));

            if (response.statusCode() == 429) {
                System.out.println("The program has hit osu.ppy.sh's rate limit! We must wait for 30 minutes to download maps again...");
                Thread.sleep(1800200);
                return downloadSingleBeatmapFromUrl(beatmap, songsDirectory, sessionCookie, client);
            }

            if (response.statusCode() != 200) {
                return false;
            }

            System.out.println("Completed " + outputFile);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean shouldBeatmapBeDownloaded(Beatmap beatmap) {
        return !beatmap.isRemoved() && !BROKEN_BEATMAPS.contains(beatmap.setId());
    }

    private static void writeFailedBeatmap(Beatmap failedBeatmap, String reason) throws IOException {
        System.out.println(reason + " Beatmap " + failedBeatmap.setId() + " " + failedBeatmap.artistName() + " - " + failedBeatmap.songName() + " cannot be downloaded, writing to failedbeatmaps.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("failedbeatmaps.txt", true))) {
            String currentDateTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
            bw.write(currentDateTime + " " + reason + " " + failedBeatmap.setId() + " " + failedBeatmap.artistName() + " - " + failedBeatmap.songName() + "\n");
        }
    }
}
