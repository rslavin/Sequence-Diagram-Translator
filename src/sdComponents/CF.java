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
			if (op.cfs != null)
				for (CF opCF : op.cfs)
					msgs.addAll(opCF.getAllMsgNums());
		}
		return msgs;
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = "   >>CF<<\n";
		ret += tab + "Number: " + num + "\n";
		if (operator != null)
			ret += tab + "Operator: " + operator + "\n";
		if (firstOS != null)
			ret += tab + "First OS: " + firstOS.name + "\n";
		ret += tab + "Lifelines:\n";
		if (lifelines != null)
			for (Lifeline element : lifelines)
				ret += tab + "   " + element.name + "\n";
		ret += tab + "Operands:\n";
		if (operands != null)
			for (Operand element : operands)
				ret += tab + element.toString(tabs + 1) + "\n";
		ret += tab + "CEUs:\n";
		if (ceus != null)
			for (CEU element : ceus)
				ret += tab + element.toString(tabs + 1) + "\n";
		ret += tab + "OSes:\n";
		if (oses != null)
			for (OS element : oses)
				ret += tab + "   " + element.name + "\n";
		ret += "Iterations:\n";
		if (operands != null)
			for (Operand element : operands)
				ret += tab + "   " + element.constraint.constraint + "\n";

		return ret;
	}

}
