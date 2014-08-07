package translators.ltl;

import java.util.ArrayList;

import enums.*;
import sdComponents.*;
import translators.LTLGenerator;

public class Formulas {

	/**
	 * Generates alpha function. Second half of alpha function can be dropped if
	 * printAlpha2 is set to false. This formula only applies to OSes outside of
	 * combined fragments.
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
	 * Generates beta function for lifeline. This formula only applies to OSes
	 * outside of combined fragments.
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
		beta += Utils.conjunct(beta1);

		if (debug)
			beta += Utils.debugPrint("Completed Beta for lifeline (" + lifeline.name + ") (beta())");
		return beta;
	}

	public static String phi(CF cf, boolean debug) {
		String phi = "";
		if (debug)
			phi += Utils.debugPrint("Beginning Phi for cf (" + cf.num + ") (phi())");
		switch (cf.operator) {
		case LOOP:
		case ALT:
			return "LOOP OR ALT";
		default:
			ArrayList<String> phiList = new ArrayList<String>();
			for(Operand operand : cf.operands)
				phiList.add(phiOperand(operand, debug));
			return Utils.conjunct(phiList);

		}
	}
	
	public static String phiOperand(Operand op, boolean debug){
		// these strings correspond to the four portions of this formula
		// each portion has a corresponding method
				
		// phi 1
		// for each CEU, find its preOS
		ArrayList<Ordered> preOSes = new ArrayList<Ordered>();
		for(CEU ceu : op.cf.ceus)
			preOSes.addAll(ceu.preOrdereds);
		
		ArrayList<String> phi1Conjuncts = new ArrayList<String>();
		for(Ordered os : preOSes)
			if(os instanceof OS)
				phi1Conjuncts.add("(G !"+ ((OS) os).ltlString() + ")");
		String phi1 = Utils.conjunct(phi1Conjuncts) + " & " + op.constraint.constraint;
		// end phi 1
		
		// phi 2
		String phi2 = theta(op, debug) + " & " + op.constraint.constraint;
		// nested CFs
		ArrayList<String> nestedConstriants = new ArrayList<String>();
		if(op.nestedCFs != null && op.nestedCFs.size() > 0){
			ArrayList<String> nestedCFConjuncts = new ArrayList<String>();
			for(CF nestedCF : op.nestedCFs){
				nestedCFConjuncts.add(phi(nestedCF, debug));
				// (this is for phi 4)
				for(Operand nestedOp : nestedCF.operands)
					nestedConstriants.add(nestedOp.constraint.constraint);
			}
			phi2 += "& " + Utils.conjunct(nestedCFConjuncts);
		}
		// end phi 2
		
		// phi 3
		String phi3 = Utils.conjunct(phi1Conjuncts) + " & (!" + op.constraint.constraint + ")";
		// end phi 3
		
		// phi4
		String phi4 = "G (!" + op.constraint.constraint + ")";
		if(nestedConstriants != null && nestedConstriants.size() > 0){
			ArrayList<String> constraintConjuncts = new ArrayList<String>();
			for(String constraint: nestedConstriants)
				constraintConjuncts.add("G (!" + constraint + ")");
			phi4 += " & " + Utils.conjunct(constraintConjuncts);
		}
		return Utils.globally("((" + phi1 + ") -> (" + phi2 + ")) & ((" + phi3 + ") -> (" + phi4 + "))" );
	}
	
	public static String theta(Operand op, boolean debug){
		return "THETA GOES HERE";
	}
	
	

	
}
