package disk144;

import java.time.LocalDateTime;

/**
 * Time stamp API
 */
public class Timestamp
{
    /**
     * Make FAT time stamp
     * @param sec seconds 0...59
     * @param min minutes 0...59
     * @param hour hours 0...23
     * @return Packed time stamp
     */
    private static int getTimeStamp (int sec, int min, int hour)
    {
        int a = (sec/2) & 31;
        int b = (min & 63) << 5;
        int c = (hour & 31) << 11;
        return (a | b | c) & 0xffff;
    }

    /**
     * Make FAT-compatible time stamp value
     * @return  16 bit packed time stamp
     */
    public static int getCurrentTimeStamp()
    {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        return getTimeStamp(second, minute, hour);
    }

    /**
     * Makes FAT date stamp
     * @param day day 1...31
     * @param month month 1...12
     * @param year year 1980...2107
     * @return Packed date stamp
     */
    private static int getDateStamp (int day, int month, int year)
    {
        int a = (day) & 31;
        int b = (month & 15) << 5;
        int c = ((year-1980) & 127) << 9;
        return (a | b | c) & 0xffff;
    }

    /**
     * Make FAT-compatible date stamp value
     * @return  16 bit packed date stamp
     */
    public static int getCurrentDateStamp ()
    {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        return getDateStamp(day, month, year);
    }
}
