package tukano.impl.api.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import tukano.api.rest.RestShorts;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import tukano.impl.java.servers.operations.Operation;

@Path(RestShorts.PATH)
public interface RestExtendedShorts extends RestShorts {

	String TOKEN = "token";
	String HEADER_VERSION = "X-SHORTS";
	
	@DELETE
	@Path("/{" + USER_ID + "}" + SHORTS)
	void deleteAllShorts(@PathParam(USER_ID) String userId, @QueryParam(PWD) String password, @QueryParam(TOKEN) String token);
		
	@POST
	@Path("/operation")
	@Consumes(MediaType.APPLICATION_JSON)
	void opFromPrimary(@HeaderParam(HEADER_VERSION) Long version, String operation, @QueryParam("opType") String opType, @QueryParam(TOKEN) String token);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<Operation> getOperations(@HeaderParam(HEADER_VERSION) Long version, @QueryParam(TOKEN) String token);
}
