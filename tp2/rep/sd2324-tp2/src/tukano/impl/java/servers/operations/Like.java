package tukano.impl.java.servers.operations;

import tukano.api.Short;

public class Like extends Operation {

    private final String shortId;
    private final String userId;
    private final Boolean isLiked;
    private final Short shrt;

    public Like (String shortId, String userId, Boolean isLiked, Short shrt) {
        super(OperationType.LIKE);
        this.shortId = shortId ;
        this.userId = userId;
        this.isLiked = isLiked;
        this.shrt = shrt;
    }

    @Override
	public String toString() {
		return "CreateShort{" +
				", shortId='" + shortId + '\'' +
                ", userId='" + userId + '\'' +
                ", isLiked='" + isLiked + '\'' +
                ", shrt=" + shrt +
				'}';
	}

    public String getShortId() {
		return shortId;
	}

    public String getUserId() {
		return userId;
	}

    public Boolean getIsLiked() {
		return isLiked;
	}
    public Short getShort() {
		return shrt;
	}
}

