package sdComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents an Execution Unit which belongs to a single Compositional
 *         Execution Unit (CEU) and may contain nested CEUs corresponding to
 *         nested Combined Fragments (CFs).
 * 
 */
public class EU {
	public Lifeline lifeline;
	public Operand operand;
	public String name;

	// lists
	public List<OS> directedOSes;
	public List<CEU> directedCEUs;
	public List<EU> connectedEUs;
	public List<String> coveredEULabels;
	public List<String> states; // labels of states
	public List<Ordered> ordereds;

	public List<OS> ignoreOSes;
	public List<OS> consideredOSes;
	public List<OS> allPossibleOSes;

	public EU() {
		super();
		lifeline = null;
		operand = null;
		name = null;
		directedOSes = new ArrayList<OS>();
		directedCEUs = new ArrayList<CEU>();
		connectedEUs = new ArrayList<EU>();
		coveredEULabels = new ArrayList<String>();
		ignoreOSes = new ArrayList<OS>();
		consideredOSes = new ArrayList<OS>();
		allPossibleOSes = new ArrayList<OS>();
		ordereds = new ArrayList<Ordered>();
	}

	public Constraint getConstraint() {
		if (operand != null)
			return operand.constraint;
		return null;
	}

	public int getNumber() {
		if (operand != null)
			return operand.constraint.num;
		return -1;
	}

	public String toString() {
		String ret = "EU\n";
		ret += "\tName: " + name + "\n";
		ret += "\tLifeline: " + lifeline.name + "\n";
		ret += "\tOperand: " + operand.constraint.constraint + "\n";
		ret += "\tDirected OSes:\n";
		for (OS element : directedOSes)
			ret += "\t\t" + element.name + "\n";
		ret += "\tDirected CEUes:\n";
		for (CEU element : directedCEUs)
			ret += "\t\t" + element.name + "\n";
		ret += "\tConnected EUs:\n";
		for (EU element : connectedEUs)
			ret += "\t\t" + element.name + "\n";
		ret += "\tCovered EU labels:\n";
		for (String element : coveredEULabels)
			ret += "\t\t" + element + "\n";
		ret += "\tStates:\n";
		for (String element : states)
			ret += "\t\t" + element + "\n";
		ret += "\tOrdereds:\n";
		for (Ordered element : ordereds)
			ret += "\t\t" + element.name + "\n";
		ret += "\tIgnored OSes:\n";
		for (OS element : ignoreOSes)
			ret += "\t\t" + element.name + "\n";
		ret += "\tConsidered OSes:\n";
		for (OS element : consideredOSes)
			ret += "\t\t" + element.name + "\n";
		ret += "\tAll possible OSes:\n";
		for (OS element : allPossibleOSes)
			ret += "\t\t" + element.name + "\n";

		return ret;
	}
}
