package tukano.impl.rest.servers;

import java.net.URI;
import java.util.List;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import tukano.api.Short;
import tukano.impl.api.java.ExtendedShorts;
import tukano.impl.api.rest.RestExtendedShorts;
import tukano.impl.java.servers.JavaShortsReplicated;
import tukano.impl.java.servers.operations.Operation;

@Singleton
public class ShortsReplicatedResource extends RestResource implements RestExtendedShorts {

	final ExtendedShorts impl;
	public ShortsReplicatedResource() {
		this.impl = new JavaShortsReplicated();
	}
	
	
	@SuppressWarnings("resource")
    @Override
	public Short createShort(String userId, String password) {
        var res = impl.createShort(userId, password);
        if (res.isOK()) {
           if (res.redirectURI() == null)
				throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
			else {
				String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId + "?password=" + password;
				throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
			} 
        }else {
            throw new WebApplicationException(super.statusCodeFrom(res));
		}
	}

	@SuppressWarnings("resource")
    @Override
	public void deleteShort(String shortId, String password) {
        var res = impl.deleteShort(shortId, password);
        if (res.isOK()) {
           if (res.redirectURI() == null)
				throw new WebApplicationException(Response.noContent().header(HEADER_VERSION, res.version()).entity(res.value()).build());
			else {
				String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + shortId + "?password=" + password;
				throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
			} 
        }else {
            throw new WebApplicationException(super.statusCodeFrom(res));
		}
	}

	@SuppressWarnings("resource")
    @Override
	public Short getShort(Long version, String shortId) {
        var res = impl.getShort(version, shortId);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + shortId;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}
	@SuppressWarnings("resource")
    @Override
	public List<String> getShorts(Long version, String userId) {
        var res = impl.getShorts(version, userId);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId + "/shorts";
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public void follow(String userId1, String userId2, boolean isFollowing, String password) {
        var res = impl.follow(userId1, userId2, isFollowing, password);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.noContent().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId1 + "/" + userId2 + "/followers" + "?password=" + password;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public List<String> followers(Long version, String userId, String password) {
        var res = impl.followers(version, userId, password);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId + "/followers" + "?password=" + password;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public void like(String shortId, String userId, boolean isLiked, String password) {
        var res = impl.like(shortId, userId, isLiked, password);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.noContent().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + shortId + "/" + userId + "/likes" + "?password=" + password;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public List<String> likes(Long version, String shortId, String password) {
        var res = impl.likes(version, shortId, password);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + shortId + "/likes" + "?password=" + password;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public List<String> getFeed(Long version, String userId, String password) {
        var res = impl.getFeed(version, userId, password);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.ok().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId + "/feed" + "?password=" + password;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}

	@SuppressWarnings("resource")
    @Override
	public void deleteAllShorts(String userId, String password, String token) {
        var res = impl.deleteAllShorts(userId, password, token);
        if (res.isOK()) {
            if (res.redirectURI() == null)
                 throw new WebApplicationException(Response.noContent().header(HEADER_VERSION, res.version()).entity(res.value()).build());
             else {
                 String requestURI = res.redirectURI().toString() + RestExtendedShorts.PATH + "/" + userId + "/shorts" + "?password=" + password + "?token=" + token;
                 throw new WebApplicationException(Response.temporaryRedirect(URI.create(requestURI)).build());
             } 
         }else {
             throw new WebApplicationException(super.statusCodeFrom(res));
         }
	}	

	@SuppressWarnings("resource")
    @Override
	public void opFromPrimary(Long version, String operation, String opType, String token) {
		var res = impl.opFromPrimary(version, operation, opType, token);

		if (!res.isOK()) {
			throw new WebApplicationException(super.statusCodeFrom(res));
		}

		throw new WebApplicationException(Response.noContent().header(HEADER_VERSION, res.version()).build());
	}

	@Override
	public List<Operation> getOperations(Long version, String token) {
        return super.resultOrThrow( impl.getOperations(version, token));
	}
}

