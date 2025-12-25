package com.org.ResolveIt.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.org.ResolveIt.model.GoogleLoginRequest;
import com.org.ResolveIt.model.UserInfo;
import com.org.ResolveIt.repository.UserInfoRepository;
import com.org.ResolveIt.security.JwtUtil;
import com.org.ResolveIt.service.GoogleTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class GoogleAuthController {

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private JwtUtil jwtService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {

        GoogleIdToken.Payload payload =
                googleTokenVerifier.verify(request.getToken());

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        UserInfo user = userRepository.findByEmail(email);

        if (user == null) {
            user = new UserInfo();
            user.setEmail(email);
            user.setName(name);
            user.setProvider("GOOGLE");
            user = userRepository.save(user);
        }

        String jwt = jwtService.generateToken(user.getEmail(), user.getId());

        return ResponseEntity.ok(
                Map.of(
                        "jwtToken", jwt,
                        "roles", user.getRoles()
                )
        );
    }
}
