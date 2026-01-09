package com.example.novelcharacter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class RefreshService {

    private final RedisService redisService;


    public void addRefresh(long uuid, String randomId, String refresh ,long expiredMS) {
        redisService.setValues("RT:"+uuid+":"+randomId, refresh, Duration.ofMillis(expiredMS));
    }

    public Boolean checkRefresh(long uuid, String randomId, String refresh) {
        String value = redisService.getValues("RT:"+uuid+":"+randomId);
        return value != null && value.equals(refresh);
    }

    // 다중 로그인 허용하지만 refresh 토큰 삭제 시 같은 유저의 모든 refresh 토큰 삭제
    public void deleteByRefresh(long uuid) {
        redisService.deleteKeysByPattern("RT:"+uuid+":*");
    }
}
