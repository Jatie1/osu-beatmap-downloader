package com.jatie;

import java.util.Scanner;

public class OnScreen {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void disclaimer() {
        System.out.println("Welcome to Jatie's osu! Beatmap Downloader! Things to make sure of before running the program:\n");
        System.out.println("1. Make sure you have osu! supporter! The program relies on osu!direct.");
        System.out.println("2. Make sure the osu! client is open before starting downloads.");
        System.out.println("3. Stay on the main menu while downloading beatmaps.");
        System.out.println("4. If you have been playing for a while, I recommend to restart the osu! client to refresh the osu.db file.");
        System.out.println("5. Ensure that you have the 'Prefer no-video downloads' option enabled in the osu! settings. This will force osu!direct to download beatmaps without videos.");
        System.out.println("6. It is possible to abort the program while downloads are in progress. However, before you re-run the program you must import all of the downloaded beatmaps and restart the osu! client.");
        System.out.println("7. If you don't follow these guidelines strictly, you will get no support from me!\n");
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
