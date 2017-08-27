
public class Sector
{
    private final byte[] array;

    public Sector (int size)
    {
        array = new byte[size];
    }

    public Sector()
    {
        this(512);
    }

    public Sector(byte[] init)
    {
        this(init.length);
        write (init);
    }


    public byte[] read()
    {
        byte[] ret = new byte[array.length];
        System.arraycopy(array,0, ret,0, array.length);
        return ret;
    }

    public void write (byte[] data)
    {
        System.arraycopy(data,0, array,0, array.length);
    }
}
