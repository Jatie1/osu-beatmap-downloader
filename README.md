osu! Beatmap Downloader
=====

Beatmap downloader programmed in Java for the rhythm game osu!.

It is able to download all ranked beatmaps from a a given year to present.

Will soon have the ability to download DMCA'd maps, once chimu.moe get their shit together.

Created as a personal project to improve my coding skills, don't expect quality code.

## Running

### NOTE: Requires osu!supporter!

Make sure you have Java installed: https://www.java.com/en/download/

Download the .zip file in the Releases section, extract and run the run.bat file.
Make sure to read the disclaimer when you run the program for the first time!

If the .bat file is not working for some reason, you must do the following steps to run the program:
- Open command prompt
- Navigate to the location of the BeatmapDownloader.jar file using the 'cd' command
- Type this command to run the file: 'java -jar BeatmapDownloader.jar'

## Changelog

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
