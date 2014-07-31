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
	public List<Ordered> orderds;

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
		orderds = new ArrayList<Ordered>();
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
}
