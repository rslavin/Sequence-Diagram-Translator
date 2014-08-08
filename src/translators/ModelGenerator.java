package translators;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import sdComponents.*;

public class ModelGenerator {
	/**
	 * Generates VAR and DEFINE sections for a sequence diagram.
	 * 
	 * @param sds
	 *            Sequence diagrams to create VAR and DEFINE sections for.
	 * @return
	 */
	public static String generateVars(ArrayList<SD> sds) {
		return "VAR\n" + generateOSVars(sds) + generateConstraintVars(sds);
	}

	/**
	 * Generates VAR entries for OSes. Removes duplicates.
	 * 
	 * @param sds
	 *            Sequence diagrams to create VAR section for.
	 * @return
	 */
	private static String generateOSVars(ArrayList<SD> sds) {
		List<String> osList = new ArrayList<String>();
		String osVars = "";
		for (SD sd : sds)
			for (Lifeline lifeline : sd.lifelines)
				for (OS os : lifeline.oses)
					osList.add(os.ltlString() + ": boolean;");
		Set<String> uniques = new LinkedHashSet<String>(osList);
		for (String var : uniques)
			osVars += var + "\n";
		return osVars;
	}

	/**
	 * Generates VAR entries for constraints. Removes duplicates. Attempts to
	 * handle "else" constraints. Currently only works if one "else" exists in
	 * entire SD. If multiples exist, they will all be titled "else" and cause
	 * an error in NuSMV.
	 * 
	 * @param sds
	 *            Sequence diagrams to create VAR section for.
	 * @return
	 */
	private static String generateConstraintVars(ArrayList<SD> sds) {
		String constraintString = "";
		List<String> defines = new ArrayList<String>();
		List<String> constraints = new ArrayList<String>();
		for (SD sd : sds)
			for (CF cf : sd.cfs)
				for (Operand op : cf.operands)
					if (!op.constraint.constraint.toLowerCase().equals("else"))
						constraints.add(op.constraint.constraint + " : boolean;");
					else
						defines.add(generateElseConstraint(cf));
		// remove duplicates and generate String
		Set<String> uniques = new LinkedHashSet<String>(constraints);
		for (String var : uniques)
			constraintString += var + "\n";

		// remove duplicates and add to String
		if (defines != null && defines.size() > 0) {
			uniques = new LinkedHashSet<String>(constraints);
			for (String var : uniques)
				constraintString += "\n" + var;
		}
		return constraintString;
	}

	/**
	 * Generates the "else" macro. Triggered by generateConstrainVars() when a
	 * constraint equals "else".
	 * 
	 * @param cf
	 *            Combined fragment with "else" constraint.
	 * @return
	 */
	private static String generateElseConstraint(CF cf) {
		// TODO currently only works if there is ONE else in the ENTIRE SD
		// TODO elses need to be renamed to be unique in the preprocessor
		String define = "DEFINE\nelse :=";
		for (Operand op : cf.operands)
			if (!op.constraint.constraint.toLowerCase().equals("else"))
				define += " !" + op.constraint.constraint + " &";
		return define.substring(0, define.length() - 2) + ";";
	}
}
