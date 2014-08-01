package sdComponents;

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
	public List<CF> cfs;
	public List<Lifeline> lifelines;
	public List<EU> eus;
	public List<Integer> msgNums;

	public Operand(Constraint constraint, List<Lifeline> lifelines, List<Integer> msgNums) {
		this.constraint = constraint;
		this.lifelines = lifelines;
		this.msgNums = msgNums;
	}
	
	public String toString(){
		String ret = "OPERAND\n";
		ret += "\tConstraint: " + constraint.constraint + "\n";
		ret += "\tCombined fragments:\n";
		for (CF element : cfs)
			ret += "\t\t" + element.num + "\n";
		ret += "\tLifelines:\n";
		for (Lifeline element : lifelines)
			ret += "\t\t" + element.name + "\n";
		ret += "\tEUs: \n";
		for (EU element : eus)
			ret += "\t\t" + element.name + "\n";
		ret += "\tMessage numbers:\n";
		for (int element : msgNums)
			ret += "\t\t" + element + "\n";
		
		return ret;
	}

}
