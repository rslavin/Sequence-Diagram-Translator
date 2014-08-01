package drivers;

import java.io.File;

import sdComponents.SD;
import translators.XMLParser;

public class ParserTester {
	public static void main(String[] args) {
		File inputFile;
		SD sequenceDiagram;

		inputFile = new File("testFiles/intermediate.xml");
		System.out.println("**Parsing XML file**");
		long startTime = System.currentTimeMillis();
		sequenceDiagram = XMLParser.parse(inputFile);
		long endTime = System.currentTimeMillis();
		System.out.println("**Parsing complete**");
		System.out.println(sequenceDiagram);
		
		System.out.println("Time to parse: " + (endTime - startTime) + "ms");

	}
}
