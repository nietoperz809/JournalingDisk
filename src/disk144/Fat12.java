package disk144;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static disk144.Constants.*;

/**
 * Implements handling of FAT part of the disk
 */
final class Fat12
{
    private final byte[] _fat;
    private final Disk144 _fmf;
    private final Fat12Entry _fatEntry;

    public Fat12 (Disk144 fmf) throws Exception
    {
        _fmf = fmf;
        _fat = fmf.readSectors(1, 9);
        _fatEntry = new Fat12Entry(this);
    }

    public byte[] getArray()
    {
        return _fat;
    }

    int getFatEntryValue (int idx)
    {
        return _fatEntry.getFatEntryValue(idx);
    }

    void setFatEntryValue (int idx, int v)
    {
        _fatEntry.setFatEntryValue(idx, v);
    }

    public void close () throws Exception
    {
        _fmf.writeSectors(1, _fat);
    }

    public ArrayList<Integer> getFreeEntryList (int needed)
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (int s = 2; s<= MAXENTRY_1440KB; s++)
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
            bytes = _fmf.read(cluster+ DATAOFFSET);
            out.write(bytes);
            cluster = getFatEntryValue(cluster);
        }
        if (remainder != 0)
        {
            bytes = _fmf.read(cluster+ DATAOFFSET);
            out.write(bytes,0,remainder);
        }
        return out;
    }

    public void deleteFile (DirectoryEntry de) throws Exception
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
