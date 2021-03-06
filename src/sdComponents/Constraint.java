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
	public Operand operand;

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

		String ret = "   >>CONSTRAINT<<\n";
		ret += tab + "Constraint: " + constraint + "\n";
		ret += tab + "Lifeline: " + lifeline + "\n";
		ret += tab + "EU: " + eu.name + "\n";
		ret += tab + "EU Label: " + euLabel + "\n";
		ret += tab + "Number: " + num + "\n";
		ret += tab + "Max iteration: " + maxIteration + "\n";
		ret += tab + "Min iteration: " + minIteration + "\n";

		return ret;
	}

	/**
	 * Returns constraint, converting "else" into conjunction of negation of
	 * other constraints in the CF.
	 * 
	 * @return
	 */
	public String getConstraint() {
		return operand.getConstraint();
	}

}
