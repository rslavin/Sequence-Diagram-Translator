package translators;

import java.util.ArrayList;

import sdComponents.*;
import translators.ltl.Formulas;
import translators.ltl.Utils;

/**
 * 
 * @author Rocky Slavin
 * 
 *         This class produces an LTL formula from an input Sequence Diagram
 *         (SD).
 * 
 */
public class LTLGenerator {
	private static final boolean ALPHA2 = true;
	private static final boolean EPSILON = true;
	private static final boolean DEBUG = true;
	

	public static String generateLTL(SD sequenceDiagram, boolean alpha2, boolean epsilon){
		Formulas formulas = new Formulas(ALPHA2, DEBUG);
		String ltl = "";
		// for each Lifeline in sequenceDiagram, print alpha and beta
		ArrayList<String> alpha = new ArrayList<String>();
		ArrayList<String> beta = new ArrayList<String>();
		for(Lifeline lifeline : sequenceDiagram.lifelines){
			alpha.add(formulas.alpha(lifeline));
			beta.add(formulas.beta(lifeline));
		}
		ltl += Utils.conjunct(alpha);
		ltl += "\n&\n" + Utils.conjunct(beta);
		
		// for each CF in sequenceDiagram, print Phi
		ArrayList<String> phi = new ArrayList<String>();
		for(CF cf : sequenceDiagram.cfs)
			phi.add(formulas.phiBar(cf));
		ltl += "\n&\n" + Utils.conjunct(phi);
		
		// if epsilon, print epsilon
		if(EPSILON)
			;
		return ltl;
	}
}
