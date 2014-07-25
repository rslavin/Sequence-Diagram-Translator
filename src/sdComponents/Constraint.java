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

	public Constraint(String constraint, Lifeline lifeline) {
		this.constraint = constraint;
		this.lifeline = lifeline;
	}
}
