package disk144;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static disk144.Constants.*;

/**
 * Implements handling of FAT part of the disk
 */
final class Fat12
{
    private final byte[] theFAT;
    private final Disk144 disk;
    private final Fat12Entry fatEntry;

    public Fat12 (Disk144 disk144)
    {
        disk = disk144;
        theFAT = disk144.readSectors(FAT1_STARTSECTOR, FAT_NUMSECTORS);
        fatEntry = new Fat12Entry(theFAT);
    }

    public byte[] getFatArray ()
    {
        return theFAT;
    }

    private int getFatEntryValue (int idx)
    {
        return fatEntry.getFatEntryValue(idx);
    }

    void setFatEntryValue (int idx, int v)
    {
        fatEntry.setFatEntryValue(idx, v);
    }

    public void close ()
    {
        disk.writeSectors(FAT1_STARTSECTOR, theFAT);
        disk.writeSectors(FAT2_STARTSECTOR, theFAT); // create 2nd FAT
    }

    public ArrayList<Integer> getFreeEntryList (int needed)
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (int s = 2; s<= MAXENTRY_1440KB; s++) // Start with 2
        {
            if (getFatEntryValue(s) == FREE_SLOT)
                list.add(s);
            if (list.size() == needed)
                return list;
        }
        throw new RuntimeException("Insufficient Disk Space");
    }

    /**
     * Read file data of a file that is on the disk
     * @param de DirEntry alread in Directory
     * @return DynArray filled witd file data
     * @throws Exception
     */
    public ByteArrayOutputStream getFile (DirectoryEntry de) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int blocks = (int)de.getFileSize() / CLUSTERSIZE;
        int remainder = (int)de.getFileSize() % CLUSTERSIZE;

        int cluster = de.getFirstCluster();
        byte[] bytes;
        for (int s=0; s<blocks; s++)
        {
            bytes = disk.read(cluster+ DATAOFFSET);
            out.write(bytes);
            cluster = getFatEntryValue(cluster);
        }
        if (remainder != 0)
        {
            bytes = disk.read(cluster+ DATAOFFSET);
            out.write(bytes,0,remainder);
        }
        return out;
    }

    public void deleteFile (DirectoryEntry de)
    {
        SplitHelper sh = new SplitHelper(de.getFileSize(), CLUSTERSIZE);
        int cluster = de.getFirstCluster();
        for (int s=0; s<sh.getTotalblocks(); s++)
        {
            int next = getFatEntryValue(cluster);
            setFatEntryValue(cluster, 0);
            cluster = next;
        }
    }

}
