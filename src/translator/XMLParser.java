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
	 * begin creation of sequence diagram components. sequence diagram
	 * components.
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
	 * @param element Root sequence diagram element in DOM tree.
	 * @return Returns the root sequence diagram object.
	 */
	private static SD parseSequenceDiagram(Element element) {
		List<Lifeline> lifelines;
		List<CF> cfs;

		// populate lifeline list
		NodeList lifelineNodes = element.getElementsByTagName("lifeline");
		if (lifelineNodes != null && lifelineNodes.getLength() > 0) {
			lifelines = new ArrayList<Lifeline>();
			for (int i = 0; i < lifelineNodes.getLength(); i++) {
				Element lfe = (Element) lifelineNodes.item(i);
				if (lfe.getElementsByTagName("type") != null)
					lifelines.add(parseLifeline(lfe));
			}
		}

		// populate combined fragment list
		NodeList cfNodes = element.getElementsByTagName("combinedFragment");
		if (cfNodes != null && cfNodes.getLength() > 0) {
			cfs = new ArrayList<CF>();
			for (int i = 0; i < cfNodes.getLength(); i++) {
				cfs.add(parseCombinedFragment(cfNodes.item(i)));
			}
		}

		// create SD object
		SD sequenceDiagram = new SD(element.getElementsByTagName("name").toString(), lifelines, cfs);

		// generate list of sending OSes (one OS per message)
		List<OS> allMessages = new ArrayList<OS>();
		for (int i = 0; i < lifelines.size(); i++) {
			Lifeline curLifeline = lifelines.get(i);
			for (int j = 0; j < curLifeline.oses.size(); j++) {
				OS curOS = curLifeline.oses.get(j);
				if (curOS.osType == OSType.SEND) {
					allMessages.add(curOS);
				}
			}
		}

		// remove from allMessages any messages that appear in AllCFMsg
		allMessages.removeAll(AllCFMsg(sequenceDiagram.osesInCFs));

		sequenceDiagram.osesInCFs = break2Alt(sequenceDiagram.osesInCFs, allMessages);
		sequenceDiagram.lifelines = composeLifeline(sequenceDiagram.lifelines);
		sequenceDiagram.lifelines = composeEU(sequenceDiagram.lifelines, sequenceDiagram.cfs);
		sequenceDiagram.lifelines = projectCF2LifelineList(sequenceDiagram.lifelines, sequenceDiagram.cfs);

		return sequenceDiagram;
	}

}
