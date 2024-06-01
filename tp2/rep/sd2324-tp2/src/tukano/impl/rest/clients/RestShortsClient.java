package tukano.impl.rest.clients;

import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.rest.RestShorts;
import tukano.impl.api.java.ExtendedShorts;
import tukano.impl.api.rest.RestExtendedShorts;
import tukano.impl.java.servers.operations.Operation;

public class RestShortsClient extends RestClient implements ExtendedShorts{

	public RestShortsClient(String serverURI) {
		super(serverURI, RestShorts.PATH);
	}

	public Result<Short> _createShort(String userId, String password) {
		return super.toJavaResult(
				target
				.path(userId)
				.queryParam(RestShorts.PWD, password )
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post( Entity.json(null)), Short.class);
	}

	public Result<Void> _deleteShort(String shortId, String password) {
		return super.toJavaResult(
				target
				.path(shortId)
				.queryParam(RestShorts.PWD, password )
				.request()
				.delete());
	}

	public Result<Short> _getShort(Long version, String shortId) {
		return super.toJavaResult(
				target
				.path(shortId)
				.request()
				.header(RestShorts.HEADER_VERSION, version)
				.get(), Short.class);
	}

	public Result<List<String>> _getShorts(Long version, String userId) {
		return super.toJavaResult(
				target
				.path(userId)
				.path(RestShorts.SHORTS)
				.request()
				.header(RestShorts.HEADER_VERSION, version)
				.accept( MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	public Result<Void> _follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.toJavaResult(
				target
				.path(userId1)
				.path(userId2)
				.path(RestShorts.FOLLOWERS)
				.queryParam(RestShorts.PWD, password )
				.request()
				.post( Entity.entity(isFollowing, MediaType.APPLICATION_JSON)));
	}

	public Result<List<String>> _followers(Long version, String userId, String password) {
		return super.toJavaResult(
				target
				.path(userId)
				.path(RestShorts.FOLLOWERS)
				.queryParam(RestShorts.PWD, password )
				.request()
				.header(RestShorts.HEADER_VERSION, version)
				.accept( MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	public Result<Void> _like(String shortId, String userId, boolean isLiked, String password) {
		return super.toJavaResult(
				target
				.path(shortId)
				.path(userId)
				.path(RestShorts.LIKES)
				.queryParam(RestShorts.PWD, password )
				.request()
				.post( Entity.entity(isLiked, MediaType.APPLICATION_JSON)));
	}

	public Result<List<String>> _likes(Long version, String shortId, String password) {
		return super.toJavaResult(
				target
				.path(shortId)
				.path(RestShorts.LIKES)
				.queryParam(RestShorts.PWD, password )
				.request()
				.header(RestShorts.HEADER_VERSION, version)
				.accept( MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	public Result<List<String>> _getFeed(Long version, String userId, String password) {
		return super.toJavaResult(
				target
				.path(userId)
				.path(RestShorts.FEED)
				.queryParam(RestShorts.PWD, password )
				.request()
				.header(RestShorts.HEADER_VERSION, version)
				.accept( MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	public Result<Void> _deleteAllShorts(String userId, String password, String token) {
		return super.toJavaResult(
				target
				.path(userId)
				.path(RestShorts.SHORTS)
				.queryParam(RestExtendedShorts.PWD, password )
				.queryParam(RestExtendedShorts.TOKEN, token )
				.request()
				.delete());
	}
	
	public Result<Void> _verifyBlobURI(String blobId) {
		return super.toJavaResult(
				target
				.path(blobId)
				.request()
				.get());
	}

	public Result<Void> _opFromPrimary(Long version, String operation, String opType, String token) { 
		return super.toJavaResult(
			target
			.queryParam(RestExtendedShorts.TOKEN, token)
			.request()
			.header(RestExtendedShorts.HEADER_VERSION, version)
			.post(Entity.entity(operation, MediaType.APPLICATION_JSON)));
	}

	@Override
	public Result<List<Operation>> getOperations(Long version, String token) {
		return null;
	}

	@Override
	public Result<Void> opFromPrimary(Long version, String operation, String opType, String token) { 
		return super.reTry( () -> _opFromPrimary(version, operation, opType, token));
	}
		
	@Override
	public Result<Short> createShort(String userId, String password) {
		return super.reTry( () -> _createShort(userId, password));
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		return super.reTry( () -> _deleteShort(shortId, password));
	}

	@Override
	public Result<Short> getShort(Long version, String shortId) {
		return super.reTry( () -> _getShort(version, shortId));
	}

	@Override
	public Result<List<String>> getShorts(Long version, String userId) {
		return super.reTry( () -> _getShorts(version, userId));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.reTry( () -> _follow(userId1, userId2, isFollowing, password));
	}

	@Override
	public Result<List<String>> followers(Long version, String userId, String password) {
		return super.reTry( () -> _followers(version, userId, password));
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		return super.reTry( () -> _like(shortId, userId, isLiked, password));
	}

	@Override
	public Result<List<String>> likes(Long version, String shortId, String password) {
		return super.reTry( () -> _likes(version, shortId, password));
	}

	@Override
	public Result<List<String>> getFeed(Long version, String userId, String password) {
		return super.reTry( () -> _getFeed(version, userId, password));
	}

	@Override
	public Result<Void> deleteAllShorts(String userId, String password, String token) {
		return super.reTry( () -> _deleteAllShorts(userId, password, token));
	}
}
