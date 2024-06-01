package tukano.api.java;

import java.net.URI;
import java.util.function.Function;

/**
 * 
 * Represents the result of an operation, either wrapping a result of the given
 * type,
 * or an error.
 * 
 * @author smd
 *
 * @param <T> type of the result value associated with success
 */
public interface Result<T> {

	/**
	 * 
	 * @author smd
	 *
	 *         Service errors:
	 *         OK - no error, implies a non-null result of type T, except for for
	 *         Void operations
	 *         CONFLICT - something is being created but already exists
	 *         NOT_FOUND - an access occurred to something that does not exist
	 *         INTERNAL_ERROR - something unexpected happened
	 */
	enum ErrorCode {
		OK, CONFLICT, NOT_FOUND, BAD_REQUEST, FORBIDDEN, INTERNAL_ERROR, NOT_IMPLEMENTED, TIMEOUT
	};

	/**
	 * Tests if the result is an error.
	 */
	boolean isOK();

	/**
	 * obtains the payload value of this result
	 * 
	 * @return the value of this result.
	 */
	T value();

	URI redirectURI();

	/**
	 *
	 * obtains the error code of this result
	 * 
	 * @return the error code
	 * 
	 */
	ErrorCode error();

	long version();

	/**
	 * Convenience method for returning non error results of the given type
	 * 
	 * @param Class of value of the result
	 * @return the value of the result
	 */
	static <T> Result<T> ok(T result) {
		return new OkResult<>(result);
	}

	static <T> OkResult<T> ok(URI uriRedirect) {
		return new OkResult<>(uriRedirect);
	}

	/**
	 * Convenience method for returning non error results without a value
	 * 
	 * @return non-error result
	 */

	static <T> Result<T> ok() {
		return new OkResult<>();
	}

	static <T> Result<T> ok(T result, long version) {
		return new OkResult<>(result, version);
	}

	static <T> OkResult<T> ok(URI uriRedirect, long version) {
		return new OkResult<>(uriRedirect, version);
	}

	static <T> OkResult<T> ok(Long version) {
		return new OkResult<>(version);
	}

	/**
	 * Convenience method used to return an error
	 * 
	 * @return
	 */
	static <T> Result<T> error(ErrorCode error) {
		return new ErrorResult<>(error);
	}

	static <T> Result<T> errorOrValue(Result<?> res, T val) {
		if (res.isOK())
			return ok(val);
		else
			return error(res.error());
	}

	static <T> Result<T> errorOrValue(Result<?> res, Result<T> other) {
		if (res.isOK())
			return other;
		else
			return error(res.error());
	}

	static Result<Void> errorOrVoid(Result<?> res, Result<?> other) {
		if (res.isOK())
			return other.isOK() ? ok() : error(other.error());
		else
			return error(res.error());
	}

	static <T, Q> Result<Q> errorOrResult(Result<T> a, Function<T, Result<Q>> b) {
		if (a.isOK())
			return b.apply(a.value());
		else
			return error(a.error());
	}

	static <T, Q> Result<Q> errorOrValue(Result<T> a, Function<T, Q> b) {
		if (a.isOK())
			return ok(b.apply(a.value()));
		else
			return error(a.error());
	}
}

/*
 * 
 */
class OkResult<T> implements Result<T> {

	final T result;

	Long version = 0L;

	URI redirectURI;

	OkResult() {
		this.result = null;
	}

	OkResult(T result) {
		this.result = result;
	}

	OkResult(Long version) {
		this.result = null;
		this.version = version;
	}

	OkResult(URI redirectURI) {
		this.result = null;
		this.redirectURI = redirectURI;
	}

	OkResult(T result, Long version) {
		this.result = result;
		this.version = version;
	}

	OkResult(URI redirectURI, Long version) {
		this.result = null;
		this.redirectURI = redirectURI;
		this.version = version;
	}

	@Override
	public boolean isOK() {
		return true;
	}

	@Override
	public T value() {
		return result;
	}

	@Override
	public URI redirectURI() {
		return redirectURI;
	}

	@Override
	public long version() {
		return version;
	}

	@Override
	public ErrorCode error() {
		return ErrorCode.OK;
	}

	public String toString() {
		return "(OK, " + value() + ")";
	}
}

class ErrorResult<T> implements Result<T> {

	final ErrorCode error;

	ErrorResult(ErrorCode error) {
		this.error = error;
	}

	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public T value() {
		throw new RuntimeException("Attempting to extract the value of an Error: " + error());
	}

	@Override
	public URI redirectURI() {
		throw new RuntimeException("Attempting to extract the redirect of an Error: " + error());
	}

	@Override
	public ErrorCode error() {
		return error;
	}

	@Override
	public long version() {
		return 0;
	}

	public String toString() {
		return "(" + error() + ")";
	}
}
