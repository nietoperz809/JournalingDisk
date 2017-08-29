import jdisk.JournalingDisk;
import jdisk.RawDisk;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JournalingDiskTest
{
    @Test
    public void testWriteSector () throws Exception
    {
        byte[] b1 = new byte[]{1,1,1,1,1};
        byte[] b2 = new byte[]{2,2,2,2,2};
        RawDisk disk = new RawDisk(100, 5);
        disk.write(0, b1);
        disk.write(1,b2);
        assertArrayEquals(b1, disk.read(0));
        assertArrayEquals(b2, disk.read(1));
        disk.writeSectors(0,new byte[]{10,11,12,13,14,15});
        assertArrayEquals(new byte[]{10,11,12,13,14}, disk.read(0));
        assertArrayEquals(new byte[]{15,2,2,2,2}, disk.read(1));
    }

    @Test
    public void testRWStream () throws Exception
    {
        String s = "thequiclbrownfoxfumpsoverthelazydog";
        RawDisk disk = new RawDisk(100, 5);
        disk.writeBytes(3, s.getBytes());
        String t = new String(disk.readBytes(3,s.length()));
        assertEquals(s,t);
    }

    @Test
    public void test1 () throws Exception
    {
        String hello = "hello world";
        String doof  = "fuck off du";
        String dumm  = "1234567890a";
        JournalingDisk disk = new JournalingDisk(100, hello.length());
        byte[] empty = disk.read(0);

        disk.beginTransaction();
        disk.write(0, hello.getBytes());
        disk.endTransaction();
        byte[]cc = disk.read(0);
        assertEquals (hello, new String(cc));

        disk.beginTransaction();
        disk.write(0, doof.getBytes());
        disk.endTransaction();
        cc = disk.read(0);
        assertEquals (doof, new String(cc));

        disk.beginTransaction();
        disk.write(0, dumm.getBytes());
        disk.endTransaction();
        cc = disk.read(0);
        assertEquals (dumm, new String(cc));

        ArrayList<Long> al = disk.getTransactionTimes();
        System.out.println(al);
        assertEquals(al.size(), 3);

        disk.rollback();
        cc = disk.read(0);
        assertEquals (doof, new String(cc));

        disk.rollback();
        cc = disk.read(0);
        assertEquals (hello, new String(cc));

        disk.rollback();
        cc = disk.read(0);
        assertArrayEquals (empty, cc);
    }

    @Test
    public void testWrite()
    {
        String hello = "hello world";
        JournalingDisk disk = new JournalingDisk(100, hello.length());

        disk.beginTransaction();
        disk.write(0, hello.getBytes());
        disk.write(1, hello.getBytes());
        disk.endTransaction();
        try
        {
            disk.saveJournal("c:/lalajournal1.raw");
            disk.toFile("c:/lala1.raw");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        disk.rollback();
        try
        {
            disk.saveJournal("c:/lalajournal2.raw");
            disk.toFile("c:/lala2.raw");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}