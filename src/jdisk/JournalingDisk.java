package jdisk;

import java.util.Set;

public class JournalingDisk extends RawDisk
{
    private SectorMap sectorMap;
    private TransactionMap mainMap = new TransactionMap();
    private long transactionId = 0;

    /**
     * Constructor with default sector size of 512 bytes
     * @param numsectors number of sectors
     */
    public JournalingDisk (int numsectors)
    {
        super(numsectors);
    }

    /**
     * Constructor
     * @param numsectors Number of sectors
     * @param sectorsize Bytes per sector
     */
    public JournalingDisk (int numsectors, int sectorsize)
    {
        super(numsectors, sectorsize);
    }

    /**
     * Start a transaction
     * Must be called before a read/write ops take place
     * @return current transaction id
     */
    public long beginTransaction()
    {
        sectorMap = new SectorMap();
        transactionId++;
        return transactionId;
    }

    /**
     * Convenient function
     * @return the current transaction id
     */
    public long getTransactionId()
    {
        return transactionId;
    }

    /**
     * End a transaction
     * Must be called if r/w ops are finished
     */
    public void endTransaction()
    {
        mainMap.put(transactionId, sectorMap);
        sectorMap = null;
    }

    /**
     * Main function performing rollbacks
     */
    private void internalRollback ()
    {
        SectorMap list = mainMap.get(transactionId);
        if (list != null)
        {
            Set<Integer> set = list.keySet();
            for (Integer sectNum : set)
            {
                Sector sect = list.get(sectNum);
                byte[] b1 = sect.getInternalBuffer();
                byte[] b2 = super.getInternalBuffer(sectNum);
                byte[] xord = plus(b1, b2);
                super.write(sectNum, xord);
            }
            mainMap.remove(transactionId);
        }
    }

    /**
     * Rollback last transaction
     */
    public void rollback()
    {
        internalRollback();
        transactionId--;
    }

    /**
     * Do multiple rollbacks
     * @param num how many transactions to reverse
     */
    public void rollback(int num)
    {
        while (num-- != 0)
        {
            rollback();
        }
    }

    /**
     * Write one sector and store difference in sector map
     * @param num Sector number
     * @param data new content
     */
    public void write (int num, byte[] data)
    {
        byte[] old = super.getInternalBuffer(num);
        Sector ext = new Sector(minus (old, data));
        sectorMap.put(num, ext);
        super.write(num, data);
    }

    /**
     * Calculate sector diff
     * @param in sector 1
     * @param src sector 2
     * @return in-src
     */
    private static byte[] minus (byte[] in, byte[] src)
    {
        byte[] x = new byte[in.length];
        for (int s = 0; s < in.length; s++)
        {
            x[s] = (byte) (in[s] - src[s]);
        }
        return x;
    }

    /**
     * Reverses sector diff
     * @param in  sect1
     * @param src sect2
     * @return in+src
     */
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
