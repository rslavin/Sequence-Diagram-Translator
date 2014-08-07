package sdComponents;

import java.util.ArrayList;
import java.util.List;

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

}
