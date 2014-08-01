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
		return this.toString(0);
	}
	
	public String toString(int tabs){
		String tab = "";
		for(int i = 0; i < tabs; i++)
			tab += "   ";
		
		String ret = tab + "OPERAND\n";
		ret += tab + "\tConstraint: " + constraint.constraint + "\n";
		ret += tab + "\tCombined fragments:\n";
		for (CF element : cfs)
			ret += tab + "\t\t" + element.num + "\n";
		ret += tab + "\tLifelines:\n";
		for (Lifeline element : lifelines)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tEUs: \n";
		for (EU element : eus)
			ret += tab + "\t\t" + element.toString(tabs + 1) + "\n";
		ret += tab + "\tMessage numbers:\n";
		for (int element : msgNums)
			ret += tab + "\t\t" + element + "\n";
		
		return ret;
	}

}
