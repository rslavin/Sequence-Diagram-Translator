package translators.ltl;

import java.util.ArrayList;

import enums.*;
import sdComponents.*;

public class Formulas {
	private boolean printAlpha2;
	private boolean debug;

	public Formulas(boolean printAlpha2, boolean debug) {
		this.printAlpha2 = printAlpha2;
		this.debug = debug;
	}

	/**
	 * Generates alpha formula. Second half of alpha formula can be dropped if
	 * printAlpha2 is set to false. This formula only applies to OSes outside of
	 * combined fragments.
	 * 
	 * @param lifeline
	 *            Single lifeline for which to generate alpha formula.
	 * @return String representing alpha formula for lifeline.
	 */
	public String alpha(Lifeline lifeline) {
		String alpha = "";
		if (debug)
			alpha += Utils.debugPrint("Beginning Alpha for lifeline (" + lifeline.name + ") (alpha())");
		// First part of alpha
		ArrayList<String> alpha1 = new ArrayList<String>();
		for (int i = 0; i < lifeline.directedOSes.size() - 1; i++) {
			// if (lifeline.orderedElements.get(i) instanceof OS &&
			// lifeline.orderedElements.get(i + 1) instanceof OS) {
			OS curOS = (OS) lifeline.directedOSes.get(i);
			OS nextOS = (OS) lifeline.directedOSes.get(i + 1);
			alpha1.add(Utils.strongUntil("!" + nextOS.ltlString(), curOS.ltlString()));
			// }
		}

		alpha += Utils.conjunct(alpha1);

		if (printAlpha2) {
			// Second part of alpha
			ArrayList<String> alpha2 = new ArrayList<String>();
			for (OS os : lifeline.directedOSes)
				alpha2.add("(!" + os.ltlString() + " U (" + os.ltlString() + " & X G !" + os.ltlString() + "))");
			if (alpha1.size() > 0)
				alpha += " & ";
			alpha += Utils.conjunct(alpha2);

		}
		if (debug)
			alpha += Utils.debugPrint("Completed Alpha for lifeline (" + lifeline.name + ") (alpha())");
		return alpha;
	}

	/**
	 * Generates beta formula for lifeline. This formula only applies to OSes
	 * outside of combined fragments.
	 * 
	 * @param lifeline
	 *            Lifeline for which to generate beta formula
	 * @return String representing beta formula for lifeline.
	 */
	public String beta(Lifeline lifeline) {
		String beta = "";
		if (debug)
			beta += Utils.debugPrint("Beginning Beta for lifeline (" + lifeline.name + ") (beta())");

		ArrayList<String> betaConjuncts = new ArrayList<String>();
		for (OS os : lifeline.directedOSes)
			if (os.osType.equals(OSType.SEND))
				betaConjuncts.add(Utils.strongUntil("!" + os.ltlStringOp(), os.ltlString()));
		beta += Utils.conjunct(betaConjuncts);

		if (debug)
			beta += Utils.debugPrint("Completed Beta for lifeline (" + lifeline.name + ") (beta())");
		return beta;
	}

	/**
	 * Generate epsilon formula for a sequence diagram.
	 * 
	 * @param sd
	 *            Sequence diagram to generate epsilon formula for.
	 * @return
	 */
	public String epsilon(SD sd) {
		String epsilon = "";
		if (debug)
			epsilon += Utils.debugPrint("Beginning Epsilon (beta())");
		// loop through all OSes
		ArrayList<String> osDisjuncts = new ArrayList<String>();
		ArrayList<String> negOSConjuncts = new ArrayList<String>();
		ArrayList<String> condConjuncts = new ArrayList<String>();
		for (Lifeline lifeline : sd.lifelines) {
			for (OS os : lifeline.oses) {
				osDisjuncts.add(os.ltlString());
				negOSConjuncts.add("(!" + os.ltlString() + ")");
				if (!os.constraints.isEmpty())
					for (Constraint constraint : os.constraints)
						condConjuncts.add("(" + os.ltlString() + " -> " + constraint.operand.getConstraint() + ")");
			}
		}

		String tempEpsilon = "(" + Utils.xor(osDisjuncts) + ") | (" + Utils.conjunct(negOSConjuncts) + ")";
		if (!condConjuncts.isEmpty())
			tempEpsilon += " & (" + Utils.conjunct(condConjuncts) + ")";

		epsilon += Utils.globally(tempEpsilon);

		if (debug)
			epsilon += Utils.debugPrint("Completed Epsilon (beta())");
		return epsilon;
	}

