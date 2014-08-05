package drivers;

import java.io.File;

import sdComponents.SD;
import translators.LTLGenerator;
import translators.XMLParser;

public class Tester {
	private static SD sequenceDiagram;
	private static long startTime, endTime;

	public static void main(String[] args) {

		parseXML(true);
		generateLTL();

	}

	private static void testParser() {
		parseXML(true);
		System.out.println(sequenceDiagram);

		System.out.println("Time to parse: " + (endTime - startTime) + "ms");
	}

	private static void parseXML(boolean verbose) {
		File inputFile;

		inputFile = new File("testFiles/intermediate.xml");
		if (verbose)
			System.out.println("**Parsing XML file**");
		startTime = System.currentTimeMillis();
		sequenceDiagram = XMLParser.parse(inputFile);
		endTime = System.currentTimeMillis();
		if (verbose)
			System.out.println("**Parsing complete**");
	}

	private static void generateLTL() {
		System.out.println(LTLGenerator.generateLTL(sequenceDiagram, true, true));
	}
}
