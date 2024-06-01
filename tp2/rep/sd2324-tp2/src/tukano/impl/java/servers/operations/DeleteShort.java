package tukano.impl.java.servers.operations;

import tukano.api.Short;

public class DeleteShort extends Operation {

    private final String shortId;
    private final Short shrt;

    public DeleteShort (String shortId, Short shrt) {
        super(OperationType.DELETE_SHORT);
        this.shortId = shortId;
        this.shrt = shrt;
    }

    @Override
	public String toString() {
		return "CreateShort{" +
				", shortId='" + shortId + '\'' +
                ", shrt=" + shrt +
				'}';
	}

    public String getShortId() {
		return shortId;
	}

    public Short getShort() {
		return shrt;
	}
}
