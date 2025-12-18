package com.org.ResolveIt.security;


import lombok.Getter;
import lombok.Setter;

import static com.org.ResolveIt.utils.ConstantUtil.TOKEN_EXPIRATION_TIME;

@Getter
@Setter
public class JwtResponse {
    private final String jwtToken;
    private final String  expirationTimes;

    public JwtResponse(String jwtToken){
        this.jwtToken = jwtToken;
        this.expirationTimes = ((TOKEN_EXPIRATION_TIME) / (1000 * 60)) + " minutes";

    }
}
