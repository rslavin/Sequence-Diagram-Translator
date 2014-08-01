package sdComponents;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents an Interaction Constraint which must be true for an
 *         Operand to execute.
 * 
 */
public class Constraint {
	public String constraint;
	public Lifeline lifeline;
	public EU eu;
	public String euLabel;
	public int num;

	public int minIteration;
	public int maxIteration;

	public Constraint(String constraint, Lifeline lifeline) {
		this.constraint = constraint;
		this.lifeline = lifeline;
		eu = null;
		euLabel = null;
		num = -1;
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = tab + "CONSTRAINT\n";
		ret += tab + "\tConstraint: " + constraint + "\n";
		ret += tab + "\tLifeline: " + lifeline + "\n";
		ret += tab + "\tEU: " + eu.name + "\n";
		ret += tab + "\tEU Label: " + euLabel + "\n";
		ret += tab + "\tNumber: " + num + "\n";
		ret += tab + "\tMax iteration: " + maxIteration + "\n";
		ret += tab + "\tMin iteration: " + minIteration + "\n";

		return ret;
	}
}
