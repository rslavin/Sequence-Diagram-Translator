package sdComponents;

import java.util.List;
import enums.*;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents an Occurrence Specification, denoting a sending or
 *         receiving event.
 */
public class OS {
	public Lifeline lifeline;
	public String name;
	public int number;
	public OSType osType;
	public MessageType messageType;
	public List<Constraint> constraints;
	public int layer;

}
