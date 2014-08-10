package drivers;

import java.io.File;
import java.util.ArrayList;

import sdComponents.SD;
import translators.*;

public class Tester {
	private static SD sequenceDiagram;
	private static long startTime, endTime;
	private static final boolean ALPHA2 = true;
	private static final boolean EPSILON = true;
	private static final boolean DEBUG = true;

	public static void main(String[] args) {

		sequenceDiagram = parseXML(false);
		//testParser();
		System.out.println(generateSMV());
	}

	private static void testParser() {
		sequenceDiagram = parseXML(true);
		System.out.println(sequenceDiagram);

		System.out.println("Time to parse: " + (endTime - startTime) + "ms");
	}

	private static SD parseXML(boolean verbose) {
		File inputFile;
		SD sequenceDiagram;

		inputFile = new File("testFiles/intermediate.xml");
		if (verbose)
			System.out.println("**Parsing XML file**");
		startTime = System.currentTimeMillis();
		sequenceDiagram = XMLParser.parse(inputFile);
		endTime = System.currentTimeMillis();
		if (verbose)
			System.out.println("**Parsing complete**");
		return sequenceDiagram;
	}

	private static String generateLTL() {
		return LTLGenerator.generateLTL(sequenceDiagram, ALPHA2, EPSILON, DEBUG);
	}
	
	private static String generateVars(){
		ArrayList<SD> sds = new ArrayList<SD>();
		sds.add(sequenceDiagram);
		return ModelGenerator.generateVars(sds) + "\n" + ModelGenerator.initializeVars(sds, false);
	}
	
	private static String ltlSpec(String string){
		return "LTLSPEC(\n" + string + "\n)";
	}
	
	private static String generateSMV(){
		return "MODULE main\n\n" + generateVars() + "\n" + ltlSpec(generateLTL());
	}
}
