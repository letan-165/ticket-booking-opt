package com.app.event_service.repository.client;

import com.app.ticket_common_library.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-client", url = "${app.service.user}" + "/auth")
public interface AuthClient {
    @GetMapping("/checkUserToken/userID")
    public ApiResponse<Boolean> checkUserToken(@PathVariable String userID);
}//Thiết token trong header
