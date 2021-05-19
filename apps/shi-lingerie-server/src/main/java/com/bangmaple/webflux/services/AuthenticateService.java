package com.bangmaple.webflux.services;

import com.bangmaple.webflux.models.AuthenticationRequest;
import com.bangmaple.webflux.models.AuthenticationResponse;
import com.bangmaple.webflux.models.SignUpModel;
import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.repositories.ReactiveUsersRepository;
import com.bangmaple.webflux.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final ReactiveUsersRepository repo;
    private final AuthenticationUtil authenticationUtil;

    public Mono<Map<String, Object>> signin(Mono<AuthenticationRequest> requestedAuthenticatingUser) {
        return requestedAuthenticatingUser.flatMap(authenticationUtil::getAuthentication)
                .flatMap((authenticatedUser -> authenticationUtil
                  .getUsernameFromAuthenticatedUser(authenticatedUser)
                  .flatMap(userDetail -> authenticationUtil
                    .getResponseAuthenticatedObject(authenticatedUser, userDetail))));
    }

    public Mono<Users> signup(Mono<SignUpModel> user) {
        return user.flatMap(u -> repo.signUpAUser(u.getUsername(),
          authenticationUtil.getEncodedPassword(u.getPassword()), u.getFullname()));
    }

    public Mono<AuthenticationResponse> setSignedInStatus(AuthenticationResponse res) {
        return repo.changeToSignedInStatus(res.getUsername()).map(flag -> res);
    }

    public Mono<AuthenticationResponse> setSignedOutStatus(AuthenticationResponse res) {
         return repo.changeToSignedOutStatus(res.getUsername()).map(flag -> res);
    }
}