	/**
	 * Generates phi bar formula for a combined fragment. For cases other than
	 * LOOP or ALT, phiBarOperand is called and conjuncted with eta, mu, and
	 * gamma.
	 * 
	 * @param cf
	 *            Combined fragment to generate phi bar formula for.
	 * @return String representing phi bar formula for the combined fragment.
	 */
	public String phiBar(CF cf) {
		String phi = "";
		if (debug)
			phi += Utils.debugPrint("Beginning Phi for cf (" + cf.num + ") (phi())");
		switch (cf.operator) {
		case LOOP:
		case ALT:
			phi += phiBarAlt(cf);
			break;
		// case PAR:
		// phi += phiBarPar(cf);
		// break;
		default:
			ArrayList<String> phiList = new ArrayList<String>();
			for (Operand operand : cf.operands)
				phiList.add(phiBarOperand(operand));
			phi += Utils.conjunct(phiList);
			String gamma = gammaBar(cf);
			if (gamma.length() > 1)
				phi += " & (" + gamma + ")";
			String eta = etaBar(cf);
			if (eta.length() > 1)
				phi += " & (" + eta + ")";
			String mu = muBar(cf);
			if (mu.length() > 1)
				phi += " & (" + mu + ")";
		}
		if (debug)
			phi += Utils.debugPrint("Completed Phi for cf (" + cf.num + ") (phi())");
		return phi;
	}

	// private String phiBarPar(CF cf){
	// String phi = theta(cf);
	// ArrayList<String> lifelineConjuncts = new ArrayList<String>();
	// for(Lifeline lifeline : cf.lifelines)
	// lifelineConjuncts.add(gamma(cf));
	// return phi + Utils.conjunct(lifelineConjuncts);
	// }
	//
	// private String theta(CF cf){
	// String theta = "";
	// ArrayList<String> lifelineConjuncts = new ArrayList<String>();
	// for(Lifeline lifeline : cf.lifelines){
	// for(OS os : cf.lifelineCEU(lifeline).getOSes())
	//
	// }
	// return theta;
	// }

	/**
	 * Generates phi bar formula for operands. Called by phi().
	 * 
	 * @param op
	 *            Operand to generate phi bar formula for. Complete phi bar
	 *            formula is a conjunction of ALL operands.
	 * @return String representing phi bar formula for operand.
	 */
	private String phiBarOperand(Operand op) {
		// these strings correspond to the four portions of this formula

		// phi 1
		// for each CEU, find its preOS
		ArrayList<Ordered> preOSes = new ArrayList<Ordered>();
		for (CEU ceu : op.cf.ceus)
			preOSes.addAll(ceu.preOrdereds);

		ArrayList<String> phi1Conjuncts = new ArrayList<String>();
		for (Ordered os : preOSes)
			if (os instanceof OS)
				phi1Conjuncts.add("(G !" + ((OS) os).ltlString() + ")");
		String phi1 = Utils.conjunct(phi1Conjuncts) + " & " + op.getConstraint();
		// end phi 1

		// phi 2
		String phi2 = thetaBar(op) + " & " + op.getConstraint();
		// nested CFs
		ArrayList<String> nestedConstriants = new ArrayList<String>();
		if (op.nestedCFs != null && op.nestedCFs.size() > 0) {
			ArrayList<String> nestedCFConjuncts = new ArrayList<String>();
			for (CF nestedCF : op.nestedCFs) {
				nestedCFConjuncts.add(phiBar(nestedCF));
				// (this is for phi 4)
				for (Operand nestedOp : nestedCF.operands)
					nestedConstriants.add(nestedOp.getConstraint());
			}
			phi2 += "& " + Utils.conjunct(nestedCFConjuncts);
		}
		// end phi 2

		// phi 3
		String phi3 = Utils.conjunct(phi1Conjuncts) + " & (!" + op.getConstraint() + ")";
		// end phi 3

		// phi4
		String phi4 = "G (!" + op.getConstraint() + ")";
		if (nestedConstriants != null && nestedConstriants.size() > 0) {
			ArrayList<String> constraintConjuncts = new ArrayList<String>();
			for (String constraint : nestedConstriants)
				constraintConjuncts.add("G (!" + constraint + ")");
			phi4 += " & " + Utils.conjunct(constraintConjuncts);
		}
		// end phi 4

		return Utils.globally("((" + phi1 + ") -> (" + phi2 + ")) & ((" + phi3 + ") -> (" + phi4 + "))");

	}

