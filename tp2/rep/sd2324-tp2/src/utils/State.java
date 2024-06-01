package utils;

public class State {
    private static Boolean state;

    public static void set(Boolean st) {
        state = st;
    }

    public static Boolean get() {
        return state;

    }

}
