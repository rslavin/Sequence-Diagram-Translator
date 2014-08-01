package sdComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves to maintain OS and CEU ordering.
 * @author Rocky
 *
 */
public class Ordered {
	public Lifeline lifeline;
	public List<Ordered> preOrdereds;
	public List<Ordered> postOrdereds;
	public String name;
	
	public Ordered(){
		lifeline = null;
		preOrdereds = new ArrayList<Ordered>();
		postOrdereds = new ArrayList<Ordered>();
	}
}