	/**
	 * Generate theta bar formula for operand. This formula calls alphaBar() for
	 * all EUs in the operand (and thus all lifelines in the operand) and calls
	 * betaBar() for all messages in the operand.
	 * 
	 * @param op
	 *            Operand to generate theta formula for.
	 * @return String representing theta formula for operand.
	 */
	private String thetaBar(Operand op) {
		String theta = "";
		if (debug)
			theta += Utils.debugPrint("Beginning Theta (theta())");
		// for all EUs in all Lifelines in the operand
		ArrayList<String> alphaConjuncts = new ArrayList<String>();
		ArrayList<String> betaConjuncts = new ArrayList<String>();
		for (EU eu : op.eus) {
			// TODO directedCEUs may need to be incorporated
			if (eu.directedOSes != null && eu.directedOSes.size() > 0)
				alphaConjuncts.add("(" + alphaBar(eu) + ")");
			for (OS os : eu.directedOSes) {
				if (os.osType.equals(OSType.SEND))
					betaConjuncts.add("(" + betaBar(os) + ")");
			}
		}
		theta += "(" + Utils.conjunct(alphaConjuncts) + ") & ";
		theta += Utils.conjunct(betaConjuncts);

		if (debug)
			theta += Utils.debugPrint("Completed Theta Bar (thetaBar())");
		return theta;
	}

	private String gammaBar(CF cf) {
		String gamma = "";
		if (debug)
			gamma += Utils.debugPrint("Beginning Gamma Bar for CF (" + cf.num + ") (gammaBar())");

		// for each ceu in cf
		ArrayList<String> ceuConjuncts = new ArrayList<String>();
		for (CEU ceu : cf.ceus) {
			// for each pre os
			ArrayList<String> preOSConjuncts = new ArrayList<String>();
			for (Ordered preOS : ceu.preOrdereds) {
				if (preOS instanceof OS) {
					// for each tOS
					ArrayList<String> tosConjuncts1 = new ArrayList<String>();
					for (OS tOS : ceu.getOSes())
						tosConjuncts1.add("!" + tOS.ltlString());
					if (!tosConjuncts1.isEmpty())
						preOSConjuncts.add("(F " + ((OS) preOS).ltlString() + " -> ("
								+ Utils.strongUntil("(" + Utils.conjunct(tosConjuncts1) + ")", ((OS) preOS).ltlString()) + "))");
				}
			}

			// for each os
			ArrayList<String> tOSConjuncts2 = new ArrayList<String>();
			for (OS tOS : ceu.getOSes()) {
				// for each post OS
				ArrayList<String> postOSConjuncts = new ArrayList<String>();
				for (Ordered postOS : ceu.postOrdereds)
					if (postOS instanceof OS)
						postOSConjuncts.add("!" + ((OS) postOS).ltlString());
				if (!postOSConjuncts.isEmpty())
					tOSConjuncts2.add("(F " + tOS.ltlString() + " -> ("
							+ Utils.strongUntil("(" + Utils.conjunct(postOSConjuncts) + ")", tOS.ltlString()) + "))");
			}

			if (!preOSConjuncts.isEmpty() && !tOSConjuncts2.isEmpty())
				ceuConjuncts.add("\n((" + Utils.conjunct(preOSConjuncts) + ") & (" + Utils.conjunct(tOSConjuncts2) + "))\n");
			else if (!preOSConjuncts.isEmpty() && tOSConjuncts2.isEmpty())
				ceuConjuncts.add("\n(" + Utils.conjunct(preOSConjuncts) + ")\n");
			else if (preOSConjuncts.isEmpty() && !tOSConjuncts2.isEmpty())
				ceuConjuncts.add("\n(" + Utils.conjunct(tOSConjuncts2) + ")\n");
		}
		gamma += Utils.conjunct(ceuConjuncts);
		if (debug)
			gamma += Utils.debugPrint("Completed Gamma Bar for CF (" + cf.num + ") (gammaBar())");
		return gamma;
	}

