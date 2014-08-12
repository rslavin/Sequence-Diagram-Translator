package translators;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import enums.Operator;
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
		return "VAR\n" + generateOSVars(sds) + generateExeVars(sds) + generateConstraintVars(sds);
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
		return "ASSIGN\n" + initializeOSVars(sds, init) + initializeExeVars(sds, init) + initializeConstraintVars(sds, init);
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
	 * Generates VAR entries for constraints. Removes duplicates. "else"
	 * constraints are skipped here and handled in the ltl formula by replacing
	 * the constraint with the negation of the conjunction of other constraints
	 * in the combined fragment.
	 * 
	 * @param sds
	 *            Sequence diagrams to create VAR section for.
	 * @return
	 */
	private static String generateConstraintVars(ArrayList<SD> sds) {
		String constraintString = "";
		List<String> constraints = new ArrayList<String>();
		for (SD sd : sds)
			for (CF cf : sd.cfs)
				for (Operand op : cf.operands)
					if (!op.constraint.constraint.toLowerCase().equals("else") && !op.constraint.constraint.toLowerCase().equals("true"))
						constraints.add(op.constraint.constraint + " : boolean;");
		// ELSE is converted in the formula. Macros are not needed.
		// remove duplicates and generate String
		Set<String> uniques = new LinkedHashSet<String>(constraints);

		for (String var : uniques)
			constraintString += "\n" + var;
		return constraintString;
	}

	/**
	 * Generates variables declarations for exes.
	 * 
	 * @param ltlSDs
	 *            List of sequence diagrams to generate variables for.
	 * @return
	 */
	private static String generateExeVars(ArrayList<SD> sds) {
		String exeVars = "";
		for (SD sd : sds)
			for (CF cf : sd.cfs)
				for (Operand op : cf.operands)
					if (op.cf.operator.equals(Operator.ALT))
						exeVars += "exe" + op.exeNum + ": boolean;\n";
		if (exeVars.length() > 0)
			return exeVars.substring(0, exeVars.length() - 1);
		return exeVars;
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
	private static String initializeExeVars(ArrayList<SD> sds, boolean init) {
		String exeVars = "";
		String initString = "TRUE";
		if (!init)
			initString = "FALSE";
		for (SD sd : sds)
			for (CF cf : sd.cfs)
				for (Operand op : cf.operands)
					if (op.cf.operator.equals(Operator.ALT))
						exeVars += "init(exe" + op.exeNum + ") := " + initString + ";\n";

		return exeVars;
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
					if (!op.constraint.constraint.toLowerCase().equals("else") && !op.constraint.constraint.toLowerCase().equals("true"))
						constraints.add("init(" + op.constraint.constraint + ") := " + initString + ";");

		// remove duplicates and generate String
		Set<String> uniques = new LinkedHashSet<String>(constraints);
		for (String var : uniques)
			constraintString += var + "\n";
		if(constraintString.length() > 0)
			return constraintString.substring(0, constraintString.length() - 1);
		return "";
	}

}
