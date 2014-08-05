package translators.ltl;

import java.util.ArrayList;

import enums.*;
import sdComponents.*;

public class Formulas {

	/**
	 * Generates alpha function. Second half of alpha function can be dropped if
	 * printAlpha2 is set to false.
	 * 
	 * @param lifeline
	 *            Single lifeline for which to generate alpha function.
	 * @param printAlpha2
	 *            If false, second half of alpha function is left off.
	 * @param debug
	 *            Verbose mode.
	 * @return String representing alpha function for lifeline.
	 */
	public static String alpha(Lifeline lifeline, boolean printAlpha2, boolean debug) {
		String alpha = "";
		if (debug)
			alpha += Utils.debugPrint("Beginning Alpha for lifeline (" + lifeline.name + ") (alpha())");

		// First part of alpha
		ArrayList<String> alpha1 = new ArrayList<String>();
		for (int i = 0; i < lifeline.orderedElements.size() - 1; i++) {
			if (lifeline.orderedElements.get(i) instanceof OS && lifeline.orderedElements.get(i + 1) instanceof OS) {
				OS curOS = (OS) lifeline.orderedElements.get(i);
				OS nextOS = (OS) lifeline.orderedElements.get(i + 1);
				alpha1.add(Utils.strongUntil("!" + nextOS.ltlString(), curOS.ltlString()));
			}
		}

		alpha += Utils.conjunct(alpha1);

		if (printAlpha2) {
			// Second part of alpha
			ArrayList<String> alpha2 = new ArrayList<String>();
			for (OS os : lifeline.oses)
				alpha2.add("(!" + os.ltlString() + " U (" + os.ltlString() + " & X G !" + os.ltlString() + "))");

			alpha += " & " + Utils.conjunct(alpha2);

		}
		if (debug)
			alpha += Utils.debugPrint("Completed Alpha for lifeline (" + lifeline.name + ") (alpha())");
		return alpha;
	}

	/**
	 * Generates beta function for lifeline.
	 * 
	 * @param lifeline
	 *            Lifeline for which to generate beta function
	 * @param debug
	 *            Verbose mode.
	 * @return
	 */
	public static String beta(Lifeline lifeline, boolean debug) {
		String beta = "";
		if (debug)
			beta += Utils.debugPrint("Beginning Beta for lifeline (" + lifeline.name + ") (beta())");

		ArrayList<String> beta1 = new ArrayList<String>();
		for (OS os : lifeline.oses)
			if (os.osType.equals(OSType.SEND))
				beta1.add(Utils.strongUntil("!" + os.ltlStringOp(), os.ltlString()));
		beta += " & " + Utils.conjunct(beta1);

		if (debug)
			beta += Utils.debugPrint("Completed Beta for lifeline (" + lifeline.name + ") (beta())");
		return beta;
	}
	
	public static String phi(CF cf, boolean debug){
		String phi = "";
		if (debug)
			phi += Utils.debugPrint("Beginning Phi for cf (" + cf.num + ") (phi())");
		switch(cf.operator){
		case ALT:
			return psi(cf, debug);
		case OPT:
			
		}
		if (debug)
			phi += Utils.debugPrint("Completed Phi for lifecfline (" + cf.num + ") (phi())");
		return phi;
	}
	
	public static String psi(CF cf, boolean debug){
		
	}
	
	public static String psiAlt(CF cf, boolean debug){
		
		return null;
	}
}
