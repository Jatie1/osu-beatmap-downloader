package com.jatie;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * osu!.db is a C# binary file. Java binary files are formatted differently.
 * This class can read required information from a C# style binary file.
 */
public class BinaryReader {
    public static int readInt(final DataInputStream dis) throws IOException {
        return Integer.reverseBytes(dis.readInt());
    }

    public static void skipString(final DataInputStream dis) throws IOException {
        if (dis.read() == 0) {
            return;
        }
        dis.skip(getStringLength(dis));
    }

    public static void skipString(final DataInputStream dis, int num) throws IOException {
        for (; num > 0; num--) {
            skipString(dis);
        }
    }

    public static int getStringLength(final DataInputStream dis) throws IOException {
        int count = 0;
        int shift = 0;

        while (true) {
            int b = dis.read();
            count |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return count;
            }
            shift += 7;
        }
    }

//    public static String readString(final DataInputStream dis) throws IOException {
//        if (dis.read() == 0) {
//            return "";
//        }
//        int val = getStringLength(dis);
//
//        byte[] buffer = new byte[val];
//        if (dis.read(buffer) < 0) {
//            throw new IOException("EOF");
//        }
//        return new String(buffer);
//    }
}
