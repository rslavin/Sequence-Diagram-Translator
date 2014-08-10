package drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sdComponents.SD;
import translators.*;

public class Tester {
	private static long startTime, endTime;
	private static String sdLTL;

	private static List<SD> sequenceDiagrams;
	private static List<String> ltlSDs;

	private static SD policy;
	private static SD regulation;

	private static final boolean ALPHA2 = true;
	private static final boolean EPSILON = true;
	private static final boolean DEBUG = false;

	public static void main(String[] args) {
		sequenceDiagrams = new ArrayList<SD>();
		ltlSDs = new ArrayList<String>();
//		sequenceDiagrams.add(parseXML("testFiles/intermediate.xml", false));
//		generateSMV(sequenceDiagrams.get(0));
		System.out.println(conformance("testFiles/tester.xml", "testFiles/hipaa.xml", false));
	}

	public static String conformance(String policyFile, String regulationFile, boolean verbose){
		policy = parseXML(policyFile, verbose);
		regulation = parseXML(regulationFile, verbose);
		sequenceDiagrams.add(policy);
		sequenceDiagrams.add(regulation);
		
		String policyLTL = generateLTL(policy);
		String regulationLTL = generateLTL(regulation);
		// add to ltlSDs for exe generation
		ltlSDs.add(policyLTL);
		ltlSDs.add(regulationLTL);
		
		return "MODULE main\n" + generateVars() + "\n\n" + ltlSpec("(" + policyLTL + ") \n\n-> \n\n(" + regulationLTL + ")");
	}

	private static void printTime() {
		System.out.println("Time to parse: " + (endTime - startTime) + "ms");
	}

	private static SD parseXML(String fileLoc, boolean verbose) {
		File inputFile;
		SD sequenceDiagram;

		inputFile = new File(fileLoc);
		if (verbose)
			System.out.println("**Parsing XML file**");
		startTime = System.currentTimeMillis();
		sequenceDiagram = XMLParser.parse(inputFile);
		endTime = System.currentTimeMillis();
		if (verbose)
			System.out.println("**Parsing complete**");
		return sequenceDiagram;
	}

	private static String generateLTL(SD sd) {
		return LTLGenerator.generateLTL(sd, ALPHA2, EPSILON, DEBUG);
	}

	private static String generateVars() {
		return ModelGenerator.generateVars((ArrayList<SD>) sequenceDiagrams) + "\n" + ModelGenerator.generateExeVars((ArrayList<String>)ltlSDs)
				+ "\n" +ModelGenerator.initializeVars((ArrayList<SD>) sequenceDiagrams, false) + "\n"
				+ ModelGenerator.initializeExeVars((ArrayList<String>) ltlSDs, false);
	}

	private static String ltlSpec(String string) {
		return "LTLSPEC(\n" + string + "\n)";
	}

	public static String generateSMV(SD sd) {
		String ltl = generateLTL(sd);
		ltlSDs.add(ltl);
		return "MODULE main\n\n" + generateVars() + "\n" + ltlSpec(ltl);
	}

}
