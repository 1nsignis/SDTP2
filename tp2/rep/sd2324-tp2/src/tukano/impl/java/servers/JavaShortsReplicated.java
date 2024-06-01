package tukano.impl.java.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tukano.api.Short;
import tukano.api.java.Result;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
//import java.util.logging.Logger;

import utils.Token;
import utils.zookeeper.ReplicationManager;
import tukano.impl.api.java.ExtendedShorts;
import tukano.impl.java.servers.operations.*;
import static tukano.impl.java.servers.operations.OperationType.valueOf;

import static tukano.impl.java.clients.Clients.ShortsClients;

public class JavaShortsReplicated extends AbstractJavaShorts {
    //private static Logger Log = Logger.getLogger(JavaShortsReplicated.class.getName());
	private final ReplicationManager repManager;
	private final Gson gson;
	private final List<Operation> listOperations;
	private final static int TOLERABLE_FAILS = 1;
	private static final long TIMEOUT = 10000;

	public JavaShortsReplicated() {
		this.gson = new GsonBuilder().create();
		repManager = ReplicationManager.getInstance(this);
		listOperations = new LinkedList<>();
	}

	@Override
	public Result<Short> createShort(String userId, String password) {
		if (repManager.isSecondary())
			return redirectToPrimary();
		var res = _createShort(userId, password);
		if (!res.isOK())
			return res;
		var createShort = new CreateShort(userId);
		processOperation(createShort, OperationType.CREATE_SHORT);
		return Result.ok(res.value(), repManager.getCurrentVersion());
	}

	@Override
	public Result<Short> getShort(Long version, String shortId) {
		long currentVersion = repManager.getCurrentVersion();
		if (currentVersion >= version) {
			var res = super.getShort(version, shortId);
			return Result.ok(res.value(), currentVersion);
		}
		else
			return redirectToPrimary();
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		if (repManager.isSecondary())
			return redirectToPrimary();
		var res = _deleteShort(shortId, password);
		if (!res.isOK())
			return res;
		var deleteShort = new DeleteShort(shortId, getShort(null, shortId).value());
		processOperation(deleteShort, OperationType.DELETE_SHORT);
		return Result.ok(repManager.getCurrentVersion());
	}

	@Override
	public Result<List<String>> getShorts(Long version, String userId) {
		long currentVersion = repManager.getCurrentVersion();
		if (currentVersion >= version) {
			var res = super.getShorts(version, userId);
			return Result.ok(res.value(), currentVersion);
		}
		else
			return redirectToPrimary();
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		if (repManager.isSecondary())
			return redirectToPrimary();
		var res = _follow(userId1, userId2, isFollowing, password);
		if (!res.isOK())
			return res;
		var follow = new Follow(userId1, userId2, isFollowing);
		processOperation(follow, OperationType.FOLLOW);
		return Result.ok(repManager.getCurrentVersion());
	}

	@Override
	public Result<List<String>> followers(Long version, String userId, String password) {
		long currentVersion = repManager.getCurrentVersion();
		if (currentVersion >= version) {
			var res = super.followers(version, userId, password);
			return Result.ok(res.value(), currentVersion);
		}
		else
			return redirectToPrimary();
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		if (repManager.isSecondary())
			return redirectToPrimary();
		var res = _like(shortId, userId, isLiked, password);
		if (!res.isOK())
			return res;
		var like = new Like(shortId, userId, isLiked, getShort(null, shortId).value());
		processOperation(like, OperationType.LIKE);
		return Result.ok(repManager.getCurrentVersion());
	}

	@Override
	public Result<List<String>> likes(Long version, String shortId, String password) {
		long currentVersion = repManager.getCurrentVersion();
		if (currentVersion >= version) {
			var res = super.likes(version, shortId, password);
			return Result.ok(res.value(), currentVersion);
		}
		else
			return redirectToPrimary();
	}

	@Override
	public Result<List<String>> getFeed(Long version, String userId, String password) {
		long currentVersion = repManager.getCurrentVersion();
		if (currentVersion >= version) {
			var res = super.getFeed(version, userId, password);
			return Result.ok(res.value(), currentVersion);
		}
		else
			return redirectToPrimary();
	}

