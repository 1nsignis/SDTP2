package tukano.impl.rest.servers;

import jakarta.inject.Singleton;
import tukano.impl.api.java.ExtendedBlobs;
import tukano.impl.api.rest.RestExtendedBlobs;
import tukano.impl.java.servers.JavaBlobsProxy;

@Singleton
public class BlobsProxyResource extends RestResource implements RestExtendedBlobs {

    final ExtendedBlobs impl;

    public BlobsProxyResource() {
        this.impl = new JavaBlobsProxy();
    }

    @Override
    public void upload(String blobId, byte[] bytes, String token) {
        super.resultOrThrow(impl.upload(blobId, bytes, token));
    }

    @Override
    public byte[] download(String blobId, String token) {
        return super.resultOrThrow(impl.download(blobId, token));
    }

    @Override
    public void deleteAllBlobs(String userId, String password) {
        super.resultOrThrow(impl.deleteAllBlobs(userId, password));
    }

    @Override
    public void delete(String blobId, String token) {
        super.resultOrThrow(impl.delete(blobId, token));
    }

}
