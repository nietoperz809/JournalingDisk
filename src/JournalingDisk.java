import java.util.HashMap;
import java.util.Set;

public class JournalingDisk extends RawDisk
{
    class SectorMap extends HashMap<Integer,Sector>
    {
        private long time;

        public SectorMap()
        {
            super();
            time = System.currentTimeMillis();
        }
    }

    class TransactionMap extends HashMap<Long, SectorMap>
    {

    }

    private SectorMap sectorMap;
    private TransactionMap mainMap = new TransactionMap();
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
        sectorMap = new SectorMap();
        transactionId++;
        return transactionId;
    }

    public void endTransaction()
    {
        mainMap.put(transactionId, sectorMap);
    }

    public void rollbackTo (long tr)
    {
        SectorMap list = mainMap.get(tr);
        //System.out.println(list.time);
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

    public void rollback()
    {
        rollbackTo(transactionId);
        transactionId--;
    }


    public byte[] read(int num)
    {
        return super.read(num);
    }

    public void write (int num, byte[] data)
    {
        byte[] old = super.read(num);
        Sector ext = new Sector (minus (old, data));
        sectorMap.put(num, ext);
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
