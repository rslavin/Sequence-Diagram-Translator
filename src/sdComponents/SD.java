package sdComponents;

import java.util.List;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a Sequence Diagram composed of Lifelines and Combined
 *         Fragments (CF).
 */
public class SD {
	public String name;
	public List<Lifeline> lifelines;
	public List<OS> osesInCFs; // TODO is this correct? Should it be CFs?
	
	public SD(String name, List<Lifeline> lifelines, List<CF> cfs) {
		super();
		this.name = name;
		this.lifelines = lifelines;
		this.cfs = cfs;
	}
	
	

}
