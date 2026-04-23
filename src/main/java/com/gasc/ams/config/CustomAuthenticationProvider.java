package com.gasc.ams.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (authentication.getPrincipal() == null) ? "" : authentication.getName();
        String presentedPassword = (authentication.getCredentials() == null) ? "" : authentication.getCredentials().toString();
        logger.debug("CustomAuthenticationProvider authenticating user={}", username);

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            logger.warn("UserDetailsService returned null for {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(presentedPassword, user.getPassword())) {
            logger.warn("Bad credentials for user={}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        logger.info("Authentication successful for user={}", username);
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
