package com.example.novelcharacter.domain.OAuth;

public interface OAuth2Response {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
