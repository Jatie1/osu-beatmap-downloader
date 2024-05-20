package com.jatie;

import com.jatie.configfile.ConfigFileManager;

public class BeatmapDownloader {

    public static void main(String[] args) throws Exception {
        OnScreen.disclaimer();
        ConfigFileManager.setConfigFile();
    }
}