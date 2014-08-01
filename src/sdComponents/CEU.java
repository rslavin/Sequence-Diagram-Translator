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
	// TODO the following covered lists should be able to be derived from
	// coveredCFs.
	public List<String> coveredLifelineNames;
	public List<Operator> coveredOperators;
	public List<Constraint> coveredConstraints;
	public List<OS> preOSes;
	public List<OS> postOSes;
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
		preOSes = new ArrayList<OS>();
		postOSes = new ArrayList<OS>();
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
		if (eus != null && eus.size() != 0) {
			EU firstEU = eus.get(0);
			if (firstEU.directedOSes.size() > 0)
				return firstEU.directedOSes.get(0);
			else if (firstEU.directedCEUs.size() > 0)
				return firstEU.directedCEUs.get(0).getFirstOS();
		}
		return null;
	}

	public String toString() {
		String ret = "CEU\n";
		ret += "\tName: " + name + "\n";
		ret += "\tCombined Fragment: " + cf.num + "\n";
		ret += "\tHas critical child: " + hasCriticalChild;
		ret += "\tFirst OS: " + firstOS.name;
		ret += "\tIteration: " + iteration;
		ret += "\tIs parent of parent: " + isParentPar;
		ret += "\tEUs:\n";
		for (EU element : eus)
			ret += "\t\t" + element.name + "\n";
		ret += "\tCovered Lifeline Names:\n";
		for (String element : coveredLifelineNames)
			ret += "\t\t" + element + "\n";
		ret += "\tCovered Operators:\n";
		for (Operator element : coveredOperators)
			ret += "\t\t" + element + "\n";
		ret += "\tCovered Constraints:\n";
		for (Constraint element : coveredConstraints)
			ret += "\t\t" + element.constraint + "\n";
		ret += "\tPre OSes:\n";
		for (OS element : preOSes)
			ret += "\t\t" + element.name + "\n";
		ret += "\tPost OSes:\n";
		for (OS element : postOSes)
			ret += "\t\t" + element.name + "\n";
		ret += "\tParent CEUs:\n";
		for (CEU element : parentCEUs)
			ret += "\t\t" + element.name + "\n";
		ret += "\tEU iterations:\n";
		for (EU element : euIterations)
			ret += "\t\t" + element.name + "\n";
		ret += "\tConnected CEUs:\n";
		for (CEU element : connectedCEUs)
			ret += "\t\t" + element.name + "\n";

		return ret;
	}

}
