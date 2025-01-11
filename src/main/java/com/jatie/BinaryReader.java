package com.jatie;

import java.io.DataInputStream;
import java.io.IOException;

public class BinaryReader {

    public static int readInt(final DataInputStream is) throws IOException {
        return Integer.reverseBytes(is.readInt());
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
            if ((b & 0x80) == 0) {
                return count;
            }
            shift += 7;
        }
    }
}
