package translators;

import java.lang.Character.Subset;
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
		return osVars.substring(0, osVars.length() - 1);
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
		

		// remove duplicates and add to String
		if (defines != null && defines.size() > 0) {
			uniques = new LinkedHashSet<String>(constraints);
			for (String var : uniques)
				constraintString += "\n" + var;
		}
		return constraintString;
	}

	/**
	 * Calculates the maximum exe value for var creation and initialization
	 * 
	 * @param ltlSDs
	 *            List of sequence diagrams.
	 * @return
	 */
	public static int countExeVars(ArrayList<String> ltlSDs) {
		int max = 0;
		for (String ltl : ltlSDs)
			for (int i = max; i < Integer.MAX_VALUE; i++)
				if (ltl.contains("exe" + (max + 1)))
					max++;
				else
					break;
		return max;

	}

	/**
	 * Generates variables declarations for exes.
	 * 
	 * @param ltlSDs
	 *            List of sequence diagrams to generate variables for.
	 * @return
	 */
	public static String generateExeVars(ArrayList<String> ltlSDs) {
		int max = countExeVars(ltlSDs);
		String vars = "";
		for (int i = 1; i <= max; i++)
			vars += "exe" + i + ": boolean;\n";
		return vars + "\n";
	}

	/**
	 * Genenerates instantiations for exe variables
	 * 
	 * @param ltlSDs
	 *            Sequence diagrams to generate instantiations for
	 * @param init
	 *            List of sequence diagrams to generate variables for.
	 * @return
	 */
	public static String initializeExeVars(ArrayList<String> ltlSDs, boolean init) {
		int max = countExeVars(ltlSDs);
		String vars = "";
		String initString = "TRUE";
		if (!init)
			initString = "FALSE";

		for (int i = 1; i <= max; i++)
			vars += "init(exe" + i + ") := " + initString + ";\n";
		return vars;
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

	/**
	 * Generates initializations for boolean variables.
	 * 
	 * @param sds
	 *            Sequence Diagrams to generate initializations for.
	 * @param init
	 *            If true, variables willbe initialized to "TRUE". "FALSE"
	 *            otherwise.
	 * @return
	 */
	public static String initializeVars(ArrayList<SD> sds, boolean init) {
		return "ASSIGN\n" + initializeOSVars(sds, init) + initializeConstraintVars(sds, init);
	}

	/**
	 * Generates initializations for boolean variables based on init value.
	 * 
	 * @param sds
	 *            Sequence Diagrams to generate initializations for.
	 * @param init
	 *            If true, variables will be initialized to "TRUE". "FALSE"
	 *            otherwise.
	 * @return
	 */
	private static String initializeOSVars(ArrayList<SD> sds, boolean init) {
		List<String> osList = new ArrayList<String>();
		String osVars = "";
		String initString = "TRUE";
		if (!init)
			initString = "FALSE";
		for (SD sd : sds)
			for (Lifeline lifeline : sd.lifelines)
				for (OS os : lifeline.oses)
					osList.add("init(" + os.ltlString() + ") := " + initString + ";");
		Set<String> uniques = new LinkedHashSet<String>(osList);
		for (String var : uniques)
			osVars += var + "\n";
		return osVars;
	}

	/**
	 * Generates initializations for boolean variables based on init value.
	 * Initializes variables representing constraints.
	 * 
	 * @param sds
	 *            Sequence diagrams to initialize constraints for.
	 * @param init
	 *            If true, variables will be initialized to "TRUE". "FALSE"
	 *            otherwise.
	 * @return
	 */
	private static String initializeConstraintVars(ArrayList<SD> sds, boolean init) {
		String constraintString = "";
		List<String> constraints = new ArrayList<String>();
		String initString = "TRUE";
		if (!init)
			initString = "FALSE";
		for (SD sd : sds)
			for (CF cf : sd.cfs)
				for (Operand op : cf.operands)
					if (!op.constraint.constraint.toLowerCase().equals("else"))
						constraints.add("init(" + op.constraint.constraint + ") := " + initString + ";");

		// remove duplicates and generate String
		Set<String> uniques = new LinkedHashSet<String>(constraints);
		for (String var : uniques)
			constraintString += var + "\n";

		return constraintString.substring(0, constraintString.length() - 1);
	}

}
