package com.example.novelcharacter.component;

public class MaskUtil {
    public static String maskUserId(String userId) {
        if (userId == null || userId.length() <= 2) {
            return "*".repeat(userId.length());
        }

        if (userId.length() <= 4) {
            return userId.charAt(0) + "***";
        }

        String start = userId.substring(0, 2);
        String end = userId.substring(userId.length() - 2);

        return start + "****" + end;
    }
}