	@Override
	public Result<Void> deleteAllShorts(String userId, String password, String token) {
		if (repManager.isSecondary())
			return redirectToPrimary();
		var res = _deleteAllShorts(userId, password, token);
		if (!res.isOK())
			return res;
		var deleteAll = new DeleteAllShorts(userId, password);
		processOperation(deleteAll, OperationType.DELETE_ALL_SHORTS);
		return Result.ok(repManager.getCurrentVersion());
	}

	private void processOperation(Operation operation, OperationType opType) {
		repManager.incrementVersion();
		listOperations.add(operation);
		String operationJson = gson.toJson(operation);
		propagateOperationToSecondaries(operationJson, opType);
	}

	@Override
	public Result<Void> primaryOperation(Long version, String operation, String opType, String token) {
		if (!Token.matches(token)) {
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		if (valueOf(opType) == OperationType.DELETE_SHORT) {
			DeleteShort op = gson.fromJson(operation, DeleteShort.class);
			listOperations.add(op);
			op_deleteShort(op);
		} else if (valueOf(opType) == OperationType.DELETE_ALL_SHORTS) {
			DeleteAllShorts op = gson.fromJson(operation, DeleteAllShorts.class);
			listOperations.add(op);
			op_deleteAllShorts(op);
		} else if (valueOf(opType) == OperationType.FOLLOW) {
			Follow op = gson.fromJson(operation, Follow.class);
			listOperations.add(op);
			op_follow(op);
		} else if (valueOf(opType) == OperationType.LIKE) {
			Like op = gson.fromJson(operation, Like.class);
			listOperations.add(op);
			op_like(op);
		} else if (valueOf(opType) == OperationType.CREATE_SHORT) {
			CreateShort op = gson.fromJson(operation, CreateShort.class);
			listOperations.add(op);
			op_createShort(op);
		}
		repManager.setVersion(version);
		return Result.ok();
	}

	@Override
	public Result<List<Operation>> getOperations(Long version, String token) {
		if (!Token.matches(token)) {
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}

		long currentVersion = repManager.getCurrentVersion();
		int startIndex = (int) Math.max(listOperations.size() - (currentVersion - version), 0);
		List<Operation> missingOperations = listOperations.subList(startIndex, listOperations.size());

		return Result.ok(missingOperations, currentVersion);
	}

	private <T> Result<T> redirectToPrimary() {
		return Result.ok(repManager.getPrimaryURI());
	}

	private void propagateOperationToSecondaries(String operation, OperationType opType) {
		var clients = ShortsClients.all();
		var theadFinishedSignal = new CountDownLatch(TOLERABLE_FAILS);
		ExecutorService executorService = Executors.newFixedThreadPool(clients.size());

		for (ExtendedShorts client : clients) {
			executorService.submit(() -> {
				var res = client.primaryOperation(repManager.getCurrentVersion(), operation, opType.name(), Token.get());
				if (res != null && res.isOK()) {
					theadFinishedSignal.countDown();
				}
			});
		}

		executorService.shutdown();
		try {
			if (!theadFinishedSignal.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
				System.err.println("Timeout reached before all operations could complete.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread was interrupted while waiting for operations to complete.", e);
		}
	}

	public void executeOperations(List<Operation> operations) {
		for (Operation operation : operations) {
			if (operation.getOperationType() == OperationType.DELETE_SHORT) {
				op_deleteShort((DeleteShort) operation);
			} else if (operation.getOperationType() == OperationType.DELETE_ALL_SHORTS) {
				op_deleteAllShorts((DeleteAllShorts) operation);
			} else if (operation.getOperationType() == OperationType.FOLLOW) {
				op_follow((Follow) operation);
			} else if (operation.getOperationType() == OperationType.LIKE) {
				op_like((Like) operation);
			} else if (operation.getOperationType() == OperationType.CREATE_SHORT) {
				op_createShort((CreateShort) operation);
			}
		}
	}

}
