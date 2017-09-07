package disk144;

import static disk144.Constants.*;

/**
 * Created by Administrator on 11/20/2016.
 */
public class DirectoryEntry
{
    public int positionInDirectory; // Internal use
    private byte[] RawData = null;
    /**
     * Standard Constructor made private
     */
    private DirectoryEntry ()
    {
        RawData = new byte[DIRENTRYSIZE];
    }
    /**
     * Constructor from bigger array that contains a Directory Entry
     *
     * @param array  Byte array
     * @param offset Offset where the directory entry can be found
     */
    public DirectoryEntry (byte[] array, int offset)
    {
        this();
        System.arraycopy(array, offset, RawData, 0, DIRENTRYSIZE);
    }

    /**
     * Create a volume label dir entry
     *
     * @param lab the label text
     * @return a dir entry with that label
     */
    public static DirectoryEntry createVolumeLabel (String lab)
    {
        StringBuilder labBuilder = new StringBuilder(lab);
        while (labBuilder.length() < 11)
        {
            labBuilder.append(' ');
        }
        lab = labBuilder.toString();
        return create(lab.substring(0, 8), lab.substring(8, 11),
                0, 0, (byte) VOLUMELABEL);
    }

    /**
     * Build a Dir entry from input
     *
     * @param name         File name
     * @param ext          File extension
     * @param fileSize     File size
     * @param firstCluster First cluster on disk
     * @return Newly created Dir Entry
     */
    static DirectoryEntry create (String name, String ext,
                                          long fileSize,
                                          int firstCluster,
                                          int attributes)
    {
        int ts = Timestamp.getCurrentTimeStamp();
        int ds = Timestamp.getCurrentDateStamp();
        DirectoryEntry d = new DirectoryEntry();
        d.setFileName(name);
        d.setExtension(ext);
        d.setAttributes((byte) attributes);
        ByteCVT.toLE16(ts, d.RawData, 14);
        ByteCVT.toLE16(ds, d.RawData, 16);
        ByteCVT.toLE16(0, d.RawData, 18);
        ByteCVT.toLE16(ts, d.RawData, 22);
        ByteCVT.toLE16(ds, d.RawData, 24);
        d.setFirstCluster(firstCluster);
        d.setFileSize(fileSize);
        return d;
    }

    public static DirectoryEntry createSubdirEntry (String name, String ext, int firstCluster)
    {
        return create(name, ext, 0, firstCluster, (byte) SUBDIRECTORY); /* Test +1*/
    }

    /**
     * Get Dir entry as array
     *
     * @return the dir entry as bytes
     */
    public byte[] asArray ()
    {
        return RawData;
    }

    public void setDeleted ()
    {
        RawData[0] = DELETED;
    }

    public boolean isNull ()
    {
        return (RawData[0] == 0);
    }

    /**
     * Shows this dir entry in human readable form
     *
     * @return a String
     */
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getFullName());
        sb.append(", ").append(getFileSize()).append(" Bytes, ");
        byte attributes = getAttributes();
        if ((attributes & 1) == 1)
        {
            sb.append("Rea");
        }
        if ((attributes & 2) == 2)
        {
            sb.append("Hid");
        }
        if ((attributes & 4) == 4)
        {
            sb.append("Sys");
        }
        if ((attributes & 8) == 8)
        {
            sb.append("Lab");
        }
        if ((attributes & 16) == 16)
        {
            sb.append("Dir");
        }
        if ((attributes & 32) == 32)
        {
            sb.append("Arc");
        }
        if (isDeleted())
        {
            sb.append("Del");
        }
        sb.append(", ").append("1stSector: ").append(getFirstCluster());
        return sb.toString();
    }

    /**
     * Get Name.Ext
     *
     * @return String containing name+ext with a dot between them
     */
    public String getFullName ()
    {
        String ext = getExtension();
        String name = getFileName();
        if (ext.isEmpty())
        {
            return name;
        }
        return name + "." + ext;
    }

    public long getFileSize ()
    {
        long fs = ByteCVT.fromLE32(RawData, 28);
        return fs;
    }

    private void setFileSize (long val)
    {
        ByteCVT.toLE32(val, RawData, 28);
    }

    private byte getAttributes ()
    {
        return RawData[11];
    }

    public boolean isDeleted ()
    {
        return (RawData[0] == DELETED);
    }

    public int getFirstCluster ()
    {
        return ByteCVT.fromLE16(RawData, 26);
    }

    private String getExtension ()
    {
        return new String(RawData, 8, 3).toUpperCase().trim();
    }

    private String getFileName ()
    {
        if (isDeleted())
        {
            return "?" + new String(RawData, 1, 7).toUpperCase().trim();
        }
        else
        {
            return new String(RawData, 0, 8).toUpperCase().trim();
        }
    }

    private void setFileName (String name)
    {
        for (int s = 0; s < 8; s++)
        {
            if (s < name.length())
            {
                RawData[s] = (byte) Character.toUpperCase(name.charAt(s));
            }
            else
            {
                RawData[s] = (byte) ' ';
            }
        }
    }

    private void setExtension (String ext)
    {
        for (int s = 0; s < 3; s++)
        {
            if (s < ext.length())
            {
                RawData[s + 8] = (byte) Character.toUpperCase(ext.charAt(s));
            }
            else
            {
                RawData[s + 8] = (byte) ' ';
            }
        }
    }

    private void setFirstCluster (int val)
    {
        ByteCVT.toLE16(val, RawData, 26);
    }

    private void setAttributes (byte v)
    {
        RawData[11] = v;
    }

}
