package drivers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import sdComponents.SD;
import translators.*;

public class Tester {
	private static long startTime, endTime;

	private static List<SD> sequenceDiagrams;

	private static SD policy;
	private static SD regulation;

	private static final boolean ALPHA2 = true;
	private static final boolean EPSILON = false;
	private static final boolean DEBUG = false;

	public static void main(String[] args) {
//		 parseXML("jython/regulation.xml", false);
//		regulation = parseXML("jython/policy.xml", false);
//		System.out.println(regulation);
		System.out.println(conformance("jython/policy.xml", "jython/regulation.xml"));
	}

	public static String conformance(String policyFile, String regulationFile) {
		sequenceDiagrams = new ArrayList<SD>();
		policy = parseXML(policyFile, false);
		regulation = parseXML(regulationFile, false);
		sequenceDiagrams.add(policy);
		sequenceDiagrams.add(regulation);

		String policyLTL = generateLTL(policy);
		String regulationLTL = generateLTL(regulation);

		return "MODULE main\n" + generateVars() + "\n\n" + ltlSpec("(" + policyLTL + ") \n\n->\n\n(" + regulationLTL + ")");
	}

	public static void conformanceToFile(String policyFile, String regulationFile) {
		try {
			PrintWriter writer = new PrintWriter("conformance.smv", "UTF-8");
			writer.println(conformance(policyFile, regulationFile));
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
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
		return ModelGenerator.generateVars((ArrayList<SD>) sequenceDiagrams) + "\n\n"
				+ ModelGenerator.initializeVars((ArrayList<SD>) sequenceDiagrams, false);
	}

	private static String ltlSpec(String string) {
		return "LTLSPEC(\n" + string + "\n)";
	}

	public static String generateSMV(SD sd) {
		String ltl = generateLTL(sd);
		return "MODULE main\n\n" + generateVars() + "\n" + ltlSpec(ltl);
	}

}
