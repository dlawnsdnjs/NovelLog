package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.OAuth.GoogleResponse;
import com.example.novelcharacter.dto.OAuth.NaverResponse;
import com.example.novelcharacter.dto.OAuth.OAuth2Response;
import com.example.novelcharacter.dto.User.CustomOAuth2User;
import com.example.novelcharacter.dto.User.UserDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if(registrationId.equals("naver")){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if(registrationId.equals("google")){
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else{
            return null;
        }

        String username = oAuth2Response.getProviderId()+"@"+oAuth2Response.getProvider()+".com";
        UserDTO existData = userService.getUserById(username);

        if(existData == null){
            existData = registerNewUser(oAuth2Response.getProvider(), oAuth2Response.getProviderId());
        }
        return new CustomOAuth2User(existData);
    }

    private UserDTO registerNewUser(String provider, String providerId) {
        boolean saved = false;
        UserDTO newUser = new UserDTO();

        while (!saved) {
            try {
                String randomNickname = "User_" + UUID.randomUUID().toString().substring(0, 6);

                newUser.setUserId(providerId+"@"+provider+".com");
                newUser.setUserName(randomNickname);
                newUser.setPassword("OAuthUser");
                newUser.setEmail("OAuthUser@"+provider+".com")  ;
                newUser.setRole("ROLE_USER");
                newUser.setLastLoginDate(LocalDate.now());
                userService.insertUser(newUser);
                saved = true;
            } catch (DataIntegrityViolationException e) {
                // UNIQUE(username) 충돌 시 재시도
//                e.printStackTrace();
            }
        }

        return newUser;
    }
}
