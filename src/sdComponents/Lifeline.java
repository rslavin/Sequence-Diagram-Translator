package sdComponents;

import java.util.List;
import enums.OSType;

/**
 * 
 * @author Rocky Slavin
 * 
 *         Represents a participating object in the Sequence Diagram. Contains
 *         sending and receiving OSes.
 * 
 */
public class Lifeline{
	public String name;
	public String type; // unsure

	// lists
	public List<OS> oses;
	public List<OS> directedOSes;
	public List<CEU> directedCEUs;
	public List<Lifeline> connectedLifelines;
	public List<Ordered> orderedElements;
	public List<String> states;
	public List<EU> criticals;
	public List<EU> assertions;

	public Lifeline(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public Lifeline(Lifeline clone) {
		this.name = clone.name;
		this.type = clone.type;
		this.oses = clone.oses;
		this.directedOSes = clone.directedOSes;
		this.directedCEUs = clone.directedCEUs;
		this.connectedLifelines = clone.connectedLifelines;
	}

	/**
	 * Checks if otherLifeline sends a message to this lifeline.
	 * 
	 * @param otherLifeline
	 *            Lifeline to check if is a sender.
	 * @return true if this lifeline is a receiver from otherLifeline, false
	 *         otherwise.
	 */
	public boolean receivesFrom(Lifeline otherLifeline) {
		for (OS os : otherLifeline.oses)
			if (os.connectedLifeline.equals(this) && os.osType.equals(OSType.SEND))
				return true;
		return false;
	}

	/**
	 * Checks if otherLifeline receives a message from this lifeline.
	 * 
	 * @param otherLifeline
	 *            Lifeline to check if is a receiver.
	 * @return true if this lifeline is a sender to otherLifeline, false
	 *         otherwise.
	 */
	public boolean sendsTo(Lifeline otherLifeline) {
		for (OS os : oses)
			if (os.connectedLifeline.equals(otherLifeline) && os.osType.equals(OSType.SEND))
				return true;
		return false;
	}

	/**
	 * Checks if this lifeline is connected to otherLifeline. Does not rely on
	 * OSType.SEND or OSType.RECEIVE. i.e., if receiving OSes haven't been
	 * generated, this method will still work.
	 * 
	 * @param otherLifeline
	 *            Lifeline to check if is connected.
	 * @return true if connected, false otherwise.
	 */
	public boolean isConnected(Lifeline otherLifeline) {
		for (OS os : oses)
			if (os.connectedLifeline.equals(otherLifeline))
				return true;
		for (OS os : otherLifeline.oses)
			if (os.connectedLifeline.equals(this))
				return true;
		return false;
	}
	
	public String toString(){
		return this.toString(0);
	}
	
	public String toString(int tabs){
		String tab = "";
		for(int i = 0; i < tabs; i++)
			tab += "   ";
		
		String ret = tab + "LIFELINE\n";
		ret += tab + "\tName: " + name + "\n";
		ret += tab + "\tType: " + type + "\n";
		ret += tab + "\tOSes:\n";
		for(OS element : oses)
			ret += tab + "\t\t" + element.toString(tabs + 1) + "\n";
		ret += tab + "\tDirected OSes:\n";
		for(OS element : directedOSes)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "Directed CEUs:\n";
		for(CEU element : directedCEUs)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tConnected Lifelines:\n";
		for(Lifeline element : connectedLifelines)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tOrdered Elements:\n";
		for(Ordered element : orderedElements)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tStates:\n";
		for(String element : states)
			ret += tab + "\t\t" + element + "\n";
		ret += tab + "\tCriticals:\n";
		for(EU element : criticals)
			ret += tab + "\t\t" + element.name + "\n";
		ret += tab + "\tOSes:\n";
		for(EU element : assertions)
			ret += tab + "\t\t" + element.name + "\n";		
		
		return ret;
	}

}
