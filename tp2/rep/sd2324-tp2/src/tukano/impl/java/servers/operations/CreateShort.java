package tukano.impl.java.servers.operations;

public class CreateShort extends Operation {

    private final String userId;

    public CreateShort (String userId) {
        super(OperationType.CREATE_SHORT);
        this.userId = userId;
    }

    @Override
	public String toString() {
		return "CreateShort{" +
				", userId='" + userId + '\'' +
				'}';
	}

    public String getUserId() {
		return userId;
	}
}
