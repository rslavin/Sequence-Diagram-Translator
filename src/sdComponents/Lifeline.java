package sdComponents;

import java.util.ArrayList;
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
public class Lifeline {
	public String name;
	public String type; // unsure

	// lists
	public List<OS> oses;
	public List<OS> directedOSes; // OSes directly enclosed
	public List<CEU> directedCEUs; // CEUs directly enclosed
	public List<Lifeline> connectedLifelines;
	public List<Ordered> orderedElements;
	public List<String> states;
	public List<EU> criticals;
	public List<EU> assertions;

	public Lifeline(String name, String type) {
		this.name = name;
		this.type = type;
		this.oses = new ArrayList<OS>();
		this.directedOSes = new ArrayList<OS>();
		this.directedCEUs = new ArrayList<CEU>();
		this.connectedLifelines = new ArrayList<Lifeline>();
		this.orderedElements = new ArrayList<Ordered>();
		this.states = new ArrayList<String>();
		this.criticals = new ArrayList<EU>();
		this.assertions = new ArrayList<EU>();
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

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";

		String ret = "   >>LIFELINE<<\n";
		ret += tab + "Name: " + name + "\n";
		ret += tab + "Type: " + type + "\n";
		ret += tab + "OSes:\n";
		if (oses != null)
			for (OS element : oses)
				ret += tab + element.toString(tabs + 1) + "\n";

		ret += tab + "Directed OSes:\n";
		if (directedOSes != null)
			for (OS element : directedOSes)
				ret += tab + "   " + element.name + "\n";

		ret += tab + "Directed CEUs:\n";
		if (directedCEUs != null)
			for (CEU element : directedCEUs)
				ret += tab + "   " + element.name + "\n";

		ret += tab + "Connected Lifelines:\n";
		if (connectedLifelines != null)
			for (Lifeline element : connectedLifelines)
				ret += tab + "   " + element.name + "\n";

		ret += tab + "Ordered Elements:\n";
		if (orderedElements != null)
			for (Ordered element : orderedElements)
				ret += tab + "   " + element.name + "\n";

		ret += tab + "States:\n";
		if (states != null)
			for (String element : states)
				ret += tab + "   " + element + "\n";

		ret += tab + "Criticals:\n";
		if (criticals != null)
			for (EU element : criticals)
				ret += tab + "   " + element.name + "\n";

		ret += tab + "OSes:\n";
		if (assertions != null)
			for (EU element : assertions)
				ret += tab + "   " + element.name + "\n";

		return ret;
	}

}
