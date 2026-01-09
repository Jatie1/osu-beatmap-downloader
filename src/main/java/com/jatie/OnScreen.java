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
            if (SCANNER.nextLine().trim().equals("y")) {
                return;
            }
            System.out.println("\nRead the disclaimer properly!");
        }
    }

    public static String[] userEnterYearRange() {
        int currentYear = LocalDate.now().getYear();

        while (true) {
            System.out.println("Enter either a single year or a year range you want to begin fetching beatmaps from (between 2007 to " + currentYear + " in the format 'YYYY' for single year and 'YYYY-YYYY' for a range)");
            System.out.print("EG: '2019' will fetch all beatmaps from 2019 and '2013-2020' will fetch all beatmaps between 2013 and 2020: ");
            String input = SCANNER.nextLine().trim();

            // Code isn't clean here but whatever, it works.
            if (input.matches("\\d{4}-\\d{4}")) { // Range
                String[] parts = input.split("-", 2);
                int yearStart = Integer.parseInt(parts[0]);
                int yearEnd = Integer.parseInt(parts[1]);
                if (yearStart >= 2007 && yearEnd <= currentYear && yearEnd > yearStart) {
                    return new String[]{parts[0] + "-01-01", Integer.toString(yearEnd + 1)};
                }
            } else if (input.matches("\\d{4}")) { // Single year
                int year = Integer.parseInt(input);
                if (year >= 2007 && year <= currentYear) {
                    return new String[]{input + "-01-01", Integer.toString(year + 1)};
                }
            }

            System.out.println("Year input is invalid!");
        }
    }

    public static boolean userEnterRankedStatusPreference(String rankedStatus) {
        while (true) {
            System.out.print("Would you like to download " + rankedStatus + " beatmaps? (y/n): ");
            switch (SCANNER.nextLine().trim()) {
                case "y":
                    return true;
                case "n":
                    return false;
            }
            System.out.println("Invalid input!");
        }
    }
}
