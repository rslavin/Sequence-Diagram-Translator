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
	public Lifeline lifeline; // lifeline to which os belongs to
	public Lifeline connectedLifeline; // lifeline at other end of message
	public String name;
	public int number;
	public OSType osType;
	public MessageType messageType;
	public List<Constraint> constraints;
	public int layer;
	public int location; // unsure what this is
	public List<String> parents;// unsure what this is
	public List<String> connectedParents; // unsure what this is
	

	public OS(Lifeline lifeline, Lifeline connectedLifeline, String name, int number, OSType osType, MessageType messageType) {
		this.lifeline = lifeline;
		this.connectedLifeline = connectedLifeline;
		this.name = name;
		this.number = number;
		this.osType = osType;
		this.messageType = messageType;
	}

}
