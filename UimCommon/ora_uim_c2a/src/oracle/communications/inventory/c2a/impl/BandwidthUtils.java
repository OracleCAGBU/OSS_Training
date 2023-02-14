package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BandwidthUtils {
	static final Pattern speedPattern = Pattern
	        .compile("([0-9]+)[ ]*([GMk]?)bps");

	public static long parseSpeed(String s) throws NumberFormatException {
		if (s == null || s.length() == 0)
			return 0;
		Matcher m = speedPattern.matcher(s);
		if (!m.matches() || m.groupCount() != 2)
			return 0;
		long speed = Long.parseLong(m.group(1));
		String multiplier = m.group(2);
		if (!multiplier.isEmpty()) {
			switch (multiplier.charAt(0)) {
			case 'G':
				speed *= 1000 * 1000 * 1000;
				break;
			case 'M':
				speed *= 1000 * 1000;
				break;
			case 'k':
				speed *= 1000;
				break;
			}
		}
		return speed;
	}
}
