package sdComponents;

import java.util.ArrayList;
import java.util.List;

import enums.Operator;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a Compositional Execution Unit. CEUs are projections of
 *         CFs onto a Lifeline. Each operand projects an execution unit (EU) on
 *         a Lifeline. A CEU is associated with a single Lifeline and may be
 *         composed of multiple EUs. Ordering must be maintained in order to
 *         calculate pre- and post- OSes. For this reason, CEU extends Ordered
 *         along with OS.
 */

public class CEU extends Ordered {
	public CF cf; // cf to which CEU belongs
	public List<EU> eus; // list of EUs

	// lists
	public List<String> coveredLifelineNames;
	public List<Operator> coveredOperators;
	public List<Constraint> coveredConstraints;
	public List<CEU> parentCEUs;

	public boolean hasCriticalChild;
	public OS firstOS; // was "int firstOSLocation"
	public int iteration;

	public boolean isParentPar; // unsure
	public List<EU> euIterations; // unsure
	public List<CEU> connectedCEUs; // unsure

	public CEU(CF cf) {
		super();
		this.cf = cf;
		eus = new ArrayList<EU>();
		coveredLifelineNames = new ArrayList<String>();
		coveredOperators = new ArrayList<Operator>();
		coveredConstraints = new ArrayList<Constraint>();
		parentCEUs = new ArrayList<CEU>();
		hasCriticalChild = false;
		firstOS = null;
		isParentPar = false;
		euIterations = new ArrayList<EU>();
		connectedCEUs = new ArrayList<CEU>();
		name = null;
	}

	/**
	 * Returns the number of operands covered by the CEU.
	 * 
	 * @return integer value of operands covered by CEU.
	 */
	public int getNumOperands() {
		if (cf != null)
			return cf.operands.size();
		return -1;
	}

	/**
	 * Returns operator covered by the CEU.
	 * 
	 * @return Operator covered by CEU.
	 */
	public Operator getOperator() {
		if (cf != null)
			return cf.operator;
		return null;
	}

	/**
	 * Finds first occurring OS
	 * 
	 * @return First OS in CEU.
	 */
	public OS getFirstOS() {
		if (eus != null && eus.size() > 0) {
			EU firstEU = eus.get(0);
			if (firstEU.directedOSes.size() > 0)
				return firstEU.directedOSes.get(0);
			else if (firstEU.directedCEUs.size() > 0)
				return firstEU.directedCEUs.get(0).getFirstOS();
		}
		return null;
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";
		String ret = "   >>CEU<<\n";
		ret += tab + "Name: " + name + "\n";
		if (lifeline != null)
			ret += tab + "Lifeline: " + lifeline.name + "\n";
		if (cf != null)
			ret += tab + "Combined Fragment: " + cf.num + "\n";
		ret += tab + "Has critical child: " + hasCriticalChild + "\n";
		if (firstOS != null)
			ret += tab + "First OS: " + firstOS.name + "\n";
		ret += tab + "Iteration: " + iteration + "\n";
		ret += tab + "Is parent of parent: " + isParentPar + "\n";
		ret += tab + "EUs:\n";
		if (eus != null)
			for (EU element : eus)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Covered Lifeline Names:\n";
		if (coveredLifelineNames != null)
			for (String element : coveredLifelineNames)
				ret += tab + "   " + element + "\n";
		ret += tab + "Covered Operators:\n";
		if (coveredOperators != null)
			for (Operator element : coveredOperators)
				ret += tab + "   " + element + "\n";
		ret += tab + "Covered Constraints:\n";
		if (coveredConstraints != null)
			for (Constraint element : coveredConstraints)
				ret += tab + "   " + element.constraint + "\n";
		ret += tab + "Pre OSes:\n";
		if (preOrdereds != null)
			for (Ordered element : preOrdereds)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Post OSes:\n";
		if (postOrdereds != null)
			for (Ordered element : postOrdereds)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Parent CEUs:\n";
		if (parentCEUs != null)
			for (CEU element : parentCEUs)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "EU iterations:\n";
		if (euIterations != null)
			for (EU element : euIterations)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Connected CEUs:\n";
		if (connectedCEUs != null)
			for (CEU element : connectedCEUs)
				ret += tab + "   " + element.name + "\n";

		return ret;
	}

}
