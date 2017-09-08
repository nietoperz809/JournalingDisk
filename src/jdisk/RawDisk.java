package jdisk;

import java.io.*;

public class RawDisk
{
    private final Sector[] sectors;
    private final int sectorSize;

    /**
     * Constructor
     * @param numsectors number of sectors
     * @param sectorsize size of sector
     */
    public RawDisk (int numsectors, int sectorsize)
    {
        sectors = new Sector[numsectors];
        sectorSize = sectorsize;
        for (int s=0; s<numsectors; s++)
            sectors[s] = new Sector(sectorsize);
    }

    /**
     * Constructor with sectorsize=512
     * @param numsectors
     */
    RawDisk (int numsectors)
    {
        this(numsectors, 512);
    }

    /**
     * Constructor for 1.44MB disk
     */
    protected RawDisk ()
    {
        this(2880, 512);
    }

    /**
     * Read one sector
     * @param num logical sector number
     * @return
     */
    public byte[] read(int num)
    {
        return sectors[num].read();
    }

    /**
     * Access to internal buffer of sector
     * @param num logical sector number
     * @return
     */
    byte[] getInternalBuffer (int num)
    {
        return sectors[num].getInternalBuffer();
    }

    public void write (int num, byte[] data)
    {
        sectors[num].write(data);
    }

    /**
     * Writes byte array to multiple sectors
     * @param start first sector
     * @param data to be written
     */
    public void writeSectors (int start, byte[] data)
    {
        int ptr=0;
        while (ptr < data.length)
        {
            int num = data.length-ptr;
            if (num > sectorSize)
                num = sectorSize;
            byte[] buff = new byte[num];
            System.arraycopy(data, ptr, buff, 0,num);
            write(start++, buff);
            ptr += num;
        }
    }

    public byte[] readSectors (int start, int num)
    {
        byte[] ret = new byte[num*sectorSize];
        for (int s=0; s<num; s++)
        {
            byte[] b = getInternalBuffer(start+s);
            System.arraycopy(b,0,ret,s*sectorSize,sectorSize);
        }
        return ret;
    }

    private void writeByte (int address, byte b)
    {
        int sect = address/sectorSize;
        int off = address%sectorSize;
        byte[] buff = getInternalBuffer(sect);
        buff[off] = b;
    }

    protected void fillArea (byte b, int address, int length)
    {
        for (int s=0; s<length; s++)
        {
            writeByte(address++, b);
        }
    }

    private byte readByte (int address)
    {
        int sect = address/sectorSize;
        int off = address%sectorSize;
        byte[] buff = getInternalBuffer(sect);
        return buff[off];
    }

    public byte[] readBytes (int address, int num)
    {
        byte[] ret = new byte[num];
        for (int s=0; s<num; s++)
            ret[s] = readByte(address++);
        return ret;
    }

    public void writeBytes (int address, byte[] bytes)
    {
        for (byte b : bytes)
            writeByte (address++, b);
    }

    public void toFile (String name) throws IOException
    {
        FileOutputStream fs = new FileOutputStream(new File(name));
        BufferedOutputStream bs = new BufferedOutputStream(fs);
        for (Sector sector : sectors)
        {
            bs.write(sector.getInternalBuffer());
        }
        bs.close();
    }

    public void fromFile (String name) throws IOException
    {
        FileInputStream fs = new FileInputStream(new File(name));
        for (Sector sector : sectors)
        {
            byte[] dest = sector.getInternalBuffer();
            if (fs.read(dest) == -1)
                break;
        }
        fs.close();
    }

}
