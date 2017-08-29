package jdisk;

import java.io.Serializable;
import java.util.HashMap;

class TransactionMap extends HashMap<Long, SectorMap> implements Serializable
{
    static final long serialVersionUID = 1L;
}
