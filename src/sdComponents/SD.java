package sdComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a Sequence Diagram composed of Lifelines and Combined
 *         Fragments (CF).
 */
public class SD {
	public String name;
	public List<Lifeline> lifelines;
	public List<CF> cfs;

	public SD(String name, List<Lifeline> lifelines, List<CF> cfs) {
		this.name = name;
		this.lifelines = lifelines;
		this.cfs = cfs;
	}

	public SD(String name) {
		this.name = name;
		this.lifelines = new ArrayList<Lifeline>();
		this.cfs = new ArrayList<CF>();
	}

	public List<OS> osesInCFs() {
		List<OS> allOSes = new ArrayList<OS>();
		for (CF cf : cfs) {
			for (OS os : cf.getOSes()) {
				allOSes.add(os);
			}
		}
		return allOSes;
	}

	/**
	 * Returns first Lifeline object in lifelines by name.
	 * 
	 * @param name
	 *            Name of lifeline to search for.
	 * @return Lifeline corresponding to name belonging to this SD. Returns null
	 *         if not found.
	 */
	public Lifeline getLifeline(String name) {
		for (Lifeline lifeline : lifelines) {
			if (lifeline.name.equals(name))
				return lifeline;
		}
		System.err.println("getLifeline(): \"" + name + "\" does not correspond to an instantiated Lifeline.");
		return null;
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = "   >>SEQUENCE DIAGRAM<<\n";
		ret += tab + "Name: " + name + "\n";
		ret += tab + "Lifelines:\n";
		for (Lifeline element : lifelines)
			ret += tab + element.toString(tabs + 1) + "\n";
		ret += tab + "Combined fragments:\n";
		for (CF element : cfs)
			ret += tab + element.toString(tabs + 1) + "\n";

		return ret;
	}

}
