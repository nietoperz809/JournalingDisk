package disk144;

import static disk144.Constants.*;

/**
 * Repesents FAT12 Directory
 */
final class Directory
{
    /**
     * Copy of directora
     */
    private final byte[] directoryBytes;

    private final Disk144 disk;

    /**
     * Constructor
     * @param fmf FMF containing disk file
     */
    public Directory (Disk144 fmf)
    {
        disk = fmf;
        directoryBytes = disk.readSectors (DIRSTARTSECTOR, NUMDIRSECTORS);
    }

    /**
     * Writes dir bytes back to Disk FMF
     */
    public void close ()
    {
        disk.writeSectors (DIRSTARTSECTOR, directoryBytes);
    }


    /**
     * List all files of master directory as human readable string
     * @return string containing list
     */
    public String list()
    {
        StringBuilder sb = new StringBuilder();
        for (int s=0; ; s++)
        {
            DirectoryEntry de = new DirectoryEntry(directoryBytes, s * DIRENTRYSIZE);
            if (de.isNull())
                break;
            sb.append(de.toString()).append('\n');
        }
        return sb.toString();
    }

    /**
     * Seeks file by file name
     * @param fname file name
     * @return new Dir entry representing the file
     */
    public DirectoryEntry seekFile (String fname)
    {
        fname = fname.toUpperCase();
        for (int s=0; ; s++)
        {
            DirectoryEntry de = new DirectoryEntry(directoryBytes, s * DIRENTRYSIZE);
            if (de.isNull())
                throw new RuntimeException("file not found");
            if (de.getFullName().equals(fname))
            {
                de.positionInDirectory = s;
                return de;
            }
        }
    }

    /**
     * Sets new dir entry at specified position
     * @param de The new Dir Entry
     * @param index index of position in dir table
     */
    public void put (DirectoryEntry de, int index)
    {
        int offset = DIRENTRYSIZE * index;
        byte[] dat = de.asArray();
        System.arraycopy(dat,0, directoryBytes,offset, DIRENTRYSIZE);
    }

    /**
     * Finds first free Dir index
     * @return Found index or none (in this case an Exception is thrown)
     */
    public int getFreeDirectoryEntryOffset (int start)
    {
        for (int s = start; s< DIRENTRYCOUNT; s++)
        {
            DirectoryEntry de = new DirectoryEntry(directoryBytes, s * DIRENTRYSIZE);
            if (de.isNull() || de.isDeleted())
                return s;
        }
        throw new RuntimeException("directory full");
    }

    public int getFreeDirectoryEntryOffset ()
    {
        return getFreeDirectoryEntryOffset(0);
    }

//    public int getNextFreeDirectoryEntryOffset ()
//    {
//        int i = getFreeDirectoryEntryOffset(0);
//        return getFreeDirectoryEntryOffset(i+1);
//    }
}
