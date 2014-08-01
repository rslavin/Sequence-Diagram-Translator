package utils;

import java.util.List;

import sdComponents.Printable;

public class Strings {

	/**
	 * Iterates through list and prints elements with preceding tabs.
	 * 
	 * @param list
	 *            List to print.
	 * @param tab
	 *            Number of preceding tabs.
	 * @return List in String format.
	 */
	public String listToString(List<Printable> list, int tab) {
		String ret = "";
		for (Printable element : list)
			ret += element.toString(tab + 1);
		return ret;
	}
}
