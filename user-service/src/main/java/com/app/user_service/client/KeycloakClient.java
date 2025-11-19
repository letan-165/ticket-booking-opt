package com.app.user_service.client;

import com.app.user_service.model.dto.request.LoginRequest;
import com.app.user_service.model.dto.request.UpdateUserRequest;
import com.app.user_service.model.dto.response.LoginResponse;
import com.app.user_service.model.keycloak.CreateUser;
import com.app.user_service.model.keycloak.RoleKeycloak;
import com.app.user_service.model.keycloak.UserInfoKeyCloak;
import com.app.user_service.model.keycloak.UserKeycloak;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakClient {
    final WebClient.Builder webClientBuilder;

    @Value("${keycloak.auth-server-url}")
    String keycloakUrl;

    @Value("${keycloak.realm}")
    String realm;

    @Value("${keycloak.credentials.id}")
    String clientId;

    @Value("${keycloak.credentials.secret}")
    String clientSecret;

    public LoginResponse login(LoginRequest loginRequest) {
        return webClientBuilder.build()
                .post()
                .uri(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("scope", "openid profile email")
                        .with("username", loginRequest.getUsername())
                        .with("password", loginRequest.getPassword()))
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();
    }

    public LoginResponse clientCredentialsLogin() {
        return webClientBuilder.build()
                .post()
                .uri(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("scope", "openid profile email"))
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();
    }

    public String createUser(String token, CreateUser payload) {
        ResponseEntity<Void> response = webClientBuilder.build()
                .post()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toEntity(Void.class)
                .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }
        return null;
    }

    public void assignRole(String token, String userId, List<RoleKeycloak> roles) {
        webClientBuilder.build()
                .post()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roles)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<UserKeycloak> getUsers(String token, String username, int first, int max) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(keycloakUrl + "/admin/realms/" + realm + "/users")
                .queryParam("first", first)
                .queryParam("max", max);

        if (username != null) {
            uriBuilder.queryParam("username", username);
        }

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder.toUriString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserKeycloak>>() {
                })
                .block();
    }

    public UserInfoKeyCloak userInfo(String token) {
        return webClientBuilder.build()
                .get()
                .uri(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(UserInfoKeyCloak.class)
                .block();
    }

    public void update(String token, String userID, UpdateUserRequest request) {
        webClientBuilder.build()
                .put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + userID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }


}

