package tukano.impl.grpc.servers;

import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
//import tukano.api.java.Blobs;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.*;
import tukano.impl.api.java.ExtendedBlobs;
import tukano.impl.java.servers.JavaBlobs;
import utils.Token;


public class GrpcBlobsServerStub extends AbstractGrpcStub implements BlobsGrpc.AsyncService {
	private static Logger Log = Logger.getLogger(GrpcBlobsServerStub.class.getName());
	ExtendedBlobs impl = new JavaBlobs();

	@Override
	public ServerServiceDefinition bindService() {
		return BlobsGrpc.bindService(this);
	}

	@Override
	public void upload(UploadArgs request, StreamObserver<UploadResult> responseObserver) {
		ParsedResult result = parse(request.getBlobId());
		var res = impl.upload(result.getBlobId(), request.getData().toByteArray(), result.getToken());
		if (!res.isOK())
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(UploadResult.newBuilder().build());
			responseObserver.onCompleted();
		}
	}

	@Override
	public void download(DownloadArgs request, StreamObserver<DownloadResult> responseObserver) {
		Log.info("requeststubbbbbbbbb"+ request.getBlobId());
		ParsedResult result = parse(request.getBlobId());
		Log.info("resultstubbbbbbbbb"+ result.getBlobId());
		 
		var res = impl.downloadToSink(result.getBlobId(), (data) -> {
			responseObserver.onNext(DownloadResult.newBuilder().setChunk(ByteString.copyFrom(data)).build());
		});
		
		//var res =  impl.download(result.getBlobId(), result.getToken());
		if (res.isOK())
			responseObserver.onCompleted();
		else
			responseObserver.onError(errorCodeToStatus(res.error()));
	}
	
	@Override
	public void delete(DeleteArgs request, StreamObserver<DeleteResult> responseObserver) {
		var res = impl.delete(request.getBlobId(), request.getToken());
		if (res.isOK()) {
			responseObserver.onNext(DeleteResult.newBuilder().build());
			responseObserver.onCompleted();
		}
		else
			responseObserver.onError(errorCodeToStatus(res.error()));

    }

	@Override
	public void deleteAllBlobs(DeleteAllBlobsArgs request, StreamObserver<DeleteAllBlobsResult> responseObserver) {
		var res = impl.deleteAllBlobs(request.getUserId(), request.getToken());
		if (res.isOK()) {
			responseObserver.onNext(DeleteAllBlobsResult.newBuilder().build());
			responseObserver.onCompleted();
		}
		else
			responseObserver.onError(errorCodeToStatus(res.error()));

    }

	private static ParsedResult parse(String str) {
       
        int tokenIndex = str.indexOf("?token=");
        
        if (tokenIndex == -1) {
            return new ParsedResult(str, Token.generate()); 
        }

        String blobId = str.substring(0, tokenIndex);
        String token = str.substring(tokenIndex + 7); 
		

        return new ParsedResult(blobId, token);
    }

	static class ParsedResult {
        private final String blobId;
        private final String token;

        public ParsedResult(String blobId, String token) {
            this.blobId = blobId;
            this.token = token;
        }

        public String getBlobId() {
            return blobId;
        }

        public String getToken() {
            return token;
        }
    }
}
