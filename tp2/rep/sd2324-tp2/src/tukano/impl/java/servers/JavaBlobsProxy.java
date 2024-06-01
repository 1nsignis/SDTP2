package tukano.impl.java.servers;

import static tukano.api.java.Result.error;
import static tukano.api.java.Result.ok;
import static tukano.api.java.Result.ErrorCode.FORBIDDEN;

//import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;

import tukano.api.java.Result;
import tukano.impl.api.java.ExtendedBlobs;

import tukano.impl.java.servers.dropbox.DeleteFileV2Args;
import tukano.impl.java.servers.dropbox.DownloadFileV2Args;
import tukano.impl.java.servers.dropbox.UploadFileV2Args;
import utils.Args;
import utils.State;
//import utils.Args;
import utils.Token;

public class JavaBlobsProxy implements ExtendedBlobs {
	// private static final String ADMIN_TOKEN = Args.valueOf("-token", "");

	private static final String ROOT = "/distributed-blobs";

	private static Logger Log = Logger.getLogger(JavaBlobsProxy.class.getName());

	private static final String UPLOAD_FILE_V2_URL = "https://content.dropboxapi.com/2/files/upload";
	private static final String DELETE_FILE_V2_URL = "https://api.dropboxapi.com/2/files/delete_v2";
	private static final String DOWNLOAD_FILE_V2_URL = "https://content.dropboxapi.com/2/files/download";

	private static final int HTTP_SUCCESS = 200;
	private static final int HTTP_NOT_FOUND = 409;

	private static final String CONTENT_TYPE_HDR = "Content-Type";
	private static final String DROPBOX_API_ARG_HDR = "Dropbox-API-Arg";
	private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	private static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";

	private final Gson json;
	private final OAuth20Service service;
	private final OAuth2AccessToken accessToken;

	private static final String apiKey = Args.valueOf("-apiKey", "2vg0b34it0yq8jl");
	private static final String apiSecret = Args.valueOf("-apiSecret", "w0m72ken4uv84zt");
	private static final String accessKey = Args.valueOf("-accessKey",
			"sl.B2Qo7eabiL73IRk7Tfr-quAktw0kpoeYwgHKp-_FdVWdN_LYPL1P4ePzOn7x1sOxZHMMBolaAQG2o7FuMspAUmKKc8F550vR7xg_zJ19I5tfeDu4X1YPxCway0mhF_1VeSIyjCpIdh3y");
	private static final Boolean state = State.get();

	public JavaBlobsProxy() {
		this.json = new Gson();
		this.accessToken = new OAuth2AccessToken(accessKey);
		this.service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);

