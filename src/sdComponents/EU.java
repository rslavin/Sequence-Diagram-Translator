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
	
	public String toString(){
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for(int i = 0; i < tabs; i++)
			tab += "   ";
		
		String ret = tab + "EU\n";
		ret += tab + "\tName: " + name + "\n";
		ret += tab + "\tLifeline: " + lifeline.name + "\n";
		ret += tab + "\tOperand: " + operand.constraint.constraint + "\n";
		ret += tab + "\tDirected OSes:\n";
		for (OS element : directedOSes)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tDirected CEUes:\n";
		for (CEU element : directedCEUs)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tConnected EUs:\n";
		for (EU element : connectedEUs)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tCovered EU labels:\n";
		for (String element : coveredEULabels)
			ret += tab + "\t\t" + element + "\n";
		ret += tab + "\tStates:\n";
		for (String element : states)
			ret += tab + "\t\t" + element + "\n";
		ret += tab + "\tOrdereds:\n";
		for (Ordered element : ordereds)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tIgnored OSes:\n";
		for (OS element : ignoreOSes)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tConsidered OSes:\n";
		for (OS element : consideredOSes)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tAll possible OSes:\n";
		for (OS element : allPossibleOSes)
			ret += tab + "\t\t" + element.name + "\n";

		return ret;
	}
}
