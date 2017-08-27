import java.util.HashMap;
import java.util.Set;

public class JournalingDisk extends RawDisk
{
    private HashMap<Integer,Sector> changeMap = new HashMap<>();
    private HashMap<Long, HashMap<Integer,Sector>> mainMap = new HashMap<>();
    private long transactionId = 0;

    public JournalingDisk (int numsectors)
    {
        super(numsectors);
    }

    public JournalingDisk (int numsectors, int sectorsize)
    {
        super(numsectors, sectorsize);
    }

    public long beginTransaction()
    {
        transactionId++;
        return transactionId;
    }

    public void endTransaction()
    {
        mainMap.put(transactionId, (HashMap<Integer,Sector>) changeMap.clone());
        changeMap.clear();
    }

    public void rollbackTo (long tr)
    {
        HashMap<Integer,Sector> list = mainMap.get(tr);
        if (list != null)
        {
            Set<Integer> set = list.keySet();
            for (Integer sectNum : set)
            {
                Sector sect = list.get(sectNum);
                byte[] b1 = sect.read();
                byte[] b2 = super.read(sectNum);
                byte[] xord = plus(b1, b2);
                super.write(sectNum, xord);
            }
            mainMap.remove(tr);
        }
    }

    public byte[] read(int num)
    {
        return super.read(num);
    }

    public void write (int num, byte[] data)
    {
        byte[] old = super.read(num);
        Sector ext = new Sector (minus (old, data));
        changeMap.put(num, ext);
        super.write(num, data);
    }

    private static byte[] minus (byte[] in, byte[] src)
    {
        byte[] x = new byte[in.length];
        for (int s = 0; s < in.length; s++)
        {
            x[s] = (byte) (in[s] - src[s]);
        }
        return x;
    }

    private static byte[] plus (byte[] in, byte[] src)
    {
        byte[] x = new byte[in.length];
        for (int s = 0; s < in.length; s++)
        {
            x[s] = (byte) (in[s] + src[s]);
        }
        return x;
    }

}
