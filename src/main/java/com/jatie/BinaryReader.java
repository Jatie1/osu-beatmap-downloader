package com.jatie;

import java.io.DataInputStream;
import java.io.IOException;

public class BinaryReader {
    public static int readInt(final DataInputStream is) throws IOException {
        int value = is.readInt();
        int b2 = value >> 8 & 0xff;
        int b3 = value >> 16 & 0xff;
        int b4 = value >> 24 & 0xff;

        return value << 24 | b2 << 16 | b3 << 8 | b4;
    }

    public static void skipString(final DataInputStream is) throws IOException {
        if (is.read() == 0) {
            return;
        }
        is.skip(getStringLength(is));
    }

    public static void skipString(final DataInputStream is, int num) throws IOException {
        for (; num > 0; num--) {
            skipString(is);
        }
    }

    public static int getStringLength(final DataInputStream is) throws IOException {
        int count = 0;
        int shift = 0;
        while (true) {
            int b = is.read();
            count |= (b & 0x7F) << shift;
            shift += 7;
            if ((b & 0x80) == 0) {
                return count;
            }
        }
    }
}
