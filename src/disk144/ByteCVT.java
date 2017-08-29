/*
 
 */
package disk144;

import java.math.BigInteger;

/**
 * Little/Big Endian byte access
 *
 * @author Administrator
 */
public class ByteCVT
{
    private static final BigInteger bi256 = BigInteger.valueOf(256);

    /**
     * Read little endian 16 bit value
     *
     * @param b      source array
     * @param offset offset in source array
     * @return integer result
     */
    public static int fromLE16 (byte[] b, int offset)
    {
        return (b[offset]&0xff) |
                ((b[offset + 1]&0xff) << 8);
    }

    public static int fromLE24 (byte[] b, int offset)
    {
        return (b[offset]&0xff) |
                ((b[offset + 1]&0xff) << 8) |
                ((b[offset + 2]&0xff) << 16);
    }

    /**
     * Read little endian 32 bit value
     *
     * @param b      source array
     * @param offset offset in source array
     * @return integer result
     */
    public static long fromLE32 (byte[] b, int offset)
    {
        return (b[offset]&0xff) |
                ((b[offset + 1]&0xff) << 8) |
                ((b[offset + 2]&0xff) << 16) |
                ((b[offset + 3]&0xff) << 24);
    }

    /**
     * Read big endian 16 bit value
     *
     * @param b      source array
     * @param offset offset in source array
     * @return integer result
     */
    public static int fromBE16 (byte[] b, int offset)
    {
        return (b[offset + 1]&0xff) |
                ((b[offset]&0xff) << 8);
    }

    public static int fromBE24 (byte[] b, int offset)
    {
        return (b[offset + 2]&0xff) |
                ((b[offset + 1]&0xff) << 8) |
                ((b[offset]&0xff) << 16);
    }

    /**
     * Read big endian 32 bit value
     *
     * @param b      source array
     * @param offset offset in source array
     * @return integer result
     */
    public static long fromBE32 (byte[] b, int offset)
    {
        return (b[offset + 3]&0xff) |
                ((b[offset + 2]&0xff) << 8) |
                ((b[offset + 1]&0xff) << 16) |
                ((b[offset]&0xff) << 24);
    }

/////////////////////////////////////////////////////////////////////////    

    /**
     * Put 16 bit little endian value
     *
     * @param val    value
     * @param b      destination array
     * @param offset offset in dest array
     */
    public static void toLE16 (int val, byte[] b, int offset)
    {
        b[offset] = (byte) val;
        b[offset + 1] = (byte) (val >>> 8);
    }

    /**
     * Put 16 bit big endian value
     *
     * @param val    value
     * @param b      destination array
     * @param offset offset in dest array
     */
    public static void toBE16 (int val, byte[] b, int offset)
    {
        b[offset + 1] = (byte) val;
        b[offset] = (byte) (val >>> 8);
    }

    /**
     * Put 32 bit little endian value
     *
     * @param val    value
     * @param b      destination array
     * @param offset offset in dest array
     */
    public static void toLE32 (long val, byte[] b, int offset)
    {
        b[offset] = (byte) val;
        b[offset + 1] = (byte) (val >>> 8);
        b[offset + 2] = (byte) (val >>> 16);
        b[offset + 3] = (byte) (val >>> 24);
    }

    /**
     * Put 32 bit big endian value
     *
     * @param val    value
     * @param b      destination array
     * @param offset offset in dest array
     */
    public static void toBE32 (long val, byte[] b, int offset)
    {
        b[offset + 3] = (byte) val;
        b[offset + 2] = (byte) (val >>> 8);
        b[offset + 1] = (byte) (val >>> 16);
        b[offset] = (byte) (val >>> 24);
    }

/////////////////////////////////////////////////////////////////////////

    /**
     * Reads arbitrary length Big Endian value
     *
     * @param b      source array
     * @param offset offset into source
     * @param size   number of bytes
     * @return the value;
     */
    public static BigInteger readBE (byte[] b, int offset, int size)
    {
        BigInteger bi = BigInteger.valueOf(0);
        for (int s = 0; s < size; s++)
        {
            bi = bi.multiply(bi256);
            bi = bi.add(BigInteger.valueOf(b[offset + s]));
        }
        return bi;
    }

    /**
     * Read BE bytes into double array
     * @param inputBytes array of bytes
     * @param bytesPerFrame size of frame, 1...8
     * @return newly created double array
     */
    public static long[] readBE (byte[] inputBytes, int bytesPerFrame)
    {
        int numframes = inputBytes.length/bytesPerFrame;
        long[] outputWords = new long[numframes];
        for (int i=0; i<inputBytes.length; i+=bytesPerFrame)
        {
            long n = 0;
            for (int s = 0; s < bytesPerFrame; s++)
            {
                n = (n * 256) + (inputBytes[i+s]);
            }
            outputWords[i/bytesPerFrame] = n;
        }
        return outputWords;
    }

    /**
     * Read LE bytes into double array
     * @param inputBytes array of bytes
     * @param bytesPerFrame size of frame, 1...8
     * @return newly created double array
     */
    public static long[] readLE (byte[] inputBytes, int bytesPerFrame)
    {
        int numframes = inputBytes.length/bytesPerFrame;
        long[] outputWords = new long[numframes];
        for (int i=0; i<inputBytes.length; i+=bytesPerFrame)
        {
            long n = 0;
            for (int s = bytesPerFrame-1; s > -1; s--)
            {
                n = (n * 256) + (inputBytes[i+s]);
            }
            outputWords[i/bytesPerFrame] = n;
        }
        return outputWords;
    }



    /**
     * Reads arbitrary length Little Endian value
     *
     * @param b      source array
     * @param offset offset into source
     * @param size   number of bytes
     * @return the value;
     */
    public static BigInteger readLE (byte[] b, int offset, int size)
    {
        BigInteger bi = BigInteger.valueOf(0);
        offset += size - 1;
        for (int s = 0; s < size; s++)
        {
            bi = bi.multiply(bi256);
            bi = bi.add(BigInteger.valueOf(b[offset - s]));
        }
        return bi;
    }

    /**
     * Writes arbitrary length big endian value into byte array
     *
     * @param in     the value
     * @param b      destination array
     * @param offset offset into array
     * @param size   number of bytes used
     */
    public static void writeBE (BigInteger in, byte[] b, int offset, int size)
    {
        for (int s = 0; s < size; s++)
        {
            b[offset + s] = in.byteValue();
            in = in.divide(bi256);
        }
    }

    /**
     * Writes arbitrary length little endian value into byte array
     *
     * @param in     the value
     * @param dest      destination array
     * @param offset offset into array
     * @param size   number of bytes used
     */
    public static void writeLE (BigInteger in, byte[] dest, int offset, int size)
    {
        offset += size - 1;
        for (int s = 0; s < size; s++)
        {
            dest[offset - s] = in.byteValue();
            in = in.divide(bi256);
        }
    }

}
