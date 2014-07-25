package sdComponents;

import java.util.ArrayList;
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
	public List<CF> cfs; 
	
	public SD(String name, List<Lifeline> lifelines, List<CF> cfs) {
		super();
		this.name = name;
		this.lifelines = lifelines;
		this.cfs = cfs;
	}
	
	public List<OS> osesInCFs(){
		List<OS> allOSes = new ArrayList<OS>();
		for(CF cf : cfs){
			for(OS os: cf.oses){
				allOSes.add(os);
			}
		}
		return allOSes;
	}
	
	

}
