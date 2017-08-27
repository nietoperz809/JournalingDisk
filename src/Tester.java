import java.util.Arrays;

public class Tester
{
    public static void main (String[] args)
    {
        String hello = "hello world";
        String doof  = "fuck off du";
        String dumm  = "1234567890a";
        JournalingDisk disk = new JournalingDisk(100, hello.length());

        long trans1 = disk.beginTransaction();
        System.out.println(trans1);
        disk.write(0, hello.getBytes());
        disk.endTransaction();
        byte[]cc = disk.read(0);
        System.out.println(Arrays.toString(cc));

        long trans2 = disk.beginTransaction();
        System.out.println(trans2);
        disk.write(0, doof.getBytes());
        disk.endTransaction();
        cc = disk.read(0);
        System.out.println(Arrays.toString(cc));

        long trans3 = disk.beginTransaction();
        System.out.println(trans3);
        disk.write(0, dumm.getBytes());
        disk.endTransaction();
        cc = disk.read(0);
        System.out.println(Arrays.toString(cc));

        disk.rollback();
        cc = disk.read(0);
        System.out.println(Arrays.toString(cc));

        disk.rollback();
        cc = disk.read(0);
        System.out.println(Arrays.toString(cc));

        disk.rollback();
        cc = disk.read(0);
        System.out.println(Arrays.toString(cc));
    }
}
