package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Token {

	private static final int TOKEN_EXPIRE = 10000; // Token expires in 10 seconds
    private static final String DELIMITER = "_";
    private static String val;
    static MessageDigest md;

	static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
	
	public static void set(String token) {
		val = token;
	}
	
	public static String get() {
		return val;
		
	}
	
	public static boolean matches(String token) {
		return (val != null) && (token != null) &&  val.equals( token );
	}

	public static String generate() {
        if (val == null) {
            throw new IllegalStateException("Value is not set");
        }

        synchronized (Token.class) {
            md.reset();
            long time = System.currentTimeMillis() + TOKEN_EXPIRE;
            md.update((byte) (time >> 24));
            md.update((byte) (time >> 16));
            md.update((byte) (time >> 8));
            md.update((byte) (time));
            md.update(val.getBytes());

            String tokenPart = String.format("%016X", new BigInteger(1, md.digest()));
            return tokenPart + DELIMITER + time;
        }
    }


	public static boolean isValid(String token) {
        if (token == null || val == null) {
            return false;
        }

        String[] parts = token.split(DELIMITER);
        if (parts.length != 2) {
            return false;
        }

        String tokenPart = parts[0];
        long time;

        try {
            time = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        if (time < System.currentTimeMillis()) {
            return false;
        }

        synchronized (Token.class) {
            md.reset();
            md.update((byte) (time >> 24));
            md.update((byte) (time >> 16));
            md.update((byte) (time >> 8));
            md.update((byte) (time));
            md.update(val.getBytes());

            String expectedTokenPart = String.format("%016X", new BigInteger(1, md.digest()));
            return expectedTokenPart.equals(tokenPart);
        }
    }

    /* 
	public static void main(String[] args) throws Exception { 

		Token.set("1234");
        String token = Token.generate();
        System.out.println("Generated Token: " + token);

        boolean isValid = Token.isValid(token);
        System.out.println("Is Token Valid? " + isValid);

        Thread.sleep(11000);
        isValid = Token.isValid(token);
        System.out.println("Is Token Valid after expiration? " + isValid);

	}
*/
	
}
