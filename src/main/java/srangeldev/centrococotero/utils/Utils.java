package srangeldev.centrococotero.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class Utils {
    private static final SecureRandom random = new SecureRandom();

    public static String generadorId() {
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
