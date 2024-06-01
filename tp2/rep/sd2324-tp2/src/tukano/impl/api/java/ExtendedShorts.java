package tukano.impl.api.java;

import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.java.servers.operations.Operation;
import java.util.List;

public interface ExtendedShorts extends Shorts {

	Result<Void> deleteAllShorts( String userId, String password, String token );
	Result<Void> primaryOperation(Long version, String operation, String opType, String token);
	Result<List<Operation>> getOperations(Long version, String token);
}
