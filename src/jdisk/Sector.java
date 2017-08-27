package jdisk;

public class Sector
{
    private final byte[] _buffer;

    public Sector (int size)
    {
        _buffer = new byte[size];
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
        byte[] ret = new byte[_buffer.length];
        System.arraycopy(_buffer,0, ret,0, _buffer.length);
        return ret;
    }

    public void write (byte[] data)
    {
        System.arraycopy(data,0, _buffer,0, _buffer.length);
    }

    public byte[] getInternalBuffer()
    {
        return _buffer;
    }
}
