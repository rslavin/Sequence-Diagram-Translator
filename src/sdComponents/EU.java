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
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = "   >>EU<<\n";
		ret += tab + "Name: " + name + "\n";
		if (lifeline != null)
			ret += tab + "Lifeline: " + lifeline.name + "\n";
		if (operand != null)
			ret += tab + "Operand: " + operand.constraint.constraint + "\n";
		ret += tab + "Directed OSes:\n";
		if (directedOSes != null)
			for (OS element : directedOSes)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Directed CEUes:\n";
		if (directedCEUs != null)
			for (CEU element : directedCEUs)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Connected EUs:\n";
		if (connectedEUs != null)
			for (EU element : connectedEUs)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Covered EU labels:\n";
		if (coveredEULabels != null)
			for (String element : coveredEULabels)
				ret += tab + "   " + element + "\n";
		ret += tab + "States:\n";
		if (states != null)
			for (String element : states)
				ret += tab + "   " + element + "\n";
		ret += tab + "   Ordereds:\n";
		if (ordereds != null)
			for (Ordered element : ordereds)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Ignored OSes:\n";
		if (ignoreOSes != null)
			for (OS element : ignoreOSes)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Considered OSes:\n";
		if (consideredOSes != null)
			for (OS element : consideredOSes)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "All possible OSes:\n";
		if (allPossibleOSes != null)
			for (OS element : allPossibleOSes)
				ret += tab + "   " + element.name + "\n";

		return ret;
	}
}
