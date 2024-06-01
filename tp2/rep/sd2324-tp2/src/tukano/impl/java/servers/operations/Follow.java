package tukano.impl.java.servers.operations;

public class Follow extends Operation {

    private final String userId1;
    private final String userId2;
    private final Boolean isFollowing;

    public Follow (String userId1, String userId2, Boolean isFollowing) {
        super(OperationType.FOLLOW);
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.isFollowing = isFollowing;
    }

    @Override
	public String toString() {
		return "CreateShort{" +
				", userId1='" + userId1 + '\'' +
                ", userId2='" + userId2 + '\'' +
                ", isFollowing='" + isFollowing + '\'' +
				'}';
	}

    public String getUserId1() {
		return userId1;
	}

    public String getUserId2() {
		return userId2;
	}

    public Boolean getIsFollowing() {
		return isFollowing;
	}
}

