package sdComponents;

import java.util.List;


/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents an Execution Unit which belongs to a single Compositional
 *         Execution Unit (CEU) and may contain nested CEUs corresponding to
 *         nested Combined Fragments (CFs).
 * 
 */
public class EU {
	public Lifeline lifeline;
	public Constraint constraint;
	
	// lists
	public List<OS> directedOSes;
	public List<CEU> directedCEUs;
	public List<EU> connectedEUs;
	public List<EU> coveredEUs;
	
	public List<OS> ignoreOSes;
	public List<OS> consideredOSes;
	public List<OS> allPossibleOSes;

}
