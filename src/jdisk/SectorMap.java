package jdisk;

import java.util.HashMap;

class SectorMap extends HashMap<Integer, Sector>
{
    private final long creationTime = System.currentTimeMillis();

    public long getCreationTime ()
    {
        return creationTime;
    }
}
