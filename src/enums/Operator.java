package enums;

public enum Operator {
	ALT, OPT, PAR, LOOP, ASSERT, NEG, WEAK, CRIT, STRICT, CONS, IGNORE;

	/**
	 * Returns appropriate Operator corresponding to op.
	 * 
	 * @param op
	 *            String value of operator.
	 * @return Operator value.
	 */
	public static Operator getOperator(String op) {
		op = op.toLowerCase();

		if (op.equals("alt") || op.equals("alternative"))
			return ALT;
		else if (op.equals("opt") || op.equals("optional"))
			return OPT;
		else if (op.equals("loop"))
			return LOOP;
		else if (op.equals("assert"))
			return ASSERT;
		else if (op.equals("neg"))
			return NEG;
		else if (op.equals("weak"))
			return WEAK;
		else if (op.equals("crit") || op.equals("critical"))
			return CRIT;
		else if (op.equals("strict"))
			return STRICT;
		else if (op.equals("cons") || op.equals("consider"))
			return CONS;
		else if (op.equals("ignore"))
			return IGNORE;
		else {
			System.err.println("getOperator(): \"" + op + "\" does not correspond to a valid Operator.");
			return null;
		}
	}

	public String toString() {
		switch (this) {
		case ALT:
			return "alt";
		case OPT:
			return "opt";
		case LOOP:
			return "loop";
		case ASSERT:
			return "assert";
		case NEG:
			return "neg";
		case WEAK:
			return "weak";
		case CRIT:
			return "crit";
		case STRICT:
			return "strict";
		case CONS:
			return "cons";
		case IGNORE:
			return "ignore";
		default:
			System.err.println("Error converting Operator to string.");
			return null;
		}
	}
};
