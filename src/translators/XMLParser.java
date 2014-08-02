package translators;

import sdComponents.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import comparators.OSComparator;
import enums.*;

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
	private static int cfNum = 1;

	/**
	 * Parses file into dom and normalizes, then calls parseSequenceDiagram() to
	 * begin creation of sequence diagram components.
	 * 
	 * @param file
	 *            xml file containing sequence diagram
	 */
	public static SD parse(File file) {
		Document dom;

		try {
			dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			dom.normalize();
			Element root = dom.getDocumentElement();

			// get list of SDs
			NodeList SDList = root.getElementsByTagName("sequenceDiagram");
			if (SDList != null && SDList.getLength() > 0)
				parseSequenceDiagram((Element) SDList.item(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sequenceDiagram;
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

		sequenceDiagram = new SD(elementValue(xmlElement, "name"));
		// populate lifeline list
		NodeList lifelineNodes = xmlElement.getElementsByTagName("lifeline");
		if (lifelineNodes != null && lifelineNodes.getLength() > 0) {
			for (int i = 0; i < lifelineNodes.getLength(); i++) {
				Element lfe = (Element) lifelineNodes.item(i);
				if (lfe.getElementsByTagName("type").getLength() > 0)
					sequenceDiagram.lifelines.add(parseLifeline(lfe));
			}
		} else
			System.err.println("parseSequenceDiagram(): no lifelines found");

		// populate combined fragment list
		NodeList cfNodes = xmlElement.getElementsByTagName("combinedFragment");
		if (cfNodes != null && cfNodes.getLength() > 0) {
			for (int i = 0; i < cfNodes.getLength(); i++)
				sequenceDiagram.cfs.add(parseCF((Element) cfNodes.item(i)));
		} else
			System.err.println("parseSequenceDiagram(): no combined fragments found");

		// generate list of sending OSes (one OS per message)
		List<Integer> allMessages = new ArrayList<Integer>();
		for (int i = 0; i < sequenceDiagram.lifelines.size(); i++) {
			Lifeline curLifeline = sequenceDiagram.lifelines.get(i);
			for (int j = 0; j < curLifeline.oses.size(); j++) {
				OS curOS = curLifeline.oses.get(j);
				if (curOS.osType == OSType.SEND)
					allMessages.add(curOS.number);
			}
		}
		for (Lifeline curLifeline : sequenceDiagram.lifelines)
			for (OS curOS : curLifeline.oses)
				if (curOS.osType == OSType.SEND)
					allMessages.add(curOS.number);

		// parse connected lifelines
		for (Lifeline curLifeline : sequenceDiagram.lifelines)
			for (OS curOS : curLifeline.oses)
				curOS.parseConnectedLifeline(lifelineNodes, sequenceDiagram);

		// remove from allMessages any messages that appear in AllCFMsg
		allMessages.removeAll(getAllCFMsg((ArrayList<CF>) sequenceDiagram.cfs));

		// break2Alt(sequenceDiagram.osesInCFs(), allMessages);
		generateReceivingOSes();
		composeEU();
		projectCFToLifelines();

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
		Lifeline lifeline = new Lifeline(elementValue(xmlElement, "roleName"), elementValue(xmlElement, "type"));
		// parse OSes from messageEvent xml tags belonging to the Lifeline
		NodeList xmlMessageEvents = xmlElement.getElementsByTagName("messageEvent");
		if (xmlMessageEvents != null && xmlMessageEvents.getLength() > 0) {
			for (int i = 0; i < xmlMessageEvents.getLength(); i++) {
				lifeline.oses.add(parseOS((Element) xmlMessageEvents.item(i), lifeline));
			}
		} else
			System.err.println("parseLifeline(): No messages found for lifeline " + lifeline.name);

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
		NodeList xmlLifelineGroup = xmlElement.getElementsByTagName("lifelines");
		NodeList xmlLifelines = ((Element) xmlLifelineGroup.item(0)).getElementsByTagName("lifeline");
		for (int i = 0; i < xmlLifelines.getLength(); i++) {
			// find Lifelines in sequenceDiagram that corresponds to the name
			// values in xmlLifelines
			Lifeline currentLifeline = sequenceDiagram.getLifeline(xmlLifelines.item(i).getFirstChild().getNodeValue());
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

		return new CF(Operator.getOperator(elementValue(xmlElement, "operator")), lifelines, operands, cfNum++);
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
		return new OS(lifeline, elementValue(xmlElement, "name"), Integer.parseInt(elementValue(xmlElement, "number")),
				OSType.SEND, MessageType.getMessageType(elementValue(xmlElement, "type")));
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
		NodeList xmlMsgEventsList = xmlElement.getElementsByTagName("messageEvents");
		NodeList xmlMsgEvents = ((Element) xmlMsgEventsList.item(0)).getElementsByTagName("number");
		if (xmlMsgEvents != null && xmlMsgEvents.getLength() > 0) {
			for (int i = 0; i < xmlMsgEvents.getLength(); i++) {
				msgNums.add(Integer.parseInt(xmlMsgEvents.item(i).getFirstChild().getNodeValue()));
			}
			Collections.sort(msgNums);
		}

		// create Constraint
		Element xmlConstraint = (Element) xmlElement.getElementsByTagName("condition").item(0);
		Constraint constraint = new Constraint(elementValue(xmlConstraint, "name"), sequenceDiagram.getLifeline(elementValue(
				xmlConstraint, "lifeline")));
		Operand op = new Operand(constraint);
		op.lifelines = new ArrayList<Lifeline>(lifelines);
		op.msgNums = msgNums;
		return op;
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
				for (int i = 0; i < outerLifeline.oses.size(); i++)
					outerLifeline.oses.get(i).location = i + 1;
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
			findMsgInEU(lifeline, sequenceDiagram.cfs, 0, new ArrayList<String>());

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
								if (innerOS.parents != null)
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
						if (cfOperand.cfs != null && cfOperand.cfs.size() > 0)
							findMsgInEU(lifeline, cfOperand.cfs, newLayer, newParents);
					}
				}
			}
		}
	}

	/**
	 * Associates relevant information to lifelines for combined fragment
	 * projection.
	 */
	private static void projectCFToLifelines() {
		List<CEU> allCEUs = new ArrayList<CEU>();
		for (Lifeline lifeline : sequenceDiagram.lifelines) {
			lifeline.directedCEUs = generateCEUs(lifeline, (ArrayList<CF>) sequenceDiagram.cfs, new ArrayList<String>(),
					new ArrayList<CEU>());
			lifeline.orderedElements = buildOrdered((ArrayList<OS>) lifeline.directedOSes, (ArrayList<CEU>) lifeline.directedCEUs);
			lifeline.states = buildStates((ArrayList<Ordered>) lifeline.orderedElements);

			allCEUs.addAll(lifeline.directedCEUs);

			buildConstraints((ArrayList<CEU>) lifeline.directedCEUs, new ArrayList<Constraint>());
			// lifeline.directedCEUs = considerToIgnore(lifeline.directedCEUs);
		}

		buildConnectedEUs((ArrayList<CF>) sequenceDiagram.cfs);
		buildLoops((ArrayList<CF>) sequenceDiagram.cfs);

		for (Lifeline lifeline : sequenceDiagram.lifelines) {
			// add connectedLifelines (all?)
			for (Lifeline connectedLifeline : sequenceDiagram.lifelines)
				if (!lifeline.equals(connectedLifeline)) // TODO correct?
					lifeline.connectedLifelines.add(connectedLifeline);
			lifeline.criticals = buildCriticals((ArrayList<CEU>) lifeline.directedCEUs, new ArrayList<EU>());
			lifeline.assertions = buildAssertions((ArrayList<CEU>) lifeline.directedCEUs, new ArrayList<EU>());
		}
	}

	/**
	 * Generates CEUs for lifeline.
	 * 
	 * @param lifeline
	 *            Lifeline to generate CEUs for.
	 * @param combinedFragments
	 *            All CFs for initial call. Recursive calls modify this for
	 *            nested CFs.
	 * @param parents
	 *            Labels for parents of nested CEUs. Empty for first call.
	 * @param ceuParents
	 *            Labels for Operand parent CEUs Empty for first call.
	 * @return
	 */
	private static ArrayList<CEU> generateCEUs(Lifeline lifeline, ArrayList<CF> combinedFragments, ArrayList<String> parents,
			ArrayList<CEU> ceuParents) {

		ArrayList<CEU> lifelineCEUs = new ArrayList<CEU>();
		for (CF cf : combinedFragments) {
			// find each instance of lifeline in all CFs
			for (Lifeline cfLifeline : cf.lifelines) {
				// create ceu projection onto lifeline
				if (cfLifeline.equals(lifeline)) {
					CEU ceu = new CEU(cf);
					ceu.lifeline = cfLifeline;
					ceu.name = cf.operator.toString() + cf.num + "_" + cfLifeline.name;

					// add coveredLifelineNames to CEU
					for (Lifeline cfCoveredLifeline : cf.lifelines) {
						if (cfCoveredLifeline != cfLifeline) { // skip this
																// lifeline
							if (parents.size() == 0)
								ceu.coveredLifelineNames.add(cfCoveredLifeline.name);
							else
								ceu.coveredLifelineNames.add(cf.operator.toString() + cf.num + "_" + cfCoveredLifeline.name);
						}
					}

					// add prefix for next recurse
					parents.add(cf.operator.toString() + cf.num + "_");

					// add parent prefix to covered lifeline names
					for (Lifeline cfCoveredLifeline : cf.lifelines) {
						if (cfCoveredLifeline != cfLifeline) // skip
							ceu.coveredLifelineNames.add(parents.get(parents.size() - 1) + cfCoveredLifeline.name);
					}

					for (int i = 0; i < cf.operands.size(); i++) {
						Operand cfOperand = cf.operands.get(i);
						cfOperand.constraint.num = i + 1;
						ceu.coveredConstraints.add(cfOperand.constraint);
						// create new EU
						EU eu = new EU();
						eu.operand = cfOperand;
						eu.lifeline = cfLifeline;
						eu.name = cf.operator.toString() + cf.num + "_op" + (i + 1) + "_" + cfLifeline.name;
						eu.operand.constraint.euLabel = cf.operator.toString() + cf.num + "_"
								+ eu.operand.constraint.lifeline.name + ".op" + (i + 1);

						parents.add("op" + (i + 1) + "_");

						for (Lifeline cfCoveredLifeline : cf.lifelines)
							if (cfCoveredLifeline != cfLifeline)
								eu.coveredEULabels.add(parents.get(parents.size() - 2) + parents.get(parents.size() - 1)
										+ cfCoveredLifeline.name);

						for (int msgNum : cfOperand.msgNums) {
							for (OS os : cfLifeline.oses) {
								if (os.number == msgNum) {
									eu.directedOSes.add(os);
									break;
								}
							}
						}

						// handle nested cfs
						if (cfOperand.cfs.size() > 0) {
							List<CEU> opParentCEUs = new ArrayList<CEU>();
							opParentCEUs.add(ceu);
							eu.directedCEUs = generateCEUs(cfLifeline, (ArrayList<CF>) cfOperand.cfs, parents,
									(ArrayList<CEU>) opParentCEUs);
						}
						ceu.eus.add(eu);
						cfOperand.eus.add(eu);
						parents.remove(parents.size() - 1);
					}
					cf.ceus.add(ceu);
					lifelineCEUs.add(ceu);
					parents.remove(parents.size() - 1);
					break;
				}
			}
		}
		return lifelineCEUs;
	}

	/**
	 * Generates pre and post OS information.
	 * 
	 * @param oses
	 *            ArrayList of OSes (subclass of Ordered).
	 * @param ceus
	 *            ArrayList of CEUs (subclass of Ordered).
	 * @return Combined ArrayList of oses and ceus in order with elements
	 *         associated with relevant pre and post Ordereds.
	 */
	private static ArrayList<Ordered> buildOrdered(ArrayList<OS> oses, ArrayList<CEU> ceus) {
		ArrayList<Ordered> ordereds = new ArrayList<Ordered>(oses);

		// add ceus to ordered in order
		for (CEU ceu : ceus) {
			if (ordereds.size() == 0)
				ordereds.add(ceu);
			else {
				for (int i = 0; i < ordereds.size(); i++) {
					Ordered ord = ordereds.get(i);
					if (ceu.getFirstOS() != null) {
						if (ord instanceof OS) {
							OS tempOS = (OS) ord;
							if (ceu.getFirstOS().number < tempOS.number) {
								ordereds.add(i, ceu);
								break;
							} else if (i == ordereds.size() - 1) {
								ordereds.add(ceu);
								break;
							}
						} else { // CEU
							CEU tempCEU = (CEU) ord;
							if (ceu.getFirstOS().number < tempCEU.getFirstOS().number) {
								ordereds.add(i, ceu);
								break;
							} else if (i == ordereds.size() - 1) {
								ordereds.add(ceu);
								break;
							}
						}
					}
				}
			}
			for (EU ceuEU : ceu.eus)
				ceuEU.ordereds = buildOrdered((ArrayList<OS>) ceuEU.directedOSes, (ArrayList<CEU>) ceuEU.directedCEUs);
		}
		// generate pre and post Ordereds for ordereds list (list is single
		// lifeline)
		if (ordereds.size() > 1) {
			for (int i = 0; i < ordereds.size(); i++) {
				Ordered ord = ordereds.get(i);
				if (ord instanceof CEU) {
					if (i == 0) // first
						ord.postOrdereds.add(ordereds.get(i + 1));
					else if (i == ordereds.size() - 1)
						ord.preOrdereds.add(ordereds.get(i - 1));
					else if (ordereds.size() > 2) {
						ord.preOrdereds.add(ordereds.get(i - 1));
						ord.postOrdereds.add(ordereds.get(i + 1));
					}
				}
			}
		}
		return ordereds;
	}

	/**
	 * Assigns state labels for a list of ordereds.
	 * 
	 * @param ordereds
	 *            Ordereds for which state labels should be generated.
	 * @return List of labels.
	 */
	private static ArrayList<String> buildStates(ArrayList<Ordered> ordereds) {
		ArrayList<String> states = new ArrayList<String>();
		states.add("sinit");

		for (Ordered ordered : ordereds) {
			if (ordered instanceof OS) {
				states.add(((OS) ordered).getStateLabel());
			} else if (ordered instanceof CEU) {
				CEU ceu = (CEU) ordered;
				for (EU ceuEU : ceu.eus)
					ceuEU.states = buildStates((ArrayList<Ordered>) ceuEU.ordereds);
			}
		}
		return states;
	}

	/**
	 * Builds connected EUs for cfs and nested cfs.
	 * 
	 * @param cfs
	 *            List of CFs to build connected EUs.
	 */
	private static void buildConnectedEUs(ArrayList<CF> cfs) {
		for (CF cf : cfs) {
			// connect CEUs
			for (CEU cfCEU : cf.ceus)
				for (CEU cfCEU2 : cf.ceus)
					if (!cfCEU.equals(cfCEU2))
						if (cfCEU.cf.num == cfCEU2.cf.num)
							cfCEU.connectedCEUs.add(cfCEU2);

			// connect EUs
			for (CEU cfCEU : cf.ceus)
				for (int i = 0; i < cfCEU.eus.size(); i++) {
					EU ceuEU = cfCEU.eus.get(i);
					for (CEU connectedCEU : cfCEU.connectedCEUs)
						ceuEU.connectedEUs.add(connectedCEU.eus.get(i));
				}

			for (Operand op : cf.operands)
				buildConnectedEUs((ArrayList<CF>) op.cfs);
		}
	}

	/**
	 * Propogates constraints to OSes
	 * 
	 * @param ceus
	 *            List of CEUs to propagate constraints for.
	 * @param parentConstraints
	 *            Parent constraints to propagate.
	 */
	private static void buildConstraints(ArrayList<CEU> ceus, ArrayList<Constraint> parentConstraints) {
		for (CEU ceu : ceus) {
			for (EU ceuEU : ceu.eus) {
				ArrayList<Constraint> euCons = new ArrayList<Constraint>(parentConstraints);
				euCons.add(ceuEU.getConstraint());
				for (OS euOS : ceuEU.directedOSes)
					euOS.constraints = new ArrayList<Constraint>(euCons);
				buildConstraints((ArrayList<CEU>) ceuEU.directedCEUs, euCons);
			}
		}
	}

	/**
	 * TODO This function should convert "break" constraints to "alt".
	 * 
	 * @param cfs
	 * @param msgNums
	 * @return
	 */
	@SuppressWarnings("unused")
	private static void breakToAlt(ArrayList<CF> cfs, ArrayList<Integer> msgNums) {
	}

	/**
	 * TODO this function should convert "consider" to "ignore"
	 * 
	 * @param ceus
	 */
	@SuppressWarnings("unused")
	private static void considerToIgnore(ArrayList<CEU> ceus) {

	}

	/**
	 * Accumulates all message numbers for all CFs in cfs
	 * 
	 * @return List of all messages in cfs.
	 */
	private static ArrayList<Integer> getAllCFMsg(ArrayList<CF> cfs) {
		ArrayList<Integer> msgNums = new ArrayList<Integer>();
		for (CF cf : cfs)
			msgNums.addAll(cf.getAllMsgNums());
		return msgNums;
	}

	/**
	 * Builds the necessary iterations for all LOOP combined fragments.
	 * 
	 * @param cfs
	 *            List of CFs to build loops for.
	 */
	private static void buildLoops(ArrayList<CF> cfs) {
		for (CF cf : cfs) {
			if (cf.operator.equals(Operator.LOOP)) {
				Operand cfOp = cf.operands.get(0);
				cfOp.constraint.maxIteration = 3;
				cfOp.constraint.minIteration = 1;

				int maxIteration = cfOp.constraint.maxIteration;
				int minIteration = cfOp.constraint.minIteration;

				for (int i = 0; i < maxIteration; i++) {
					if (i < minIteration)
						cfOp.constraint.constraint = "true";

					for (EU opEU : cfOp.eus) {
						for (int j = 0; j < opEU.directedOSes.size(); j++) {
							OS directedOS = opEU.directedOSes.get(j);
							directedOS.name = directedOS.name + "[" + (i + 1) + "]";
							directedOS.iteration = i + 1;
							String curState = opEU.states.get(j + 1);
							opEU.states.remove(j + 1);
							curState = curState + "_" + (i + 1);
							opEU.states.add(j + 1, curState);
						}

						buildCEUIterations((ArrayList<CEU>) opEU.directedCEUs, i, minIteration);

						for (CEU cfCEU : cf.ceus) {
							if (cfCEU.lifeline.equals(opEU.lifeline)) {
								cfCEU.euIterations.add(opEU);
								break;
							}
						}
					}
					cf.iterations.add(cfOp);
					buildLoops((ArrayList<CF>) cfOp.cfs);
				}
			}
		}
	}

	/**
	 * Propagates iterations.
	 * 
	 * @param ceus
	 *            List of CEUs to propogate iterations.
	 * @param iteration
	 *            Current iteration.
	 * @param minIteration
	 *            Minimum iteration.
	 */
	private static void buildCEUIterations(ArrayList<CEU> ceus, int iteration, int minIteration) {
		for (CEU ceu : ceus) {
			ceu.iteration = iteration + 1;
			for (EU ceuEU : ceu.eus) {
				if (iteration < minIteration - 1)
					ceuEU.operand.constraint.constraint = "true";
				for (OS directedOS : ceuEU.directedOSes)
					directedOS.name = directedOS.name + "[" + (iteration + 1) + "]";
				buildCEUIterations((ArrayList<CEU>) ceuEU.directedCEUs, iteration, minIteration);
			}
		}
	}

	/**
	 * Builds array of critical EUs
	 * 
	 * @param ceus
	 *            List of CEUs to check for criticals.
	 * @param criticals
	 *            Current list of critical EUs.
	 * @return List of EUs containing criticals.
	 */
	private static ArrayList<EU> buildCriticals(ArrayList<CEU> ceus, ArrayList<EU> criticals) {
		for (CEU ceu : ceus) {
			if (ceu.cf.operator.equals(Operator.CRIT))
				criticals.add(ceu.eus.get(0));
			for (EU ceuEU : ceu.eus)
				criticals.addAll(buildCriticals((ArrayList<CEU>) ceuEU.directedCEUs, criticals));
		}
		return criticals;
	}

	/**
	 * Builds array of critical EUs
	 * 
	 * @param ceus
	 *            List of CEUs to check for assertions.
	 * @param assertions
	 *            Current List of assertion EUs
	 * @return List of EUs containing assertions.
	 */
	private static ArrayList<EU> buildAssertions(ArrayList<CEU> ceus, ArrayList<EU> assertions) {
		for (CEU ceu : ceus) {
			if (ceu.cf.operator.equals(Operator.ASSERT))
				assertions.add(ceu.eus.get(0));
			for (EU ceuEU : ceu.eus)
				assertions.addAll(buildAssertions((ArrayList<CEU>) ceuEU.directedCEUs, assertions));
		}
		return assertions;
	}

	/**
	 * Returns value between tags marked "key"
	 * 
	 * @param e
	 *            Element with tags.
	 * @param key
	 *            Key with value.
	 * @return First value corresponding to key.
	 */
	public static String elementValue(Element e, String key) {
		// for each of the Element's children
		for (int i = 0; i < e.getChildNodes().getLength(); i++)
			// check if the node's name equals the key
			if (e.getChildNodes().item(i).getNodeName().equals(key))
				return (e.getChildNodes().item(i).getFirstChild().getNodeValue());
		return null;
	}
}