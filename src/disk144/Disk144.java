package disk144;

import jdisk.RawDisk;

import java.security.SecureRandom;

import static disk144.Constants.winNtBootSector;

public class Disk144 extends RawDisk
{
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
        final byte[] fatInitBytes = {(byte) 0xf0, (byte) 0xff, (byte) 0xff};
        writeBytes(0x200, fatInitBytes);
        writeBytes(0x1400, fatInitBytes);
        fillArea((byte)0xf6, 0x4200, 1457664);
        setVolumeLabel(name);
    }

    /**
     * set Vol label of this disk
     * @param label
     * @throws Exception
     */
    private void setVolumeLabel (String label) throws Exception
    {
        label = label.toUpperCase();
        while (label.length()<11)
            label = label+' ';
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

    public static void main (String[] args) throws Exception
    {
        Disk144 disk = new Disk144("halloweltdubistcool");
        disk.toFile("c:/disk2-144.img");
    }
}
