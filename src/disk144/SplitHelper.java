package disk144;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

/**
 * Created by Administrator on 11/24/2016.
 */
public class SplitHelper
{
    public int getBlocks()
    {
        return blocks;
    }
    private int blocks;

    public int getRemainder ()
    {
        return remainder;
    }

    private int remainder;

    public int getTotalblocks ()
    {
        return totalblocks;
    }

    private int totalblocks;

    public SplitHelper (long length, int chunksize)
    {
        blocks = (int)(length / chunksize);
        remainder = (int)(length % chunksize);
        totalblocks = blocks + (remainder !=0 ? 1 : 0);
    }

    public static ByteOutputStream[] split (byte[] data, int fragment)
    {
        SplitHelper sh = new SplitHelper(data.length, fragment);
        ByteOutputStream[] res = new ByteOutputStream[sh.getTotalblocks()];
        int s;
        for (s=0; s<sh.getBlocks(); s++)
        {
            res[s] = new ByteOutputStream();
            res[s].write (data,s*fragment,fragment);
        }
        if (sh.getRemainder() != 0)
        {
            res[s] = new ByteOutputStream();
            res[s].write (data,s*fragment,sh.getRemainder());
        }
        return res;
    }

}
