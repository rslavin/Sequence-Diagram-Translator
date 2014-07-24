package sdComponents;

import java.util.List;

//enumerated operators
enum Operator {
	ALT, OPT, PAR, LOOP, ASSERT, NEG, WEAK
};

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
	public List<CEU> ceuList;

}
