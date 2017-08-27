import java.util.HashMap;

class SectorMap extends HashMap<Integer,Sector>
{
    private long timeStamp;

    public SectorMap ()
    {
        super();
        timeStamp = System.currentTimeMillis();
    }

    public long getTimeStamp ()
    {
        return timeStamp;
    }
}
