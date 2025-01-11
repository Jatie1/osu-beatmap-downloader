package com.jatie.configfile;

import com.jatie.OnScreen;
import com.jatie.Validator;
import com.jatie.entity.ConfigFileProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileManager {
    private static final File CONFIG_FILE = new File("beatmapdownloader.cfg");

    public static ConfigFileProperties manageConfigFile() throws Exception {
        if (!CONFIG_FILE.exists()) {
            System.out.println("\nConfiguration file does not exist! Ensure your config file exists and is accessible by external programs. Press enter to re-scan for the config file.");
            OnScreen.SCANNER.nextLine();
        }
        System.out.println("\nReading properties from config file...");
        ConfigFileProperties configFileProperties = readConfigFile();
        if (!Validator.validateConfigFile(configFileProperties)) {
            System.out.println("\nConfiguration file properties are not valid! Ensure your config file has the required entries and values. Press enter to re-scan the config file.");
            OnScreen.SCANNER.nextLine();
        }
        return configFileProperties;
    }

    public static ConfigFileProperties readConfigFile() throws IOException {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.load(fis);
            return new ConfigFileProperties(
                    properties.getProperty("apiKey"),
                    properties.getProperty("osuDirectory"),
                    properties.getProperty("sessionCookie"));
        }
    }
}
