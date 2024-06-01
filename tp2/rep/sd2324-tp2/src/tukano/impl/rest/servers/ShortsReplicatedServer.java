package tukano.impl.rest.servers;

import org.glassfish.jersey.server.ResourceConfig;

//import tukano.impl.rest.servers.utils.CustomLoggingFilter;
import tukano.impl.rest.servers.utils.GenericExceptionMapper;
import tukano.api.java.Shorts;
import utils.Args;


import java.util.logging.Logger;

public class ShortsReplicatedServer extends AbstractRestServer {

	public static final int PORT = 8080;

	private static final Logger Log = Logger.getLogger(ShortsReplicatedServer.class.getName());

	ShortsReplicatedServer(int port) {
		super(Log, Shorts.NAME, port);
	}

	@Override
	void registerResources(ResourceConfig config) {

		config.register(ShortsReplicatedResource.class);
		config.register(new GenericExceptionMapper());
		// config.register(new CustomLoggingFilter());
	}

	public static void main(String[] args) throws Exception {
		Args.use(args);
		new ShortsReplicatedServer(Args.valueOf("-port", PORT)).start();
	}
}
