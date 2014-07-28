package translator;

import sdComponents.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import comparators.OSComparator;

import enums.MessageType;
import enums.Operator;

/**
 * 
 * @author Rocky Slavin
 * 
 *         This class parses a single sequence diagram in XML format into an SD
 *         object.
 * 
 */
public class XMLParser {
	private static SD sequenceDiagram;
	private static int cfNum = 0; // TODO is this necessary?

	/**
	 * Parses file into dom and normalizes, then calls parseSequenceDiagram() to
	 * begin creation of sequence diagram components.
	 * 
	 * @param file
	 *            xml file containing sequence diagram
	 */
	public static void parse(File file) {
		Document dom;

		try {
			dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			dom.normalize();
			Element root = dom.getDocumentElement();

			// get list of SDs
			NodeList SDList = root.getElementsByTagName("sequenceDiagram");
			if (SDList != null && SDList.getLength() > 0)
				sequenceDiagram = parseSequenceDiagram((Element) SDList.item(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Parses SD object which consists of all other components. Other components
	 * are parsed through calls from this method.
	 * 
	 * @param xmlElement
	 *            Root sequence diagram element in DOM tree.
	 * @return Returns the root sequence diagram object.
	 */
	private static SD parseSequenceDiagram(Element xmlElement) {
		List<Lifeline> lifelines;
		List<CF> cfs;

		// populate lifeline list
		NodeList lifelineNodes = xmlElement.getElementsByTagName("lifeline");
		if (lifelineNodes != null && lifelineNodes.getLength() > 0) {
			lifelines = new ArrayList<Lifeline>();
			for (int i = 0; i < lifelineNodes.getLength(); i++) {
				Element lfe = (Element) lifelineNodes.item(i);
				if (lfe.getElementsByTagName("type") != null)
					lifelines.add(parseLifeline(lfe));
			}
		} else
			System.err.println("parseSequenceDiagram(): no lifelines found");

		// populate combined fragment list
		NodeList cfNodes = xmlElement.getElementsByTagName("combinedFragment");
		if (cfNodes != null && cfNodes.getLength() > 0) {
			cfs = new ArrayList<CF>();
			for (int i = 0; i < cfNodes.getLength(); i++)
				cfs.add(parseCF((Element) cfNodes.item(i)));
		} else
			System.err.println("parseSequenceDiagram(): no combined fragments found");

		// create SD object
		SD sequenceDiagram = new SD(xmlElement.getAttribute("name"), lifelines, cfs);

		// generate list of sending OSes (one OS per message)
		List<OS> allMessages = new ArrayList<OS>();
		for (int i = 0; i < lifelines.size(); i++) {
			Lifeline curLifeline = lifelines.get(i);
			for (int j = 0; j < curLifeline.oses.size(); j++) {
				OS curOS = curLifeline.oses.get(j);
				if (curOS.osType == OSType.SEND)
					allMessages.add(curOS);
			}
		}
		for (Lifeline curLifeline : lifelines) {
			for (OS curOS : curLifeline.oses)
				if (curOS.osType == OSType.SEND)
					allMessages.add(curOS);
		}

		// remove from allMessages any messages that appear in AllCFMsg
		// TODO not sure about this
		allMessages.removeAll(allCFMsg(sequenceDiagram.osesInCFs()));

		// TODO refactor break2Alt()
		// break2Alt(sequenceDiagram.osesInCFs(), allMessages);
		generateReceivingOSes();
		composeEU();
		sequenceDiagram.lifelines = projectCF2LifelineList(sequenceDiagram.lifelines, sequenceDiagram.cfs);

		return sequenceDiagram;
	}

	/**
	 * Parses a Lifeline from an xml element. Iterates through all messages
	 * belonging to the lifeline and extracts OSes.
	 * 
	 * @param xmlElement
	 *            Element representing the Lifeline in xml
	 * @return Lifeline object
	 */
	private static Lifeline parseLifeline(Element xmlElement) {
		Lifeline lifeline = new Lifeline(xmlElement.getAttribute("roleName"), xmlElement.getAttribute("type"));

		// parse OSes from messageEvent xml tags belonging to the Lifeline
		NodeList xmlMessageEvents = xmlElement.getElementsByTagName("messageEvent");
		if (xmlMessageEvents != null && xmlMessageEvents.getLength() > 0) {
			for (int i = 0; i < xmlMessageEvents.getLength(); i++) {
				lifeline.oses.add(parseOS((Element) xmlMessageEvents.item(i), lifeline));
			}
		} else
			System.err.println("parsLifeline(): No messages found for lifeline " + lifeline.name);

		return lifeline;
	}

	/**
	 * Parses a CF from an xml element. Lifelines MUST be parsed before calling
	 * parseCF().
	 * 
	 * @param xmlElement
	 *            Element representing the combined fragment in xml
	 * @return CF object
	 */
	private static CF parseCF(Element xmlElement) {
		// get Lifelines; parseCF MUST be called after Lifelines have been
		// parsed.
		List<Lifeline> lifelines = new ArrayList<Lifeline>();
		NodeList xmlLifelines = xmlElement.getElementsByTagName("lifelines");
		for (int i = 0; i < xmlLifelines.getLength(); i++) {
			// find Lifelines in sequenceDiagram that corresponds to the name
			// values in xmlLifelines
			Lifeline currentLifeline = sequenceDiagram.getLifeline(xmlLifelines.item(i).getNodeValue());
			if (currentLifeline != null)
				lifelines.add(currentLifeline);
			else
				System.err.println("parseCF(): Lifeline (" + xmlLifelines.item(i).getNodeValue()
						+ ") not found in parsed SD object.");
		}

		// get Operands
		List<Operand> operands = new ArrayList<Operand>();
		NodeList xmlOperands = xmlElement.getElementsByTagName("operand");
		for (int i = 0; i < xmlOperands.getLength(); i++)
			operands.add(parseOperand((Element) xmlOperands.item(i), lifelines));

		return new CF(Operator.getOperator(xmlElement.getAttribute("operator")), lifelines, operands);
	}

	/**
	 * Parses OS from xml messageEvent. ALL messageEvents in xml file are of
	 * MessageType SEND. RECEIVE OSes are calculated in composeLifeline().
	 * 
	 * @param xmlElement
	 *            Element representing the messageEvent in xml.
	 * @param lifeline
	 *            Lifeline to which the OS belongs to.
	 * @return OS portion of messageEvent belonging to lifeline.
	 */
	private static OS parseOS(Element xmlElement, Lifeline lifeline) {
		return new OS(lifeline, sequenceDiagram.getLifeline(xmlElement.getAttribute("receiver")),
				xmlElement.getAttribute("name"), Integer.parseInt(xmlElement.getAttribute("number")), OSType.RECEIVE,
				MessageType.getMessageType(xmlElement.getAttribute("type")));
	}

	/**
	 * Parses Operand from xml operand. Lifelines MUST be parsed before calling
	 * parseOperand().
	 * 
	 * @param xmlElement
	 *            Element representing the operand in xml.
	 * @param lifelines
	 *            Lifelines which take part of this Operand. These lifelines are
	 *            shared with the CF to which this Operand belongs to.
	 * @return Operand object.
	 */
	private static Operand parseOperand(Element xmlElement, List<Lifeline> lifelines) {
		// find combined fragments if they exist
		List<CF> combinedFragments = new ArrayList<CF>();
		NodeList xmlCombinedFragments = xmlElement.getElementsByTagName("combinedFragment");
		if (xmlCombinedFragments != null && xmlCombinedFragments.getLength() > 0)
			for (int i = 0; i < xmlCombinedFragments.getLength(); i++)
				combinedFragments.add(parseCF((Element) xmlCombinedFragments.item(i)));

		// find messageEvents
		List<Integer> msgNums = new ArrayList<Integer>();
		NodeList xmlMsgEvents = xmlElement.getElementsByTagName("messageEvents");
		if (xmlMsgEvents != null && xmlMsgEvents.getLength() > 0) {
			for (int i = 0; i < xmlMsgEvents.getLength(); i++)
				msgNums.add(Integer.parseInt(xmlMsgEvents.item(i).getNodeValue()));
			Collections.sort(msgNums);
		}

		// create Constraint
		Element xmlConstraint = (Element) xmlElement.getElementsByTagName("condition").item(0);
		Constraint constraint = new Constraint(xmlConstraint.getAttribute("name"), sequenceDiagram.getLifeline(xmlConstraint
				.getAttribute("lifeline")));

		return new Operand(constraint, new ArrayList<Lifeline>(lifelines), msgNums);
	}

	/**
	 * Adds receiving OSes to the Lifelines by iterating through other
	 * Lifelines. Lifelines MUST be parsed before calling
	 * generateReceivingOSes().
	 * 
	 * @param oldLifelines
	 *            Current List of Lifelines without receiving OSes.
	 * @return New list of Lifelines with receiving OSes.
	 */
	private static void generateReceivingOSes() {
		if (sequenceDiagram.lifelines != null && sequenceDiagram.lifelines.size() > 0) {
			// for each lifeline
			for (Lifeline outerLifeline : sequenceDiagram.lifelines) {
				// check all other lifelines
				for (Lifeline innerLifeline : sequenceDiagram.lifelines) {
					// check other lifelines to see if they send a message to
					// this lifeline
					for (OS os : innerLifeline.oses) {
						// if so, create a receiving OS on this lifeline
						if (os.connectedLifeline.equals(outerLifeline))
							outerLifeline.oses.add(new OS(outerLifeline, innerLifeline, os.name, os.number, OSType.RECEIVE,
									os.messageType));
					}
				}
				// reorder OSes to integrate new receiving OSes
				Collections.sort(outerLifeline.oses, new OSComparator());
				// TODO not sure why this is done
				outerLifeline.directedOSes = new ArrayList<OS>(outerLifeline.oses);
				// TODO not sure why this is done
				for (int i = 1; i < outerLifeline.oses.size() + 1; i++)
					outerLifeline.oses.get(i).location = i;
			}
		} else
			System.err.println("composeLifeline: null Lifeline list.)");
	}

	/**
	 * Calls findMsginEU() for each Lifeline in order to assign connectedParents
	 * to OSes. connectedParents and parents are important for mapping LTL
	 * formula variables to NuSMV model modules. Assigns connectedParents for
	 * all OSes. Lifelines MUST be parsed before calling composeEU().
	 */
	private static void composeEU() {
		for (Lifeline lifeline : sequenceDiagram.lifelines)
			findMsgInEU(lifeline, sequenceDiagram.cfs, 0, new ArrayList<String>()); // TODO
																					// change
																					// prototype
																					// (curLifeline,
		// layer)

		// TODO unsure about connectedParents
		for (Lifeline outerLifeline : sequenceDiagram.lifelines)
			for (OS outerOS : outerLifeline.oses)
				// check each other Lifeline
				for (Lifeline innerLifeline : sequenceDiagram.lifelines)
					// if the OS is connected to a Lifeline
					if (outerOS.connectedLifeline.equals(innerLifeline))
						// look the message(s) in the second Lifeline that make
						// the connection
						for (OS innerOS : innerLifeline.oses)
							if (outerOS.number == innerOS.number)
								// assign its parents as connected Parents to
								// the first OS
								outerOS.connectedParents = new ArrayList<String>(innerOS.parents);

	}

	/**
	 * Recursively adds parents to lifelines. findMsgInEU is important for
	 * mapping LTL formula variables to NuSMV modules.
	 * 
	 * @param lifeline
	 *            A single Lifeline. Generally, this should be called for all
	 *            lifelines.
	 * @param combinedFragments
	 *            Should be all combined fragments for initial call.
	 * @param layer
	 *            Should be 0 for initial call.
	 * @param parents
	 *            Should be empty for initial call.
	 */
	private static void findMsgInEU(Lifeline lifeline, List<CF> combinedFragments, int layer, List<String> parents) {
		// for each CF
		for (int i = 0; i < combinedFragments.size(); i++) {
			CF cf = combinedFragments.get(i);
			// check if the cf contains lifeline
			if (cf.lifelines.contains(lifeline)) {
				// if so, check each OS in the lifeline
				for (OS lifelineOS : lifeline.oses) {
					for (int j = 0; j < cf.operands.size(); j++) {
						Operand cfOperand = cf.operands.get(j);
						// recursion occurs here
						int newLayer = layer + 1;
						List<String> newParents = new ArrayList<String>(parents);
						String parentCF = cf.operator.toString() + (i + 1) + "_" + lifeline.name;
						newParents.add(parentCF);
						String parentOperand = cf.operator.toString() + (i + 1) + "_op" + (j + 1) + "_" + lifeline.name;
						newParents.add(parentOperand);
						for (Integer opMsgNum : cfOperand.msgNums) {
							if (lifelineOS.number == opMsgNum) {
								lifelineOS.parents = new ArrayList<String>(newParents);
								lifelineOS.layer = layer;
								lifeline.directedOSes.remove(lifelineOS);
								break;
							}
						}
						findMsgInEU(lifeline, cfOperand.cfs, newLayer, newParents);
					}
				}
			}
		}
	}
	

	}
}
