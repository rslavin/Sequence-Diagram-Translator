package sdComponents;

import java.util.ArrayList;
import java.util.List;

import enums.Operator;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a Combined Fragment consisting of an Interaction Operator
 *         and one or more Operands depending on the type of CF.
 * 
 */
public class CF {
	public Operator operator;
	public List<Lifeline> lifelines;
	public List<Operand> operands;
	public OS firstOS; // was "int firstMsgNum"
	public List<CEU> ceus;
	public List<OS> oses;
	public int num;
	public List<Operand> iterations;

	public CF(Operator operator, List<Lifeline> lifelines, List<Operand> operands) {
		this.operator = operator;
		this.lifelines = lifelines;
		this.operands = operands;
		firstOS = null;
		ceus = new ArrayList<CEU>();
		oses = new ArrayList<OS>();
		iterations = new ArrayList<Operand>();
		num = -1;
	}

	/**
	 * Returns all message numbers from the CF and nested CFs
	 * 
	 * @return Total message numbers.
	 */
	public ArrayList<Integer> getAllMsgNums() {
		ArrayList<Integer> msgs = new ArrayList<Integer>();
		for (Operand op : operands) {
			msgs.addAll(op.msgNums);
			for (CF opCF : op.cfs)
				msgs.addAll(opCF.getAllMsgNums());
		}
		return msgs;
	}

	public String toString() {
		String ret = "CF\n";
		ret += "\tNumber: " + num + "\n";
		ret += "\tOperator: " + operator + "\n";
		ret += "\tFirst OS: " + firstOS.name + "\n";
		ret += "\tLifelines:\n";
		for (Lifeline element : lifelines)
			ret += "\t\t" + element.name + "\n";
		ret += "\tOperands:\n";
		for (Operand element : operands)
			ret += "\t\t" + element.constraint.constraint + "\n";
		ret += "\tCEUs:\n";
		for (CEU element : ceus)
			ret += "\t\t" + element.name + "\n";
		ret += "\tOSes:\n";
		for (OS element : oses)
			ret += "\t\t" + element.name + "\n";
		ret += "\tIterations:\n";
		for (Operand element : operands)
			ret += "\t\t" + element.constraint.constraint + "\n";

		return ret;
	}

}
