package sdComponents;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import translators.XMLParser;
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
		this.constraints = new ArrayList<Constraint>();
	}

	public OS(Lifeline lifeline, String name, int number, OSType osType, MessageType messageType) {
		this.lifeline = lifeline;
		this.name = name;
		this.number = number;
		this.osType = osType;
		this.messageType = messageType;
		this.iteration = 0;
		this.constraints = new ArrayList<Constraint>();
	}

	public void parseConnectedLifeline(NodeList lifelineNodes, SD sequenceDiagram) {
		if (lifelineNodes != null && lifelineNodes.getLength() > 0) {
			for (int i = 0; i < lifelineNodes.getLength(); i++) {
				Element lfe = (Element) lifelineNodes.item(i);
				if (lfe.getElementsByTagName("messageEvent") != null) {
					NodeList messageEvents = lfe.getElementsByTagName("messageEvent");
					for (int j = 0; j < messageEvents.getLength(); j++) {
						if (XMLParser.elementValue((Element) messageEvents.item(j), "number").equals(Integer.toString(number)))
							connectedLifeline = sequenceDiagram.getLifeline(XMLParser.elementValue(
									(Element) messageEvents.item(j), "receiver"));

					}
				}
			}
		}
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

	public String toString() {
		return this.toString(0);
	}

	public String toString(int tabs) {
		String tab = "";
		for (int i = 0; i < tabs; i++)
			tab += "   ";
		String ret = "   >>OS<<\n";
		ret += tab + "Message name: " + name + "\n";
		ret += tab + "Number: " + number + "\n";
		if (lifeline != null)
			ret += tab + "Lifeline: " + lifeline.name + "\n";
		if (connectedLifeline != null)
			ret += tab + "Connected Lifeline: " + connectedLifeline.name + "\n";
		if (osType != null)
			ret += tab + "OS Type: " + osType + "\n";
		if (messageType != null)
			ret += tab + "Message Type: " + messageType + "\n";
		ret += tab + "Layer: " + layer + "\n";
		ret += tab + "Location: " + location + "\n";
		ret += tab + "Iteration: " + iteration + "\n";
		ret += tab + "Constraints:\n";
		if (constraints != null)
			for (Constraint element : constraints)
				ret += tab + "   " + element.constraint + "\n";
		ret += tab + "Parents:\n";
		if (parents != null)
			for (String element : parents)
				ret += tab + "   " + element + "\n";
		ret += tab + "Connected parents:\n";
		if (connectedParents != null)
			for (String element : connectedParents)
				ret += tab + "   " + element + "\n";

		return ret;
	}

	/**
	 * Generates string for use in LTL formulas. Embeds OSType into name.
	 * 
	 * @return
	 */
	public String ltlString() {
		switch (osType) {
		case SEND:
			return "OS_s_" + name;
		default:
			return "OS_r_" + name;
		}
	}

	/**
	 * Generates string for use in LTL formulas where the opposite OS is needed.
	 * 
	 * @return
	 */
	public String ltlStringOp() {
		switch (osType) {
		case SEND:
			return "OS_r_" + name;
		default:
			return "OS_s_" + name;
		}
	}
}
