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

	public List<OS> osesInCFs() {
		List<OS> allOSes = new ArrayList<OS>();
		for (CF cf : cfs) {
			for (OS os : cf.oses) {
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
		return null;
	}

}
