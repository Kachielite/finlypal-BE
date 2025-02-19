package com.derrick.finlypal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is invoked when the user is not authenticated and tries to
     * access a protected resource. It sends a 403 Forbidden response to the
     * user with a JSON body containing the error message.
     *
     * @param request       the request that caused the authentication failure
     * @param response      the response to send back to the user
     * @param authException the authentication exception that caused the failure
     * @throws IOException if the response cannot be sent
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("apiPath", request.getRequestURI());
        errorResponse.put("code", HttpServletResponse.SC_FORBIDDEN);
        errorResponse.put("message", "Access Denied: " + authException.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), errorResponse);

        response.getOutputStream().flush(); // 🚀 Ensure response is sent
    }
}
