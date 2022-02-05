package com.jatie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeatmapDownloader {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Set<Integer> BROKEN_BEATMAPS = Stream.of(1054045, 956145, 236280, 222253, 306693, 290262, 88835, 42110, 27482, 951, 687, 913, 83, 562, 664, 703, 716).collect(Collectors.toCollection(HashSet::new));

    private static String apiKey;
    private static String path;

    public static void main(String[] args) {
        System.out.println("Welcome to Jatie's osu! Beatmap Downloader! Things to make sure of before running the program:\n");
        System.out.println("1. Make sure you have osu! supporter! The program relies on osu!direct.");
        System.out.println("2. Make sure the osu! client is open before starting downloads.");
        System.out.println("3. Stay on the main menu while downloading beatmaps.");
        System.out.println("4. If you have been playing for a while, I recommend to restart the osu! client to refresh the osu.db file.");
        System.out.println("5. Ensure that you have the 'Prefer no-video downloads' option enabled in the osu! settings. This will force osu!direct to download beatmaps without videos.");
        System.out.println("6. It is possible to abort the program while downloads are in progress. However, before you re-run the program you must import all of the downloaded beatmaps and restart the osu! client.");
        System.out.println("7. If you don't follow these guidelines strictly, you will get no support from me!\n");
        startingConfirmation();
        configFile();
        Set<Integer> userBeatmaps = getBeatmapSetIdsFromDatabaseFile();
        String[] dateRange = enterYearRange();
        boolean includeRankedMaps = enterRankedStatusPreference("ranked");
        boolean includeApprovedMaps = enterRankedStatusPreference("approved");
        boolean includeQualifiedMaps = enterRankedStatusPreference("qualified");
        boolean includeLovedMaps = enterRankedStatusPreference("loved");
        Set<Beatmap> allBeatmaps = getAllBeatmaps(dateRange, includeRankedMaps, includeApprovedMaps, includeQualifiedMaps, includeLovedMaps);
        allBeatmaps = getMissingBeatmaps(userBeatmaps, allBeatmaps);
        preDownloadActivities();
        downloadBeatmaps(allBeatmaps);
    }

    public static void writeFailedBeatmap(Beatmap failedBeatmap, boolean chimu) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("failedbeatmaps.txt", true))) {
            if (chimu) {
                bw.write("DMCA/BROKEN " + failedBeatmap.getSetId() + " " + failedBeatmap.getArtistName() + " - " + failedBeatmap.getSongName() + "\n");
            } else {
                bw.write("TIMEOUT " + failedBeatmap.getSetId() + " " + failedBeatmap.getArtistName() + " - " + failedBeatmap.getSongName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadBeatmaps(Set<Beatmap> missingBeatmaps) {
        System.out.println("\nStarting downloads!");

        long startTime = System.currentTimeMillis();
        int count = 1;
        for (Beatmap beatmap : missingBeatmaps) {
            if (!beatmap.isRemoved() && !BROKEN_BEATMAPS.contains(beatmap.getSetId())) {
                System.out.println("(" + count++ + "/" + missingBeatmaps.size() + ") Downloading " + beatmap.getSetId() + " " + beatmap.getArtistName() + " - " + beatmap.getSongName() + " through osu!direct");
                downloadDirect(beatmap);
            } else {
                System.out.println("(" + count++ + "/" + missingBeatmaps.size() + ") Beatmap " + beatmap.getSetId() + " " + beatmap.getArtistName() + " - " + beatmap.getSongName() + " cannot be downloaded because chimu.moe is broken, writing to failedbeatmaps.txt");
                writeFailedBeatmap(beatmap, true);
            }
        }
        long totalSecs = (System.currentTimeMillis() - startTime) / 1000;
        long hours = totalSecs / 3600;
        long minutes = totalSecs / 60 % 60;
        long seconds = totalSecs % 60;

        System.out.println("\nCompleted all downloads in " + hours + "h " + minutes + "m " + seconds + "s! Check failedbeatmaps.txt for all beatmaps that couldn't be downloaded!");
    }

    public static void downloadDirect(Beatmap beatmap) {
        File downloads = new File(path + "\\Downloads");
        if (!downloads.exists()) {
            downloads.mkdir();
        }
        try {
            Runtime.getRuntime().exec(new String[]{path + "\\osu!.exe", "osu://s/" + beatmap.getSetId()});
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis();
        while (downloads.list().length == 0) {
            if (System.currentTimeMillis() - time > 30000) {
                System.out.println("FAILURE: Timeout length exceeded! Skipping " + beatmap.getSetId() + " " + beatmap.getArtistName() + " - " + beatmap.getSongName() + "...");
                writeFailedBeatmap(beatmap, false);
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (downloads.list().length != 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void preDownloadActivities() {
        // osu! client open check
        while (!checkOsuOpen()) {
            System.out.print("\nWhy is the osu! client not open? Did you read the disclaimer? Open the client and press enter to continue.");
            SCANNER.nextLine();
        }
        // Delete old failedbeatmaps.txt if exists, then create blank new one
        File failedBeatmaps = new File("failedbeatmaps.txt");
        failedBeatmaps.delete();
        try {
            failedBeatmaps.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkOsuOpen() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"tasklist.exe", "/fo", "csv", "/nh"}).getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("\"osu!.exe\"")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Set<Beatmap> getMissingBeatmaps(Set<Integer> userBeatmaps, Set<Beatmap> allBeatmaps) {
        Set<Beatmap> missingMaps = new HashSet<>();
        for (Beatmap beatmap : allBeatmaps) {
            if (!userBeatmaps.contains(beatmap.getSetId())) {
                missingMaps.add(beatmap);
            }
        }
        return missingMaps;
    }

    public static Set<Beatmap> getAllBeatmaps(String[] dateRange, boolean includeRankedMaps, boolean includeApprovedMaps, boolean includeQualifiedMaps, boolean includeLovedMaps) {
        System.out.println("\nFetching beatmap information from osu! API...");
        Set<Beatmap> beatmapSets = new HashSet<>();
        outerloop: // I hate this code
        while (true) {
            String jsonText = null;
            try (InputStream is = new URL("https://osu.ppy.sh/api/get_beatmaps?k=" + apiKey + "&m=0&since=" + dateRange[0]).openStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                jsonText = br.lines().collect(Collectors.joining());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (jsonText.length() == 2) { // No more beatmaps left, gives an empty json array '[]'
                break;
            }
            JSONArray json = new JSONArray(jsonText);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                Beatmap beatmap = new Beatmap(jsonObject.getInt("beatmapset_id"), jsonObject.getString("artist"), jsonObject.getString("title"), jsonObject.getInt("audio_unavailable") == 1 || jsonObject.getInt("download_unavailable") == 1);
                if (jsonObject.getString("approved_date").substring(0, 4).equals(dateRange[1])) {
                    break outerloop; // Breaking out of nested loops sucks, but I don't care enough to fix
                }
                switch (jsonObject.getInt("approved")) {
                    case 1:
                        if (includeRankedMaps) {
                            beatmapSets.add(beatmap);
                        }
                        break;
                    case 2:
                        if (includeApprovedMaps) {
                            beatmapSets.add(beatmap);
                        }
                        break;
                    case 3:
                        if (includeQualifiedMaps) {
                            beatmapSets.add(beatmap);
                        }
                        break;
                    case 4:
                        if (includeLovedMaps) {
                            beatmapSets.add(beatmap);
                        }
                        break;
                }
            }
            dateRange[0] = json.getJSONObject(json.length() - 1).getString("approved_date");
            System.out.print(beatmapSets.size() + " beatmaps fetched from the API...\r");
        }
        System.out.println(beatmapSets.size() + " total beatmaps fetched from the API!");
        return beatmapSets;
    }

    public static Set<Integer> getBeatmapSetIdsFromDatabaseFile() {
        Set<Integer> beatmapSetIds = new HashSet<>();
        File dbFile = new File(path + "\\osu!.db");
        while (!dbFile.exists()) {
            System.out.print("\nThe osu!.db file doesn't exist in your osu! folder! Ensure that the file exists and press enter to re-scan for it.");
            SCANNER.nextLine();
        }
        System.out.println("\nScanning osu!.db file...");
        try (FileInputStream f = new FileInputStream(dbFile); DataInputStream d = new DataInputStream(f)) {
            d.skip(17);
            BinaryReader.skipString(d);
            int numberBeatmaps = BinaryReader.readInt(d);

            for (int i = 1; i < numberBeatmaps + 1; i++) {
                System.out.print("Scanning beatmap " + i + " of " + numberBeatmaps + "...\r");
                BinaryReader.skipString(d, 9);
                d.skip(39);
                for (int j = 0; j < 4; j++) {
                    d.skip(BinaryReader.readInt(d) * 14L);
                }
                d.skip(12);
                d.skip(BinaryReader.readInt(d) * 17L + 4);
                beatmapSetIds.add(BinaryReader.readInt(d));
                d.skip(15);
                BinaryReader.skipString(d, 2);
                d.skip(2);
                BinaryReader.skipString(d);
                d.skip(10);
                BinaryReader.skipString(d);
                d.skip(18);
            }
            System.out.println("Finished scanning " + beatmapSetIds.size() + " beatmap sets from " + numberBeatmaps + " beatmaps!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return beatmapSetIds;
    }

    public static boolean enterRankedStatusPreference(String status) {
        while (true) {
            System.out.print("Would you like to download " + status + " beatmaps? (y/n): ");
            String result = SCANNER.nextLine();
            if (result.equals("y")) {
                return true;
            }
            if (result.equals("n")) {
                return false;
            }
            System.out.println("Invalid input!");
        }
    }

    public static String[] enterYearRange() {
        while (true) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            System.out.println("Enter the year range you want to begin fetching beatmaps from (between 2007 to " + currentYear + ")");
            System.out.print("EG '2013-2020' will fetch all beatmaps between 2013 and 2020: ");
            String yearRange = SCANNER.nextLine();
            if (yearRange.matches("[\\d]{4}-[\\d]{4}")) {
                String[] yearRangeSplit = yearRange.split("-");
                int yearStart = Integer.parseInt(yearRangeSplit[0]);
                int yearEnd = Integer.parseInt(yearRangeSplit[1]);
                if (yearStart >= 2007 && yearEnd <= currentYear && yearEnd >= yearStart) {
                    yearRangeSplit[0] += "-01-01";
                    yearRangeSplit[1] = Integer.toString(yearEnd + 1);
                    return yearRangeSplit;
                }
            }
            System.out.println("Year range entered is invalid!");
        }
    }

    public static void configFile() {
        File configFile = new File("beatmapdownloader.cfg");
        if (!configFile.exists()) {
            System.out.println("\nConfiguration file does not exist! Creating a new one.");
            createConfigFile(configFile);
            return;
        }
        if (!readConfigFile(configFile)) {
            System.out.println("\nConfiguration file is corrupted! Creating a new one.");
            createConfigFile(configFile);
        }
    }

    public static void createConfigFile(File configFile) {
        apiKey = enterApiKey();
        path = enterOsuDirectory();
        try (FileWriter fw = new FileWriter(configFile)) {
            fw.write("apikey=" + apiKey + "\n");
            fw.write("path=" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean readConfigFile(File configFile) {
        Properties properties = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            properties.load(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String thisApiKey = properties.getProperty("apikey");
        String thisPath = properties.getProperty("path");
        if (thisApiKey != null && thisPath != null && validateApiKey(thisApiKey) && validateOsuDirectory(thisPath)) {
            apiKey = thisApiKey;
            path = thisPath;
            return true;
        }
        return false;
    }

    public static String enterApiKey() {
        while (true) {
            System.out.print("\nEnter osu! API key: ");
            String thisApiKey = SCANNER.nextLine();
            System.out.println("Validating API key...");
            if (validateApiKey(thisApiKey)) {
                System.out.println("API key is valid!");
                return thisApiKey;
            }
            System.out.println("API key is invalid!");
        }
    }

    public static boolean validateApiKey(String thisApiKey) {
        try {
            URL url = new URL("https://osu.ppy.sh/api/get_beatmaps?k=" + thisApiKey + "&s=1");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            if (httpConn.getResponseCode() != 401) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String enterOsuDirectory() {
        while (true) {
            System.out.print("\nEnter osu! folder location: ");
            String thisPath = SCANNER.nextLine();
            if (validateOsuDirectory(thisPath)) {
                System.out.println("Location is valid!");
                thisPath = thisPath.replace("\\", "\\\\");
                return thisPath;
            }
            System.out.println("Location is invalid! Must be in a similar format to 'C:\\Program Files\\osu!'");
        }
    }

    public static boolean validateOsuDirectory(String thisPath) {
        File songsFolder = new File(thisPath + "\\Songs");
        return songsFolder.isDirectory() && thisPath.matches("(?:[^\\\\]+\\\\)+osu!$");
    }

    public static void startingConfirmation() {
        while (true) {
            System.out.print("If you have read the above disclaimer, type 'y' to continue: ");
            String result = SCANNER.nextLine();
            if (result.equals("y")) {
                return;
            }
            System.out.println("\nRead the disclaimer properly!");
        }
    }
}