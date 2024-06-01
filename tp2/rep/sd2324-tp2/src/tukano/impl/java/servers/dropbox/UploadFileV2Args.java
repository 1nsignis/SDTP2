package tukano.impl.java.servers.dropbox;

public record UploadFileV2Args(
		boolean autorename,
		boolean mute,
		boolean strict_conflict,
		String path,
		String mode
) {}
