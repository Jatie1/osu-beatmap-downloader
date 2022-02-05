osu! Beatmap Downloader
=====

Beatmap downloader programmed in Java for the rhythm game osu!.

Has the ability to download all ranked beatmaps within a given year range, and will scan the maps you already have to eliminate duplicate downloads.

Will soon have the ability to download DMCA'd maps, once chimu.moe get their shit together.

Created as a personal project to improve my coding skills, don't expect quality code.

## Running

### NOTE: Requires osu!supporter! This application utilizes osu!direct.

Make sure you have Java installed: https://www.java.com/en/download/

Download the .zip file in the Releases section, extract and run the run.bat file.
Make sure to read the disclaimer when you run the program for the first time!

If the .bat file is not working for some reason, you must do the following steps to run the program:
- Open command prompt
- Navigate to the location of the BeatmapDownloader.jar file using the 'cd' command
- Type this command to run the file: 'java -jar BeatmapDownloader.jar'

## Changelog

### 1.1.1 - 06/02/2022

- Will delete all files in the Downloads folder before starting downloads (these are usually incomplete downloads that get stuck in the folder)

### 1.1 - 06/02/2022

- Added the ability to fetch maps from a year range (EG from 2013 to 2020) instead of just a year to present time
- Added a timer showing how long all downloads took in hours, minutes and seconds. Displayed after all downloads are completed.
- Doesn't instantly quit the program when all downloads are done, and instead prompts the user to press enter
- Changed DMCA tag in failedbeatmaps.txt to DMCA/BROKEN, as some maps aren't actually skipped for DMCA reasons

### 1.0.3 - 05/02/2022

- Validate beatmapdownloader.cfg before reading it
- Fixed rare crashes when osu! folder is missing required files / directories
- Spaced out segments of the text output to make it more readable
- Various text additions / changes / fixes

### 1.0.2 - 04/02/2022

- Fixed bug in failedbeatmaps.txt writer showing DMCAFAILURE for every failed map
- Create empty failedbeatmaps.txt even if no maps fail
- Made some text more descriptive
- Replace all instances of 'maps' with 'beatmaps'
- Formatting fixes

### 1.0.1 - 04/02/2022

- Fixed crash when closing the program during config file setup, then re-opening the program
- Expanded disclaimer at the start
- Better text feedback during config file setup
- Added text in failedbeatmaps.txt writer to differentiate between osu!direct timeout and DMCA'd / broken maps that chimu will download when they are working again (will likely be temporary)

### 1.0.0 - 03/02/2022

- Initial release
