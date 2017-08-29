package jdisk;

import java.io.Serializable;
import java.util.HashMap;

class SectorMap extends HashMap<Integer, Sector> implements Serializable
{
    private final long creationTime = System.currentTimeMillis();
    static final long serialVersionUID = 1L;

    public long getCreationTime ()
    {
        return creationTime;
    }
}
