package com.jatie;

import java.time.LocalDate;
import java.util.Scanner;

public class OnScreen {
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void disclaimer() {
        System.out.println("Welcome to Jatie's osu! Beatmap Downloader! Things to make sure of before running the program:\n");
        System.out.println("1. Make sure you have filled out the beatmapdownloader.cfg! Instructions on how to fill it out are provided in the file.");
        while (true) {
            System.out.print("If you have read the above disclaimer, type 'y' to continue: ");
            if (SCANNER.nextLine().equals("y")) {
                return;
            }
            System.out.println("\nRead the disclaimer properly!");
        }
    }

    public static int userEnterDownloadStrategy() {
        System.out.println("How would you like to download the beatmaps?");
        System.out.println("(1) Using the .");
        System.out.println("If you would like to learn more about how these methods work, type 'info'.");
        while (true) {
            String userInput = SCANNER.nextLine();
            switch (userInput) {
                case "1":
                    return 1;
                case "info":
                    showHelpDownloadStrategy();
            }
            System.out.println("\nNot a valid input!");
        }
    }

    public static void showHelpDownloadStrategy() {

    }

    public static String[] userEnterYearRange() {

        int currentYear = LocalDate.now().getYear();

        while (true) {
            System.out.println("Enter either a single year or a year range you want to begin fetching beatmaps from (between 2007 to " + currentYear + " in the format 'YYYY' for single year and 'YYYY-YYYY' for a range)");
            System.out.print("EG: '2019' will fetch all beatmaps from 2019 and '2013-2020' will fetch all beatmaps between 2013 and 2020: ");
            String userInput = SCANNER.nextLine();

            // Code isn't clean here but whatever, it works.
            if (userInput.matches("\\d{4}-\\d{4}")) { // Range
                String[] yearRangeSplit = userInput.split("-");
                int yearStart = Integer.parseInt(yearRangeSplit[0]);
                int yearEnd = Integer.parseInt(yearRangeSplit[1]);
                if (yearStart >= 2007 && yearEnd <= currentYear && yearEnd > yearStart) {
                    return new String[]{yearRangeSplit[0] + "-01-01", Integer.toString(yearEnd + 1)};
                }
            } else if (userInput.matches("\\d{4}")) { // Single year
                int year = Integer.parseInt(userInput);
                if (year >= 2007 && year <= currentYear) {
                    return new String[]{userInput + "-01-01", Integer.toString(year + 1)};
                }
            }

            System.out.println("Year input is invalid!");
        }
    }

    public static boolean userEnterRankedStatusPreference(String rankedStatus) {
        while (true) {
            System.out.print("Would you like to download " + rankedStatus + " beatmaps? (y/n): ");
            switch (SCANNER.nextLine()) {
                case "y":
                    return true;
                case "n":
                    return false;
            }
            System.out.println("Invalid input!");
        }
    }
}