		if (state)
			ignorePreviousState();

	}

	@Override
	public Result<Void> upload(String blobId, byte[] bytes, String token) {
		if (!Token.isValid(token))
			return error(FORBIDDEN);

		var path = (getBlobPath(blobId) != null) ? getBlobPath(blobId) : ROOT;

		var jsonArgs = json.toJson(new UploadFileV2Args(
				false,
				false,
				false,
				path,
				"overwrite"));

		var uploadFile = new OAuthRequest(Verb.POST, UPLOAD_FILE_V2_URL);
		uploadFile.addHeader(DROPBOX_API_ARG_HDR, jsonArgs);
		uploadFile.addHeader(CONTENT_TYPE_HDR, OCTET_STREAM_CONTENT_TYPE);
		uploadFile.setPayload(bytes);
		Log.info("###uploadfile##:" + uploadFile);

		service.signRequest(accessToken, uploadFile);
		Log.info("###token##:" + accessToken);
		Log.info("-----------------HERE---------------");
		try {
			var response = service.execute(uploadFile);
			Log.info("###response##:" + response);
			if (response.getCode() != HTTP_SUCCESS) {
				Log.info("-----------------HERE1---------------");
				return Result.error(Result.ErrorCode.INTERNAL_ERROR);
			}
		} catch (Exception e) {
			Log.info("-----------------HERE2---------------");
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}

		return ok();
	}

	@Override
	public Result<byte[]> download(String blobId) {
		

		var path = (getBlobPath(blobId) != null) ? getBlobPath(blobId) : ROOT;

		var jsonArgs = json.toJson(new DownloadFileV2Args(path));

		var downloadFile = new OAuthRequest(Verb.POST, DOWNLOAD_FILE_V2_URL);
		downloadFile.addHeader(DROPBOX_API_ARG_HDR, jsonArgs);
		downloadFile.addHeader(CONTENT_TYPE_HDR, OCTET_STREAM_CONTENT_TYPE);

		service.signRequest(accessToken, downloadFile);

		try {
			var response = service.execute(downloadFile);

			if (response.getCode() != HTTP_SUCCESS) {
				if (response.getCode() == HTTP_NOT_FOUND) {
					return Result.error(Result.ErrorCode.NOT_FOUND);
				} else {
					return Result.error(Result.ErrorCode.INTERNAL_ERROR);
				}
			}

			return Result.ok(response.getStream().readAllBytes());
		} catch (Exception e) {
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	@Override
	public Result<Void> downloadToSink(String blobId, Consumer<byte[]> sink) {
		

		var path = (getBlobPath(blobId) != null) ? getBlobPath(blobId) : ROOT;

		var jsonArgs = json.toJson(new DownloadFileV2Args(path));

		var downloadFile = new OAuthRequest(Verb.POST, DOWNLOAD_FILE_V2_URL);
		downloadFile.addHeader(DROPBOX_API_ARG_HDR, jsonArgs);
		downloadFile.addHeader(CONTENT_TYPE_HDR, OCTET_STREAM_CONTENT_TYPE);

		service.signRequest(accessToken, downloadFile);

		try {
			var response = service.execute(downloadFile);

			if (response.getCode() != HTTP_SUCCESS) {
				if (response.getCode() == HTTP_NOT_FOUND) {
					return Result.error(Result.ErrorCode.NOT_FOUND);
				} else {
					return Result.error(Result.ErrorCode.INTERNAL_ERROR);
				}
			}

			return Result.ok();
		} catch (Exception e) {
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	@Override
	public Result<Void> delete(String blobId, String token) {
		if (!Token.isValid(token))
			return error(FORBIDDEN);

		var path = (getBlobPath(blobId) != null) ? getBlobPath(blobId) : ROOT;

		var jsonArgs = json.toJson(new DeleteFileV2Args(path));

		var deleteFile = new OAuthRequest(Verb.POST, DELETE_FILE_V2_URL);
		deleteFile.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
		deleteFile.setPayload(jsonArgs);

		service.signRequest(accessToken, deleteFile);

		try {
			var response = service.execute(deleteFile);

			if (response.getCode() != HTTP_SUCCESS) {
				if (response.getCode() == HTTP_NOT_FOUND) {
					return Result.error(Result.ErrorCode.NOT_FOUND);
				} else {
					return Result.error(Result.ErrorCode.INTERNAL_ERROR);
				}
			}
		} catch (Exception e) {
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
		return ok();
	}

	@Override
	public Result<Void> deleteAllBlobs(String userId, String token) {
		Log.info("deleteAllBlobs: " + userId);

		if (!Token.isValid(token))
			return error(FORBIDDEN);

		String path = ROOT + "/" + userId;

		var jsonArgs = json.toJson(new DeleteFileV2Args(path));

		var deleteFolder = new OAuthRequest(Verb.POST, DELETE_FILE_V2_URL);
		deleteFolder.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
		deleteFolder.setPayload(jsonArgs);

		service.signRequest(accessToken, deleteFolder);

		try {
			var response = service.execute(deleteFolder);

			if (response.getCode() != HTTP_SUCCESS) {
				if (response.getCode() == HTTP_NOT_FOUND) {
					return Result.error(Result.ErrorCode.NOT_FOUND);
				} else {
					return Result.error(Result.ErrorCode.INTERNAL_ERROR);
				}
			}
		} catch (Exception e) {
		}

		return Result.ok();
	}

	private String getBlobPath(String blobId) {
		var parts = blobId.split("-");
		if (parts.length != 2)
			return null;

		return ROOT + "/" + parts[0] + "/" + parts[1];
	}

	private void ignorePreviousState() {
		var jsonArgs = json.toJson(new DeleteFileV2Args(ROOT));

		var deleteFolder = new OAuthRequest(Verb.POST, DELETE_FILE_V2_URL);
		deleteFolder.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
		deleteFolder.setPayload(jsonArgs);

		service.signRequest(accessToken, deleteFolder);

		try {
			service.execute(deleteFolder);
		} catch (Exception e) {
			Log.info("-----------------HERE---------------");
		}
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * // var state = Boolean.parseBoolean(args[0]);
	 * 
	 * var cd = new JavaBlobsProxy();
	 * System.out.println("apikey:" + JavaBlobsProxy.apiKey);
	 * System.out.println("apiSecret:" + JavaBlobsProxy.apiSecret);
	 * System.out.println("accesskey:" + JavaBlobsProxy.accessKey);
	 * var bytes = "exampletowrite".getBytes();
	 * 
	 * var res = cd.upload("blobId", bytes);
	 * System.out.println("######:" + res.value());
	 * 
	 * var res2 = cd.download("blobId");
	 * String s = new String(res2.value(), StandardCharsets.UTF_8);
	 * System.out.println("######:" + s);
	 * 
	 * var res3 = cd.delete(s, s)
	 * }
	 */
}
