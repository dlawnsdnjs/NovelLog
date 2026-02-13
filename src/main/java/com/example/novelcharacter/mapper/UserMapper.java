package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.User.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public User getUserByUuid(long uuid);
    public User getUserById(String userId);
    public User getUserByName(String userName);
    public User findByEmail(String email);
    public void insertUser(User user);
    public void updateUser(User user);
    public void deleteUser(User user);
}
