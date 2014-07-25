package comparators;

import java.util.Comparator;
import sdComponents.OS;

public class OSComparator implements Comparator<OS> {

	public int compare(OS os1, OS os2) {
		if (os1.number > os2.number)
			return 1;
		return 0;
	}
}
