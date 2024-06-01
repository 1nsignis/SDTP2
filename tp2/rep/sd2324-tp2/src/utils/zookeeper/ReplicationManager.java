package utils.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import io.grpc.xds.shaded.io.envoyproxy.envoy.extensions.transport_sockets.tls.v3.Secret;
import tukano.impl.api.rest.RestExtendedShorts;
import tukano.impl.api.java.ExtendedShorts;
import static tukano.impl.java.clients.Clients.ShortsClients;
import tukano.impl.java.servers.JavaShortsReplicated;
import tukano.impl.java.servers.operations.Operation;
//import tukano.impl.java.servers.operations.Operation;
import utils.Token;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static tukano.impl.rest.servers.AbstractRestServer.SERVER_URI;

public class ReplicationManager implements Watcher {

	private URI primaryURI;
	private static ReplicationManager instance;
	private long version;
	private final Zookeeper zk;
	private final String node;
	private boolean primary;
	private static final String ROOT = "/shortszk";
	private final JavaShortsReplicated shorts;

	private ReplicationManager(JavaShortsReplicated shorts) {
		try {
			zk = Zookeeper.getInstance(ROOT);
			node = zk.createNode(ROOT + RestExtendedShorts.PATH + "_", SERVER_URI.getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
			selectPrimary(zk.getChildren(ROOT, this));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		primary = false;
		version = 0;
		this.shorts = shorts;
	}

	public static ReplicationManager getInstance(JavaShortsReplicated shorts) {
		if (instance == null)
			instance = new ReplicationManager(shorts);
		return instance;
	}

    private void selectPrimary(List<String> nodes) {
        try {

            List<String> orderedNodes = new LinkedList<>(nodes);
            Collections.sort(orderedNodes);
            String primaryNode = orderedNodes.get(0);
            String primaryNodePath = ROOT + "/" + primaryNode;
            primary = node.equals(primaryNodePath);
            byte[] primaryNodeData = zk.client().getData(primaryNodePath, false, new Stat());
            primaryURI = new URI(new String(primaryNodeData));

        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse primary node URI", e);
        } catch (KeeperException e) {
            throw new RuntimeException("Failed to access ZooKeeper", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            throw new RuntimeException("Thread interrupted while accessing ZooKeeper", e);
        }
	}
    
	@Override
	public void process(WatchedEvent event) {
		selectPrimary(zk.getChildren(ROOT));

        URI previousPrimaryURI;
        try {
            previousPrimaryURI = new URI(primaryURI.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!previousPrimaryURI.equals(primaryURI)) {
            handlePrimaryChange();
        }

	}

    private void handlePrimaryChange() {
        List<ExtendedShorts> clients = ShortsClients.all();

        ExtendedShorts clientWithLargestOperations = (ExtendedShorts) clients.stream()
                .map(client -> client.getOperations(version, Token.get()))
                .filter(response -> response != null && response.isOK())
                .max(Comparator.comparingInt(response -> response.value().size()))
                .orElse(null);

        if (clientWithLargestOperations != null) {
            shorts.executeOperations(clientWithLargestOperations.getOperations(version, Token.get()).value());
        }
    }

	public long getCurrentVersion() {
		return version;
	}

	public void setVersion(long newVersion) {
		version = newVersion;
	}

	public void incrementVersion() {
		++version;
	}

    public boolean isPrimary() {
        return primary;
    }

	public boolean isSecondary() {
		return !primary;
	}

	public URI getPrimaryURI() {
		return primaryURI;
	}

	
}