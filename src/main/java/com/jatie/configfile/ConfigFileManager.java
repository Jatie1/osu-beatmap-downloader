package com.jatie.configfile;

import com.jatie.Validator;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

public class ConfigFileManager {
    private static final Scanner SCANNER = new Scanner(System.in);
    public static final File CONFIG_FILE_NAME = new File("beatmapdownloader.cfg");

    public static ConfigFile setConfigFile() throws IOException {
        if (!CONFIG_FILE_NAME.exists()) {
            System.out.println("\nConfiguration file does not exist! Creating a new one.");
            return createConfigFile();
        }
        System.out.println("\nReading properties from config file...");
        ConfigFile configFile = readConfigFile();
        if (!Validator.validateConfigFile(configFile)) {
            System.out.println("\nConfiguration file is corrupted! Creating a new one.");
            return createConfigFile();
        }
        return configFile;
    }

    public static ConfigFile createConfigFile() throws IOException {
        Properties properties = new Properties();

        ConfigFile configFile = new ConfigFile();
        configFile.setApiKey(getApiKey());
        configFile.setOsuDirectory(getOsuDirectory());
        String[] loginDetails = getLoginDetails();
        configFile.setUsername(loginDetails[0]);
        configFile.setPassword(loginDetails[1]);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_NAME)) {
            properties.setProperty("apiKey", configFile.getApiKey());
            properties.setProperty("osuDirectory", configFile.getOsuDirectory());
            properties.setProperty("username", configFile.getUsername());
            properties.setProperty("password", configFile.getPassword());
            properties.store(fos, "For Jatie's osu! Beatmap Downloader");
        }
        return configFile;
    }

    public static ConfigFile readConfigFile() throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_NAME)) {
            properties.load(fis);
            String apiKey = properties.getProperty("apiKey");
            String osuDirectory = properties.getProperty("osuDirectory");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            return new ConfigFile(apiKey, osuDirectory, username, password);
        }
    }

    public static String getApiKey() throws IOException {
        while (true) {
            System.out.print("\nEnter osu! legacy API key (get it from here https://osu.ppy.sh/home/account/edit#legacy-api): ");
            String apiKey = SCANNER.nextLine();
            System.out.println("Validating API key...");
            if (Validator.validateApiKey(apiKey)) {
                System.out.println("API key is valid!");
                return apiKey;
            }
            System.out.println("API key is invalid!");
        }
    }
    public static String getOsuDirectory() {
        while (true) {
            System.out.print("\nEnter osu! folder location: ");
            String osuDirectory = SCANNER.nextLine();
            if (Validator.validateOsuDirectory(osuDirectory)) {
                System.out.println("Location is valid!");
                return osuDirectory;
            }
            System.out.println("Location is invalid! Must be in a similar format to 'C:\\Program Files\\osu!'");
        }
    }

    public static String[] getLoginDetails() {
        while (true) {
            System.out.print("\nEnter osu! username: ");
            String username = SCANNER.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username is invalid!");
                continue;
            }

            System.out.print("Enter osu! password: ");
            String password = SCANNER.nextLine();
            System.out.println("Verifying login details...");
            if (Validator.validateLoginDetails(username, password)) {
                System.out.println("Login details are valid!");
                return new String[] {username, password};
            }
        }
    }
}
