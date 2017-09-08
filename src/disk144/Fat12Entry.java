package disk144;

/**
 * FAT table entries are packed so that two cluster entries occupy
 * three bytes with the following general format:
 *
 * yz Zx XY
 *
 * where
 * xyz is the one pointer entry and
 * XYZ is the second pointer entry.
 */
final class Fat12Entry
{
    private final byte[] fat;

//    public Fat12Entry (Fat12 f)
//    {
//        this.fat = f.getFatArray();
//    }

    public Fat12Entry (byte[] f)
    {
        this.fat = f;
    }

    /**
     * Reads upper 4 bits of 12 bit value
     * @param b the 12 bits value
     * @return the upper 4 bits
     */
    private static int upper4 (int b)
    {
        return (b >> 8) & 0x0f;
    }

    /**
     * Reads middle 4 bits of 12 bit value
     * @param b the 12 bits value
     * @return the middle 4 bits
     */
    private static int middle4 (int b)
    {
        return (b >> 4) & 0x0f;
    }

    /**
     * Reads lower 4 bits of 12 bit value
     * @param b the 12 bits value
     * @return the lower 4 bits
     */
    private static int lower4 (int b)
    {
        return b & 0x0f;
    }

    /**
     * Build 12 bit value from 3*4 bits
     * @param low Lower bits
     * @param mid Middle bits
     * @param high Upper bits
     * @return A 12 bit value
     */
    private static int construct12 (int low, int mid, int high)
    {
        return (low | mid << 4 | high << 8) & 0xfff;
    }

    /**
     * Build 8 bit value from 2*4 bits
     * @param low Lower bits
     * @param high Upper bits
     * @return An 8 bit value
     */
    private static byte construct8 (int low, int high)
    {
        return (byte) ((low | high << 4) & 0xff);
    }

    /**
     * Reads compressed FAT12 entry
     * @param index entry index
     * @return the 12 bit value
     */
    public int getFatEntryValue (int index)
    {
        int loc = (3 * index) / 2;
        if ((index % 2) == 0)   // even
        {
            int a = lower4(fat[loc + 1]);
            int b = lower4(fat[loc]);
            int c = middle4(fat[loc]);
            return construct12(b, c, a);
        }
        int a = lower4(fat[loc + 1]);
        int b = middle4(fat[loc + 1]);
        int c = middle4(fat[loc]);
        return construct12(c, a, b);
    }

    /**
     * Writes compressed FAT12 value
     * @param index index of FAT entry
     * @param val The 12 bit value
     */
    public void setFatEntryValue (int index, int val)
    {
        int loc = (3 * index) / 2;
        if ((index % 2) == 0)   // even
        {
            int a = middle4(val);
            int b = lower4(val);
            int c = upper4(val);
            int d = middle4(fat[loc + 1]);
            fat[loc] = construct8(b, a);
            fat[loc + 1] = construct8(c, d);
        }
        else
        {
            int a = middle4(val);
            int b = lower4(val);
            int c = upper4(val);
            int d = lower4(fat[loc]);
            fat[loc + 1] = construct8(a, c);
            fat[loc] = construct8(d, b);
        }
    }
}
