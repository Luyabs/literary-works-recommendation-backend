package edu.shu.abs.common.authentication;

import java.util.UUID;

public class MyToken {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
