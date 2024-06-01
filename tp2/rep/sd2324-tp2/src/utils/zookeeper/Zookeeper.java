package utils.zookeeper;

import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Zookeeper {
	
	private ZooKeeper _client;
	private static Zookeeper instance;
	private static final String SERVERS = "kafka";
	private final int TIMEOUT = 5000;

	private Zookeeper(String path) throws Exception {
		this.connect(SERVERS, TIMEOUT);
		createNode(path, new byte[0], CreateMode.PERSISTENT);
	}

	public static Zookeeper getInstance(String path) throws Exception {
		if (instance == null) {
			instance = new Zookeeper(path);
		}
		return instance;
	}

	public synchronized ZooKeeper client() {
		if (_client == null || !_client.getState().equals(ZooKeeper.States.CONNECTED)) {
			throw new IllegalStateException("ZooKeeper is not connected.");
		}
		return _client;
	}

	public void registerWatcher( Watcher w ) {
		client().register( w );
	}

	private void connect(String host, int timeout) throws IOException, InterruptedException {
		var connectedSignal = new CountDownLatch(1);
		_client = new ZooKeeper(host, TIMEOUT, (e) -> {
			if (e.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
				connectedSignal.countDown();
			}
		});
		connectedSignal.await();
	}

	public String createNode(String path, byte[] data, CreateMode mode) {
		try {
			return client().create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
		} catch (KeeperException.NodeExistsException x) {
			return path;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public List<String> getChildren(String path) {
		try {
			return client().getChildren(path, false);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public List<String> getChildren(String path, Watcher watcher) {
		try {
			return client().getChildren(path, watcher);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
