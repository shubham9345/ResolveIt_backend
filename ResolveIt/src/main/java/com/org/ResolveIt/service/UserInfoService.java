package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.UserInfo;
import com.org.ResolveIt.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfo AddUser(UserInfo user) {
        UserInfo existingUser = userInfoRepository.findByUsername(user.getUsername());
        UserInfo existingUserByEmail = userInfoRepository.findByEmail(user.getEmail());

        if (existingUser != null || existingUserByEmail!=null) {
            throw new RuntimeException("username or email already exist!!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());
        return userInfoRepository.save(user);
    }

    public UserInfo getUserbyId(Long Id) {
        Optional<UserInfo> UserOptional = userInfoRepository.findById(Id);
        if (UserOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with Id" + Id,"user is not found!! check it once");
        }
        return UserOptional.get();
    }

    public List<UserInfo> getAllUser() {
        return userInfoRepository.findAll();
    }
}
