package sdComponents;

import java.util.ArrayList;
import java.util.List;

import translators.ltl.Utils;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a section of a Combined Fragment (CF). The number of
 *         Operands belonging to a combined fragment can depend on the
 *         Interaction Operator.
 * 
 */
public class Operand {
	public Constraint constraint;
	public CF cf;
	public List<CF> nestedCFs;
	public List<Lifeline> lifelines;
	public List<EU> eus;
	public List<Integer> msgNums;

	public Operand(Constraint constraint) {
		this.constraint = constraint;
		this.cf = null;
		this.lifelines = new ArrayList<Lifeline>();
		this.eus = new ArrayList<EU>();
		this.msgNums = new ArrayList<Integer>();
		this.nestedCFs = new ArrayList<CF>();
	}

	public Operand(Constraint constraint, List<Lifeline> lifelines, List<Integer> msgNums) {
		this.constraint = constraint;
		this.lifelines = lifelines;
		this.msgNums = msgNums;
		this.cf = null;
		this.lifelines = new ArrayList<Lifeline>();
		this.eus = new ArrayList<EU>();
		this.msgNums = new ArrayList<Integer>();
		this.nestedCFs = new ArrayList<CF>();
	}

	/**
	 * Generates list of OS objects belonging to the Operand.
	 * 
	 * @return ArrayList of OSes belonging to the Operand.
	 */
	public ArrayList<OS> getOSes() {
		ArrayList<OS> oses = new ArrayList<OS>();
		for (Lifeline lifeline : lifelines)
			for (OS os : lifeline.oses)
				if (msgNums.contains(os.number))
					oses.add(os);
		return oses;
	}

	/**
	 * Returns the first OS in the lifeline to which the operand's constraint
	 * belongs to.
	 * 
	 * @return
	 */
	public OS getFirstOS() {
		// generate candidates by finding intersection of oses in Operand
		// and oses in constraint's lifeline (EU)
		ArrayList<OS> candidates = new ArrayList<OS>();
		for (OS os : this.getOSes())
			if (constraint.lifeline.oses.contains(os))
				candidates.add(os);
		// find earliest os in candidates based on message number
		OS earliest = null;
		for (OS os : candidates)
			if (earliest == null || os.number < earliest.number)
				earliest = os;
		return earliest;
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = "   >>OPERAND<<\n";
		ret += tab + "Constraint: " + constraint.constraint + "\n";
		if (cf != null)
			ret += tab + "Combined Fragment: " + cf.num + "\n";
		ret += tab + "Nested combined fragments:\n";
		for (CF element : nestedCFs)
			ret += tab + "   " + element.num + "\n";
		ret += tab + "Lifelines:\n";
		for (Lifeline element : lifelines)
			ret += tab + "   " + element.name + "\n";
		ret += tab + "EUs: \n";
		for (EU element : eus)
			ret += tab + element.toString(tabs + 1) + "\n";
		ret += tab + "Message numbers:\n";
		for (int element : msgNums)
			ret += tab + "   " + element + "\n";

		return ret;
	}

	/**
	 * Generates list of preordereds for operand.
	 * 
	 * @return
	 */
	public ArrayList<Ordered> getPreOrdereds() {
		ArrayList<Ordered> preOrdereds = new ArrayList<Ordered>();
		for (CEU ceu : cf.ceus)
			preOrdereds.addAll(ceu.preOrdereds);
		return preOrdereds;
	}

	/**
	 * Returns the operand's constraint, converting else to conjunction of
	 * negation of other constraints when necessary.
	 * 
	 * @return
	 */
	public String getConstraint() {
		if (constraint.constraint.toLowerCase().equals("else")) {
			ArrayList<String> otherConstraints = new ArrayList<String>();
			for (Operand otherOp : cf.operands)
				if (!otherOp.equals(this))
					otherConstraints.add("!" + otherOp.constraint.constraint);
			return "(" + Utils.conjunct(otherConstraints) + ")";
		}
		return constraint.constraint;
	}

	/**
	 * Generates list of postordereds for operand.
	 * 
	 * @return
	 */
	public ArrayList<Ordered> getPostOrdereds() {
		ArrayList<Ordered> postOrdereds = new ArrayList<Ordered>();
		for (CEU ceu : cf.ceus)
			postOrdereds.addAll(ceu.postOrdereds);
		return postOrdereds;
	}

}
