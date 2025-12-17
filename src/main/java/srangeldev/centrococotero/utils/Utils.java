package srangeldev.centrococotero.utils;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class Utils implements IdentifierGenerator {

    private static final String REGEX = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";
    private static final int LONGITUD = 11;

    public Serializable generateYoutubeId() {
        StringBuilder sb = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            sb.append(REGEX.charAt(new Random().nextInt(REGEX.length())));
        }
        return sb.toString();
    }

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return generateYoutubeId();
    }
}


