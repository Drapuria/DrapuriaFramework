//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.drapuria.framework.util;

public class LongHash {
    public LongHash() {
    }

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - -2147483648L;
    }

    public static int msw(long l) {
        return (int)(l >> 32);
    }

    public static int lsw(long l) {
        return (int)(l & -1L) + Integer.MIN_VALUE;
    }
}
