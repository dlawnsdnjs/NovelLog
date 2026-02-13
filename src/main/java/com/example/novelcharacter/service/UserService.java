package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.User.entity.User;
import com.example.novelcharacter.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p><b>UserService</b>는 사용자(User) 정보를 관리하는 서비스 계층 클래스입니다.</p>
 *
 * <p>사용자 등록, 조회, 수정, 삭제 및 중복 검사 등의 기능을 제공합니다.
 * MyBatis의 {@link UserMapper}를 사용하여 데이터베이스와 연동됩니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *     <li>사용자 정보 조회 (UUID, ID, 이름, 이메일 기준)</li>
 *     <li>아이디 및 이메일 중복 여부 확인</li>
 *     <li>사용자 등록 및 정보 수정</li>
 *     <li>이름 변경 및 중복 이름 검증</li>
 *     <li>최근 로그인 시간 갱신</li>
 *     <li>사용자 삭제</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    /** 사용자 데이터 접근을 위한 MyBatis 매퍼 */
    private final UserMapper userMapper;

    /**
     * <p>UUID를 기반으로 사용자를 조회합니다.</p>
     *
     * @param uuid 사용자 고유 번호(UUID)
     * @return 조회된 {@link User} 객체 (없으면 null)
     */
    public User getUserByUuid(long uuid) {
        return userMapper.getUserByUuid(uuid);
    }

    /**
     * <p>사용자 아이디(ID)를 기반으로 사용자를 조회합니다.</p>
     *
     * @param userId 사용자 로그인 아이디
     * @return 조회된 {@link User} 객체 (없으면 null)
     */
    public User getUserById(String userId) {
        return userMapper.getUserById(userId);
    }

    /**
     * <p>사용자 이름을 기반으로 사용자를 조회합니다.</p>
     *
     * @param userName 사용자 이름
     * @return 조회된 {@link User} 객체 (없으면 null)
     */
    public User getUserByName(String userName) {
        return userMapper.getUserByName(userName);
    }

    /**
     * <p>입력된 사용자 아이디가 이미 존재하는지 여부를 확인합니다.</p>
     *
     * @param userId 중복 여부를 확인할 사용자 아이디
     * @return 존재하면 true, 없으면 false
     */
    public boolean isExistByUserId(String userId) {
        User user = getUserById(userId);
        return user != null;
    }

    /**
     * <p>이메일을 기반으로 사용자를 조회합니다.</p>
     *
     * @param email 사용자 이메일 주소
     * @return 조회된 {@link User} 객체 (없으면 null)
     */
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /**
     * <p>입력된 이메일이 이미 존재하는지 여부를 확인합니다.</p>
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @return 존재하면 true, 없으면 false
     */
    public boolean isExistByEmail(String email) {
        User user = findByEmail(email);
        return user != null;
    }

    /**
     * <p>새로운 사용자를 데이터베이스에 등록합니다.</p>
     *
     * @param user 등록할 사용자 정보 DTO
     */
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    /**
     * <p>기존 사용자 정보를 수정합니다.</p>
     *
     * @param user 수정할 사용자 정보 DTO
     */
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public void updatePassword(String userId, String newPassword){
        User user = userMapper.getUserById(userId);
        user.setPassword(newPassword);
        updateUser(user);
    }

    public boolean checkDuplicateName(String userName) {
        return getUserByName(userName) != null;
    }


    /**
     * <p>사용자 이름을 변경합니다.</p>
     * <p>이미 존재하는 이름일 경우 {@link DuplicateMemberException} 예외를 발생시킵니다.</p>
     *
     * @param userName 새로 설정할 사용자 이름
     * @param uuid     사용자 UUID
     * @throws Exception 이름이 중복될 경우 발생
     */
    public void updateUserName(String userName, long uuid) throws DuplicateMemberException {
        User user = getUserByUuid(uuid);
        if (getUserByName(userName) != null) {
            throw new DuplicateMemberException("중복된 이름입니다");
        }
        user.setUserName(userName);
        updateUser(user);
    }

    /**
     * <p>사용자의 마지막 로그인 시간을 현재 날짜로 갱신합니다.</p>
     *
     * @param user 대상 사용자 정보 DTO
     */
    public void updateLastLoginTime(User user) {
        user.setLastLoginDate(LocalDate.now());
        updateUser(user);
    }

    /**
     * <p>사용자 정보를 삭제합니다.</p>
     *
     * @param user 삭제할 사용자 정보 DTO
     */
    public void deleteUser(User user) {
        userMapper.deleteUser(user);
    }

    public void deleteUser(long uuid){
        deleteUser(getUserByUuid(uuid));
    }
}
