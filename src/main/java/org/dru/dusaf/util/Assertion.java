package org.dru.dusaf.util;

@SuppressWarnings("AssertWithSideEffects")
public final class Assertion {
    private static final boolean enabled;

    public static boolean isEnabled() {
        return enabled;
    }

    static {
        boolean a = false;
        assert a = true;
        enabled = a;
    }

    private Assertion() {
    }
}
