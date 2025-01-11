package com.jatie;

import com.jatie.entity.ConfigFileProperties;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

public class OsuDatabaseReader {
    public static Set<Integer> getBeatmapSetIdsFromDBFile(ConfigFileProperties configFileProperties) throws Exception {
        File osuDBFile = new File(configFileProperties.osuDirectory() + File.separator + "osu!.db");

        System.out.println("\nScanning osu!.db file...");

        Set<Integer> beatmapSetIds = new HashSet<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(osuDBFile))) {
            dis.skip(17);
            BinaryReader.skipString(dis);
            int numberOfBeatmaps = BinaryReader.readInt(dis);

            for (int i = 1; i < numberOfBeatmaps + 1; i++) {
                System.out.print("Scanning beatmap " + i + " of " + numberOfBeatmaps + "...\r");
                BinaryReader.skipString(dis, 9);
                dis.skip(39);
                for (int j = 0; j < 4; j++) {
                    dis.skip(BinaryReader.readInt(dis) * 10L);
                }
                dis.skip(12);
                dis.skip(BinaryReader.readInt(dis) * 17L + 4);
                beatmapSetIds.add(BinaryReader.readInt(dis));
                dis.skip(15);
                BinaryReader.skipString(dis, 2);
                dis.skip(2);
                BinaryReader.skipString(dis);
                dis.skip(10);
                BinaryReader.skipString(dis);
                dis.skip(18);
            }
            System.out.println("Finished scanning " + beatmapSetIds.size() + " beatmap sets from " + numberOfBeatmaps + " beatmaps!\n");
        }
        return beatmapSetIds;
    }
}
