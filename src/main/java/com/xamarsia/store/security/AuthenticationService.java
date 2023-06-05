package com.xamarsia.store.security;

import com.xamarsia.store.TokenType;
import com.xamarsia.store.dto.auth.AuthenticationRequest;
import com.xamarsia.store.dto.auth.AuthenticationResponse;
import com.xamarsia.store.dto.auth.RegisterRequest;
import com.xamarsia.store.entity.Role;
import com.xamarsia.store.entity.User;
import com.xamarsia.store.entity.Token;
import com.xamarsia.store.repository.TokenRepository;
import com.xamarsia.store.service.OrderService;
import com.xamarsia.store.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final OrderService orderService;

    public AuthenticationResponse register(RegisterRequest request) {

        boolean emailUsed = userService.isEmailUsed(request.getEmail());

        if (emailUsed) {
            throw new IllegalStateException("There is already an account registered with that email");
        } else if(!EmailValidation.patternMatches(request.getEmail())) {
            throw new IllegalStateException("Invalid email address");
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // TODO add realisation for different role
                .build();

        var savedUser = userService.save(user);
        orderService.createCart(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userService.getByEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse logout( ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();

        revokeAllUserTokens(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
