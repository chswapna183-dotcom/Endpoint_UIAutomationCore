package com.chswapna183_dotcom.api.endpoints;

public final class UserEndpoints {
    public static final String USERS = "/users";

    private UserEndpoints() {
    }

    public static String userById(int userId) {
        return USERS + "/" + userId;
    }

    public static String userById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        return USERS + "/" + userId.trim();
    }
}
