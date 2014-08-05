package translators.ltl;

import java.util.ArrayList;

public class Utils {

	/**
	 * Prints (p U (q & p))
	 * 
	 * @param p
	 *            Input string
	 * @param q
	 *            Input string
	 * @return Returns string representation fo strong until.
	 */
	protected static String strongUntil(String p, String q) {
		return "(" + p + " U (" + q + " & " + p + "))";
	}

	/**
	 * Conjuncts String elements in list.
	 * 
	 * @param list
	 *            ArrayList of Strings to conjunct
	 * @return String with elements of list connected with " & ". Removes
	 *         trailing " & ".
	 */
	protected static String conjunct(ArrayList<String> list) {
		String ret = "";
		for (String element : list)
			ret += element + " & ";
		return ret.substring(0, ret.length() - 3);
	}

}
