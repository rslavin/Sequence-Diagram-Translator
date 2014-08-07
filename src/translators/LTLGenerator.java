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
	private static final boolean DEBUG = false;
	

	public static String generateLTL(SD sequenceDiagram, boolean alpha2, boolean epsilon){
		String ltl = "";
		// for each Lifeline in sequenceDiagram, print alpha and beta
		ArrayList<String> alpha = new ArrayList<String>();
		ArrayList<String> beta = new ArrayList<String>();
		for(Lifeline lifeline : sequenceDiagram.lifelines){
			alpha.add(Formulas.alpha(lifeline, ALPHA2, DEBUG));
			beta.add(Formulas.beta(lifeline, DEBUG));
		}
		ltl += Utils.conjunct(alpha);
		ltl += "\n&\n" + Utils.conjunct(beta);
		
		// for each CF in sequenceDiagram, print Phi
		ArrayList<String> phi = new ArrayList<String>();
		for(CF cf : sequenceDiagram.cfs)
			phi.add(Formulas.phi(cf, DEBUG));
		ltl += "\n&\n" + Utils.conjunct(phi);
		// if epsilon, print epsilon
		return ltl;
	}
}
