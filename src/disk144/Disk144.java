package disk144;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import jdisk.RawDisk;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;

import static disk144.Constants.*;

public class Disk144 extends RawDisk
{
    private static final byte[] fatInitBytes = {(byte) 0xf0, (byte) 0xff, (byte) 0xff};
    
    /**
     * Constructor
     * @param name volume name
     * @throws Exception if smth. gone wrong
     */
    public Disk144 (String name) throws Exception
    {
        super();
        write (0, winNtBootSector);

        // Formatting ...
        writeBytes(0x27, getFourRandomBytes());
        writeBytes(0x200, fatInitBytes);
        writeBytes(0x1400, fatInitBytes);
        fillArea((byte)0xf6, 0x4200, 1457664);
        setVolumeLabel(name);
    }

    /**
     * set Vol label of this disk
     * @param label the label
     * @throws Exception if smth. failed
     */
    private void setVolumeLabel (String label) throws Exception
    {
        StringBuilder labelBuilder = new StringBuilder(label.toUpperCase());
        while (labelBuilder.length()<11)
            labelBuilder.append(' ');
        label = labelBuilder.toString();
        if (label.length() > 11)
            label = label.substring (0, 11);
        writeBytes (0x2b, label.getBytes());   // write label in boot block

        Directory directory = new Directory(this); // write label in directory
        int freedir = directory.getFreeDirectoryEntryOffset();
        DirectoryEntry de = DirectoryEntry.createVolumeLabel(label);
        directory.put (de, freedir);
        directory.close();
    }

    /**
     * Get 4 random bytes
     * @return the bytes
     */
    private static byte[] getFourRandomBytes ()
    {
        byte b[] = new byte[4];
        new SecureRandom().nextBytes(b);
        return b;
    }

    /**
     * Put a new File on disk
     * @param filename File name
     * @param ext Extension
     * @param data File Data
     * @throws Exception
     */
    public void putFile (String filename, String ext, byte[] data) throws Exception
    {
        Fat12 fat = new Fat12(this);
        Directory directory = new Directory(this);
        int freedir = directory.getFreeDirectoryEntryOffset();

        SplitHelper sh = new SplitHelper(data.length, CLUSTERSIZE);
        ArrayList<Integer> freeList = fat.getFreeEntryList(sh.getTotalblocks());

        ByteOutputStream splits[] = SplitHelper.split(data, CLUSTERSIZE);

        DirectoryEntry de = DirectoryEntry.create(filename,
                ext,
                data.length,
                freeList.get(0),
                ARCHIVE);

        directory.put (de, freedir);

        for (int i=0; i<sh.getTotalblocks(); i++)
        {
            int sector = freeList.get(i);
            int nextsector;
            if (i == (sh.getTotalblocks()-1))
            {
                nextsector = LAST_SLOT;
            }
            else
            {
                nextsector = freeList.get(i + 1);
            }
            writeSectors(sector+ DATAOFFSET, splits[i].getBytes());

            fat.setFatEntryValue (sector, nextsector);
        }

        directory.close ();
        fat.close();
    }

    public void deleteFile (String filename) throws Exception
    {
        Fat12 fat = new Fat12(this);
        Directory directory = new Directory(this);
        DirectoryEntry de = directory.seekFile(filename);
        de.setDeleted();
        directory.put(de, de.positionInDirectory);
        fat.deleteFile(de);

        fat.close();
        directory.close();
    }

    /**
     * Read a file on Disk
     * @param filename Name of file (Format: name.ext)
     * @return a DynArray containing the file data
     * @throws Exception if smth gone wrong or file doesn't exist
     */
    public ByteArrayOutputStream getFileData (String filename) throws Exception
    {
        Fat12 fat = new Fat12(this);
        Directory d = new Directory(this);
        DirectoryEntry de = d.seekFile(filename);
        return fat.getFile(de);
    }

    public void createSubDir (String name, String ext) throws Exception
    {
        Fat12 fat = new Fat12(this);
        ArrayList<Integer> freeList = fat.getFreeEntryList(1); // only one sector
        Directory directory = new Directory(this);
        int freedir = directory.getFreeDirectoryEntryOffset();
        DirectoryEntry de = DirectoryEntry.createSubdirEntry(name, ext, freedir+1); // TODO: why+1?
        fat.setFatEntryValue (freeList.get(0), LAST_SLOT); // only one sector
        directory.put (de, freedir);

        directory.close ();
        fat.close();
    }

    public static void main (String[] args) throws Exception
    {
        String dfile = "c:/testdisk.img";
        Disk144 disk = new Disk144("halloweltdubistcool");
        //disk.fromFile();

        disk.createSubDir("nana", "");
       // disk.toFile("c:/disk2-144.img");

//        ByteArrayOutputStream ba = disk.getFileData("fuck.txt");
//        System.out.println(ba);

//        byte[] buff = new byte[1000];
//        for (int s=0; s<buff.length; s++)
//        {
//            buff[s] = (byte)(s%26 + 'a');
//        }
//        disk.putFile("hallo", "txt", buff);

        disk.toFile(dfile);
    }
}
