package enums;

public enum MessageType {
	SYNC, ASYNC;

	/**
	 * Returns appropriate MessageType corresponding to mt.
	 * 
	 * @param op
	 *            String value of message type.
	 * @return MessageType value.
	 */
	public static MessageType getMessageType(String mt) {
		mt = mt.toLowerCase();

		if (mt.equals("asynchcall"))
			return ASYNC;
		else if (mt.equals("synchcall"))
			return SYNC;
		else {
			System.err.println("getMessageType(): \"" + mt + "\" does not correspond to a valid message type.");
			return null;
		}
	}

	public String toString() {
		switch (this) {
		case SYNC:
			return "synchronous";
		case ASYNC:
			return "asynchronous";
		default:
			System.err.println("Error converting MessageType to string.");
			return null;
		}
	}
}