	private String etaBar(CF cf) {
		String eta = "";
		if (debug)
			eta += Utils.debugPrint("Beginning eta bar for CF (" + cf.num + ") (etaBar())");
		// for each lifeline in the cf
		ArrayList<String> lifelineConjuncts = new ArrayList<String>();
		for (Lifeline lifeline : cf.lifelines) {
			ArrayList<String> preOSConjuncts = new ArrayList<String>();
			// for each pre os for the CEU on the lifeline (there are pre and
			// post for each ceu)
			// if pre and post ordereds exist
			if (cf.lifelineCEU(lifeline) != null && cf.lifelineCEU(lifeline).preOrdereds != null
					&& cf.lifelineCEU(lifeline).postOrdereds != null && cf.lifelineCEU(lifeline).preOrdereds.size() > 0
					&& cf.lifelineCEU(lifeline).postOrdereds.size() > 0) {
				CEU ceu = cf.lifelineCEU(lifeline);
				for (Ordered preOS : ceu.preOrdereds) {
					if (preOS instanceof OS) {
						ArrayList<String> postOSConjuncts = new ArrayList<String>();
						// for each post OS
						for (Ordered postOS : ceu.postOrdereds) {
							if (postOS instanceof OS) {
								postOSConjuncts.add("!" + ((OS) postOS).ltlString());
							}
						}
						preOSConjuncts.add("F " + ((OS) preOS).ltlString() + " -> ("
								+ Utils.strongUntil("(" + Utils.conjunct(postOSConjuncts) + ")", ((OS) preOS).ltlString()) + ")");
					}
				}
				lifelineConjuncts.add("(" + Utils.conjunct(preOSConjuncts) + ")");
			}
		}
		eta += Utils.conjunct(lifelineConjuncts);
		if (debug)
			eta += Utils.debugPrint("Completed eta bar for CF (" + cf.num + ") (etaBar())");
		return eta;
	}

	/**
	 * Generates mu bar formula for a combined fragment.
	 * 
	 * @param cf
	 *            Combined fragment to generate mu bar formula for.
	 * @return
	 */
	private String muBar(CF cf) {
		String mu = "";
		if (debug)
			mu += Utils.debugPrint("Beginning mu bar for CF (" + cf.num + ") (muBar())");
		ArrayList<String> osConjuncts = new ArrayList<String>();
		// for each operand and each set of OSes in that operand, generate the
		// formula
		for (Operand operand : cf.operands)
			for (OS os : operand.getOSes())
				if (!os.equals(operand.getFirstOS()) && !os.osType.equals(OSType.RECEIVE))
					osConjuncts.add(Utils.strongUnless("!" + os.ltlString(), operand.getFirstOS().ltlString()));

		mu += Utils.conjunct(osConjuncts);
		if (debug)
			mu += Utils.debugPrint("Completed mu bar for CF (" + cf.num + ") (muBar())");
		return mu;
	}

