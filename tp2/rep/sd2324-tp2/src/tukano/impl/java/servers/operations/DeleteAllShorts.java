package tukano.impl.java.servers.operations;

public class DeleteAllShorts extends Operation {

    private final String userId;
    private final String password;

    public DeleteAllShorts (String userId, String password) {
        super(OperationType.DELETE_ALL_SHORTS);
        this.userId = userId;
        this.password = password;
    }

    @Override
	public String toString() {
		return "CreateShort{" +
				", userId='" + userId + '\'' +
                ", password=" + password +
				'}';
	}

    public String getUserId() {
		return userId;
	}
    public String getPassword() {
		return password;
	}
}
