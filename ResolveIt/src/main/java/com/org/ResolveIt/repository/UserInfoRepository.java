package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUsername(String Username);
    UserInfo findByEmail(String email);
}
