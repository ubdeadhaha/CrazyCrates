package com.badbones69.crazycrates.support.libs;

import com.badbones69.crazycrates.api.CrazyManager;

/**
 * @Author Badbones69
 */
public enum ServerProtocol {

    TOO_OLD(1),
    v1_13_R2(1132),
    v1_14_R4(1141),
    v1_15_R2(1152),
    v1_16_5(1165),
    v1_17_1(1171),
    v1_18_1(1181),
    v1_19(119),
    TOO_NEW(-2);

    private static ServerProtocol currentProtocol;
    private static ServerProtocol latest;

    private final int versionProtocol;

    private static final CrazyManager crazyManager = CrazyManager.getInstance();

    ServerProtocol(int versionProtocol) {
        this.versionProtocol = versionProtocol;
    }

    public static ServerProtocol getCurrentProtocol() {

        String serVer = crazyManager.getPlugin().getServer().getClass().getPackage().getName();

        int serProt = Integer.parseInt(
                serVer.substring(
                        serVer.lastIndexOf('.') + 1
                ).replace("_", "").replace("R", "").replace("v", "")
        );

        for (ServerProtocol protocol : values()) {
            if (protocol.versionProtocol == serProt) {
                currentProtocol = protocol;
                break;
            }
        }

        if (currentProtocol == null) currentProtocol = ServerProtocol.TOO_NEW;

        return currentProtocol;
    }

    public static boolean isLegacy() {
        return isOlder(ServerProtocol.v1_13_R2);
    }

    public static ServerProtocol getLatestProtocol() {

        if (latest != null) return latest;

        ServerProtocol old = ServerProtocol.TOO_OLD;

        for (ServerProtocol protocol : values()) {
            if (protocol.compare(old) == 1) {
                old = protocol;
            }
        }

        return old;
    }

    public static boolean isAtLeast(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        int proto = currentProtocol.versionProtocol;
        return proto >= protocol.versionProtocol || proto == -2;
    }

    public static boolean isSame(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        return currentProtocol.versionProtocol == protocol.versionProtocol;
    }

    public static boolean isOlder(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        int proto = currentProtocol.versionProtocol;
        return proto < protocol.versionProtocol || proto == -1;
    }

    public int compare(ServerProtocol protocol) {
        int result = -1;
        int current = versionProtocol;
        int check = protocol.versionProtocol;

        if (current > check || check == -2) {
            result = 1;
        } else if (current == check) {
            result = 0;
        }

        return result;
    }

}