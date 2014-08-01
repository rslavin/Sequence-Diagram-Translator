package sdComponents;

import java.util.List;

import enums.*;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents an Occurrence Specification, denoting a sending or
 *         receiving event. Ordering must be maintained in order to calculate
 *         pre- and post- OSes. For this reason, OS extends Ordered along with
 *         CEU.
 */
public class OS extends Ordered {
	public Lifeline connectedLifeline; // lifeline at other end of message
	public int number;
	public OSType osType;
	public MessageType messageType;
	public List<Constraint> constraints;
	public int layer; // for use with NuSMV modules
	public int location; // unsure what this is
	public List<String> parents;// for use with NuSMV modules
	public List<String> connectedParents; // for use with NuSMV modules
	public int iteration;

	public OS(Lifeline lifeline, Lifeline connectedLifeline, String name, int number, OSType osType, MessageType messageType) {
		this.lifeline = lifeline;
		this.connectedLifeline = connectedLifeline;
		this.name = name;
		this.number = number;
		this.osType = osType;
		this.messageType = messageType;
		this.iteration = 0;
	}

	/**
	 * Generates label for statelist depending on if the OS sends or receives.
	 * 
	 * @return "s_[name]" or "r_[name]"
	 */
	public String getStateLabel() {
		switch (osType) {
		case SEND:
			return "s_" + name;
		case RECEIVE:
			return "r_" + name;
		default:
			return null;
		}
	}

	public String toString(int tabs) {
		String ret = "OS\n";
		ret += "\tNumber: " + number + "\n";
		ret += "\tLifeline: " + connectedLifeline + "\n";
		ret += "\tOS Type: " + osType + "\n";
		ret += "\tMessage Type: " + messageType + "\n";
		ret += "\tLayer: " + layer + "\n";
		ret += "\tLocation: " + location + "\n";
		ret += "\tIteration: " + iteration + "\n";
		ret += "\tConstraints:\n";
		for (Constraint element : constraints)
			ret += "\t\t" + element.constraint + "\n";
		ret += "\tParents:\n";
		for (String element : parents)
			ret += "\t\t" + element + "\n";
		ret += "\tConnected parents:\n";
		for (String element : connectedParents)
			ret += "\t\t" + element + "\n";

		return ret;
	}

}
