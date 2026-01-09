package com.jatie;

import com.jatie.entity.ConfigFileProperties;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class OsuDatabaseReader {
    public static Set<Integer> getBeatmapSetIdsFromDBFile(ConfigFileProperties configFileProperties) throws IOException {
        File osuDBFile = new File(configFileProperties.osuDirectory(), "osu!.db");

        System.out.println("\nScanning osu!.db file...");

        Set<Integer> beatmapSetIds = new HashSet<>();
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(osuDBFile)))) {
            dis.skipBytes(17);
            BinaryReader.skipString(dis);
            int numberOfBeatmaps = BinaryReader.readInt(dis);

            for (int i = 1; i <= numberOfBeatmaps; i++) {
                if (i % 100 == 0) {
                    System.out.print("Scanning beatmap " + i + " of " + numberOfBeatmaps + "...\r");
                }
                BinaryReader.skipString(dis, 9);
                dis.skipBytes(39);
                for (int j = 0; j < 4; j++) {
                    dis.skipBytes(BinaryReader.readInt(dis) * 10);
                }
                dis.skipBytes(12);
                dis.skipBytes(BinaryReader.readInt(dis) * 17 + 4);
                beatmapSetIds.add(BinaryReader.readInt(dis));
                dis.skipBytes(15);
                BinaryReader.skipString(dis, 2);
                dis.skipBytes(2);
                BinaryReader.skipString(dis);
                dis.skipBytes(10);
                BinaryReader.skipString(dis);
                dis.skipBytes(18);
            }
            System.out.println("Finished scanning " + beatmapSetIds.size() + " beatmap sets from " + numberOfBeatmaps + " beatmaps!\n");
        }
        return beatmapSetIds;
    }
}
