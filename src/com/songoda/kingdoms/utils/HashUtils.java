package com.songoda.kingdoms.utils;

import java.util.HashSet;
import java.util.Set;

public class HashUtils {
    public static String nonce = "%%__NONCE__%%";
    public static <T> boolean setEqualsIgnoreOrder(Set<String> list1, Set<String> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
