package sdComponents;

import java.util.List;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a participating object in the Sequence Diagram. Contains
 *         sending and receiving OSes.
 * 
 */
public class Lifeline {
	public String name;
	public String type; // unsure

	// lists
	public List<OS> oses;
	public List<OS> directedOSes;
	public List<CEU> directedCEUs;
	public List<Lifeline> connectedLifelines;

}
