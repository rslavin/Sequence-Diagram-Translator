package sdComponents;

import java.util.List;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a Compositional Execution Unit. CEUs are projections of
 *         CFs onto a Lifeline. Each operand projects an execution unit (EU) on
 *         a Lifeline. A CEU is associated with a single Lifeline and may be
 *         composed of multiple EUs
 * 
 */
public class CEU {
	public Lifeline lifeline; // Lifeline to which this CEU belongs
	public Operator operator; // enum Operator type in CF
	public List<EU> eus; // list of EUs
	
	// lists
	//TODO the following covered lists should be able to be derived from coveredCFs.
	public List<Lifeline> coveredLifelines;
	public List<Operator> coveredOperators; 
	public List<Constraint> coveredConstraints;
	public List<OS> preOSes;
	public List<OS> postOSes;
	public List<CEU> parentCEUs;
	
	public boolean hasCriticalChild; 
	public OS firstOS; // was "int firstOSLocation"	
	
	public boolean isParentPar; // unsure
	public List<EU> euIteration; // unsure
	public List<CEU> connectedCEUs; //unsure


}
