package tukano.impl.rest.servers;

import org.glassfish.jersey.server.ResourceConfig;

//import tukano.impl.rest.servers.utils.CustomLoggingFilter;
import tukano.impl.rest.servers.utils.GenericExceptionMapper;
import tukano.api.java.Blobs;
import utils.Args;
import utils.State;

import java.util.logging.Logger;

public class BlobsProxyServer extends AbstractRestServer {

	public static final int PORT = 8080;

	private static final Logger Log = Logger.getLogger(BlobsProxyServer.class.getName());
	private static boolean state;
	private static String apiKey;
	private static String apiSecret;
	private static String accessKey;

	BlobsProxyServer(int port) {
		super(Log, Blobs.NAME, port);
	}

	@Override
	void registerResources(ResourceConfig config) {

		config.register(BlobsProxyResource.class);
		config.register(new GenericExceptionMapper());
		// config.register(new CustomLoggingFilter());
	}

	public static void main(String[] args) throws Exception {
		/*
		 * state = Boolean.parseBoolean(args[0]);
		 * Args.use(args);
		 * apiKey = Args.valueOf("-apiKey", "");
		 * apiSecret = Args.valueOf("-apiSecret", "");
		 * accessKey = Args.valueOf("-accessKey", "");
		 */
		State.set(Boolean.parseBoolean(args[0]));
		Args.use(args);
		new BlobsProxyServer(Args.valueOf("-port", PORT)).start();
	}
}