	/**
	 * Generates alpha bar formula for an EU. Second half of alpha bar formula
	 * can be dropped if printAlpha2 is set to false. This formula only applies
	 * to OSes inside of combined fragments.
	 * 
	 * @param eu
	 *            EU to generate alpha bar formula for.
	 * @return String representing alpha bar formula for EU.
	 */
	private String alphaBar(EU eu) {
		String alpha = "";
		if (debug)
			alpha += Utils.debugPrint("Beginning Alpha bar (alphaBar())");

		// First part of alpha
		ArrayList<String> alpha1 = new ArrayList<String>();

		for (int i = 0; i < eu.ordereds.size() - 1; i++) {
			if (eu.ordereds.get(i) instanceof OS && eu.ordereds.get(i + 1) instanceof OS) {
				OS curOS = (OS) eu.ordereds.get(i);
				OS nextOS = (OS) eu.ordereds.get(i + 1);
				alpha1.add("((" + Utils.strongUntil("!" + nextOS.ltlString(), curOS.ltlString()) + ") | (O " + curOS.ltlString()
						+ "))");
			}
		}

		alpha += Utils.conjunct(alpha1);

		if (printAlpha2) {
			// Second part of alpha
			ArrayList<String> alpha2 = new ArrayList<String>();
			for (OS os : eu.directedOSes)
				alpha2.add("("
						+ Utils.strongUntil("!" + os.ltlString(), "(" + os.ltlString() + " & " + " X G !" + os.ltlString() + ")")
						+ " |  (!" + os.ltlString() + " & O " + os.ltlString() + "))");
			if (alpha1.size() > 0)
				alpha += " & ";
			alpha += Utils.conjunct(alpha2);

		}
		if (debug)
			alpha += Utils.debugPrint("Completed Alpha bar (alphaBar())");
		return alpha;
	}

	/**
	 * Generate beta bar formula for a message.
	 * 
	 * @param os
	 *            OS corresponding to message to generate beta bar for. By
	 *            convention, this message should only be called for OSes of
	 *            type OSType.SEND.
	 * @return String representing beta bar formula for a message.
	 */
	private String betaBar(OS os) {
		String beta = "";
		if (debug)
			beta += Utils.debugPrint("Beginning Beta Bar for message (" + os.name + ") (betaBar())");

		beta += Utils.strongUntil("!" + os.ltlStringOp(), os.ltlString()) + " | " + "(O " + os.ltlString() + ")";

		if (debug)
			beta += Utils.debugPrint("Completed Beta Bar for message (" + os.name + ") (betaBar())");
		return beta;
	}

	/**
	 * Takes exe as a parameter due to nested calls.
	 * 
	 * @param cf
	 * @param exe
	 * @return
	 */
	private String phiBarAlt(CF cf) {
		String phi = "";
		if (debug)
			phi += Utils.debugPrint("Beginning Phi Bar Alt for CF (" + cf.num + ") (phiBarAlt())");

		// for each operand in the CF
		ArrayList<String> operandConjuncts = new ArrayList<String>();
		for (Operand operand : cf.operands) {
			// add to operandConjuncts
			operandConjuncts.add("((" + Utils.globally(phiBarAlt1(operand, cf) + ") & (" + phiBarAlt2(operand, cf)) + "))");
		}
		String gamma = gammaBar(cf);
		String mu = muBar(cf);
		String eta = etaBar(cf);
		String nu = nu(cf);

		phi += Utils.conjunct(operandConjuncts);
		if (gamma.length() > 0)
			phi += " & " + gamma;
		if (mu.length() > 0)
			phi += " & " + mu;
		if (eta.length() > 0)
			phi += " & " + eta;
		if (nu.length() > 0)
			phi += " & " + nu;

		if (debug)
			phi += Utils.debugPrint("Completed Phi Bar Alt for CF (" + cf.num + ") (phiBarAlt())");
		return phi;

	}

