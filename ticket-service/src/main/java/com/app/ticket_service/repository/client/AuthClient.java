package com.app.ticket_service.repository.client;

import com.app.ticket_common_library.config.security.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-client", url = "${app.service.user}" + "/keycloak/auth", configuration = FeignConfig.class)
public interface AuthClient {
    @GetMapping("/token/{userID}")
    Boolean checkUserToken(@PathVariable String userID);
}
