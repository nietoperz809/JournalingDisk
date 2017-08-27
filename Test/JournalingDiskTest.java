
import jdisk.JournalingDisk;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class JournalingDiskTest
{
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
}