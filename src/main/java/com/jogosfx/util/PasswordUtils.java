package com.jogosfx.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtils {

    private static final int WORK_FACTOR = 12;

    private PasswordUtils() {}

    /**
     * Gera o hash BCrypt de uma senha em texto puro.
     *
     * @param plainPassword senha sem hash
     * @return hash BCrypt pronto para armazenar no banco
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifica se uma senha em texto puro corresponde a um hash BCrypt armazenado.
     *
     * @param plainPassword  senha informada pelo usuário
     * @param hashedPassword hash armazenado no banco
     * @return {@code true} se a senha for correta
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
