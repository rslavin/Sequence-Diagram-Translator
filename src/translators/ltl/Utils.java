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
	public static String strongUntil(String p, String q) {
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
	public static String conjunct(ArrayList<String> list) {
		String ret = "";
		switch (list.size()) {
		case 0:
			return "";
		case 1:
			return list.get(0);
		default:
			for (String element : list)
				ret += element + " & ";
			return ret.substring(0, ret.length() - 3);
		}
	}

	/**
	 * Disjuncts String elements in list.
	 * 
	 * @param list
	 *            ArrayList of Strings to disjunct.
	 * @return String with elements of list connected with " | ". Removes
	 *         trailing " | ".
	 */
	public static String disjunct(ArrayList<String> list) {
		String ret = "";
		switch (list.size()) {
		case 0:
			return "";
		case 1:
			return list.get(0);
		default:
			for (String element : list)
				ret += element + " | ";
			return ret.substring(0, ret.length() - 3);
		}
	}

	public static String debugPrint(String msg) {
		return "\r\n-- DEBUG: " + msg + "\r\n";

	}
	
	public static String globally(String msg){
		return "G (" + msg + ")";
	}

}
