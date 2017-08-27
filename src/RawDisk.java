
public class RawDisk
{
    private Sector[] sectors;

    public RawDisk (int numsectors, int sectorsize)
    {
        sectors = new Sector[numsectors];
        for (int s=0; s<numsectors; s++)
            sectors[s] = new Sector(sectorsize);
    }

    public RawDisk (int numsectors)
    {
        this(numsectors, 512);
    }

    public byte[] read(int num)
    {
        return sectors[num].read();
    }

    public void write (int num, byte[] data)
    {
        sectors[num].write(data);
    }

}
