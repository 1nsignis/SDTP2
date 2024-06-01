package tukano.impl.rest.servers;

import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tukano.impl.discovery.Discovery;
import tukano.impl.java.servers.AbstractServer;
import utils.IP;


public abstract class AbstractRestServer extends AbstractServer {
	private static final String SERVER_BASE_URI = "https://%s:%s%s";
	private static final String REST_CTX = "/rest";
	public static String SERVER_URI;

	protected AbstractRestServer(Logger log, String service, int port) {
		super(log, service, String.format(SERVER_BASE_URI, IP.hostName(), port, REST_CTX));
		SERVER_URI = String.format(SERVER_BASE_URI, IP.hostName(), port, REST_CTX);
	}

	protected void start() throws UnknownHostException, NoSuchAlgorithmException {
		
		ResourceConfig config = new ResourceConfig();
		
		registerResources( config );
		
		JdkHttpServerFactory.createHttpServer( URI.create(serverURI.replace(IP.hostName(), INETADDR_ANY)), config, SSLContext.getDefault());
		
		Discovery.getInstance().announce(service, super.serverURI);
		
		Log.info(String.format("%s Server ready @ %s\n",  service, serverURI));
	}
	
	abstract void registerResources( ResourceConfig config );
}