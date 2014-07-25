package translator;

import sdComponents.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
				sequenceDiagram = parseSequenceDiagram(SDList.item(0));
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
		}

		// populate combined fragment list
		NodeList cfNodes = xmlElement.getElementsByTagName("combinedFragment");
		if (cfNodes != null && cfNodes.getLength() > 0) {
			cfs = new ArrayList<CF>();
			for (int i = 0; i < cfNodes.getLength(); i++)
				cfs.add(parseCombinedFragment(cfNodes.item(i)));
		}

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
		allMessages.removeAll(AllCFMsg(sequenceDiagram.osesInCFs()));

		// TODO refactor break2Alt()
		// break2Alt(sequenceDiagram.osesInCFs(), allMessages);
		sequenceDiagram.lifelines = composeLifeline(sequenceDiagram.lifelines);
		sequenceDiagram.lifelines = composeEU(sequenceDiagram.lifelines, sequenceDiagram.cfs);
		sequenceDiagram.lifelines = projectCF2LifelineList(sequenceDiagram.lifelines, sequenceDiagram.cfs);

		return sequenceDiagram;
	}

	/**
	 * Parses a lifeline from an xml element. Iterates through all messages
	 * belonging to the lifeline and extracts OSes.
	 * 
	 * @param xmlElement Element representing the Lifeline in xml
	 * @return Lifeline object
	 */
	private static Lifeline parseLifeline(Element xmlElement) {
		Lifeline lifeline = new Lifeline(xmlElement.getAttribute("roleName"), xmlElement.getAttribute("type"));

		// parse OSes from messageEvent xml tags belonging to the Lifeline
		NodeList xmlMessageEvents = xmlElement.getElementsByTagName("messageEvent");
		if (xmlMessageEvents != null && xmlMessageEvents.getLength() > 0) {
			for (int i = 0; i < xmlMessageEvents.getLength(); i++) {
				lifeline.oses.add(parseMessageEvent((Element) xmlMessageEvents.item(i), lifeline.name));
			}
		}
		return lifeline;
	}
}
