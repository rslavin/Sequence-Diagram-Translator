package enums;

public enum OSType {
	SEND, RECEIVE;

	public String toString() {
		switch (this) {
		case SEND:
			return "send";
		case RECEIVE:
			return "receive";
		default:
			System.err.println("Error converting OSType to string.");
			return null;
		}
	}
};