	// TODO store exe values directly in Operands when parsing. Ask Hui.
	private String phiBarAlt1(Operand operand, CF cf) {
		// for each operand
		ArrayList<String> innerOpDisjuncts = new ArrayList<String>();
		for (Operand innerOp : cf.operands) {
			// for each pre os
			ArrayList<String> preOSConjuncts = new ArrayList<String>();
			for (Ordered preOS : innerOp.getPreOrdereds()) {
				if (preOS instanceof OS) {
					preOSConjuncts.add("G (!" + ((OS) preOS).ltlString() + ")");
				}
			}
			innerOpDisjuncts.add("(" + Utils.conjunct(preOSConjuncts) + ")");
		}
		String phi = "((" + Utils.disjunct(innerOpDisjuncts) + ") & exe" + operand.exeNum + ") -> ((" + thetaBar(operand)
				+ ") & G exe" + operand.exeNum + " & G " + operand.getConstraint();

		// nested CFs
		ArrayList<String> nestedCFConjuncts = new ArrayList<String>();
		for (CF nestedCF : operand.nestedCFs)
			nestedCFConjuncts.add(phiBar(nestedCF));
		if (!nestedCFConjuncts.isEmpty())
			phi += " & (" + Utils.conjunct(nestedCFConjuncts) + ")";
		phi += ")";
		return phi;
	}

	private String phiBarAlt2(Operand operand, CF cf) {
		// for each operand
		ArrayList<String> innerOpDisjuncts = new ArrayList<String>();
		for (Operand innerOp : cf.operands) {
			// for each pre os
			ArrayList<String> preOSConjuncts = new ArrayList<String>();
			for (Ordered preOS : innerOp.getPreOrdereds()) {
				if (preOS instanceof OS) {
					preOSConjuncts.add("G (!" + ((OS) preOS).ltlString() + ")");
				}
			}
			innerOpDisjuncts.add("(" + Utils.conjunct(preOSConjuncts) + ")");
		}
		String phi = "((" + Utils.disjunct(innerOpDisjuncts) + ") & (!exe" + operand.exeNum + ")) -> " + "(G (!exe"
				+ operand.exeNum + ") & G (!" + operand.getConstraint() + ")";

		// nested CFs
		ArrayList<String> nestedOperands = new ArrayList<String>();
		for (CF nestedCF : operand.nestedCFs)
			for (Operand nestedOperand : nestedCF.operands)
				nestedOperands.add("!" + nestedOperand.getConstraint());
		if (!nestedOperands.isEmpty())
			phi += " & G (" + Utils.conjunct(nestedOperands) + ")";
		phi += ")";
		return phi;
	}

	private String nu(CF cf) {
		String nu = "";
		if (debug)
			nu += Utils.debugPrint("Beginning Nu for CF (" + cf.num + ") (nu())");
		// for each operand
		ArrayList<String> xorOps = new ArrayList<String>();
		ArrayList<String> opConjuncts = new ArrayList<String>();
		ArrayList<String> negOpConjuncts = new ArrayList<String>();
		ArrayList<String> osConjuncts = new ArrayList<String>();
		for (Operand op : cf.operands) {
			xorOps.add("exe" + op.exeNum);
			opConjuncts.add("(exe" + op.exeNum + " -> " + op.getConstraint() + ")");
			negOpConjuncts.add("(!" + op.getConstraint() + ")");
			for (OS os : op.getOSes()) {
				osConjuncts.add("(" + os.ltlString() + " -> exe" + op.exeNum + ")");
			}
		}

		nu += Utils.globally("(((" + Utils.xor(xorOps) + ") & (" + Utils.conjunct(opConjuncts) + ")) | ("
				+ Utils.conjunct(negOpConjuncts) + ")) & (" + Utils.conjunct(osConjuncts) + ")");
		if (debug)
			nu += Utils.debugPrint("Beginning Nu for CF (" + cf.num + ") (nu())");
		return nu;
	}
}
