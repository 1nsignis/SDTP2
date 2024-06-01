package tukano.impl.java.servers;

import tukano.api.Short;
import tukano.api.java.Result;

public class JavaShorts extends AbstractJavaShorts {
	
	
	@Override
	public Result<Short> createShort(String userId, String password) {
		return _createShort(userId, password);
	}
	
	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		return _deleteShort(shortId, password);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		return _follow(userId1, userId2, isFollowing, password);	
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		return _like(shortId, userId, isLiked, password);
	}
	// Extended API 
	@Override
	public Result<Void> deleteAllShorts(String userId, String password, String token) {
		return _deleteAllShorts(userId, password, token);
	}

}

