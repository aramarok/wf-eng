package wf.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat dformat = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm:ss");

    public static String getTimestamp() {
	Date now = new Date();
	return dformat.format(now);
    }

    public static Date parse(final String s) {
	Date d = dformat.parse(s, new ParsePosition(0));
	return d;
    }
}
