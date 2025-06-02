package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.login.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ErpLoginService {

    String url="http://erpnext.localhost:8000/api/method/login";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ErpLoginService() {
        this.restTemplate = new RestTemplate();
    }

    public LoginResponseHeaders loginToErp(LoginRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                this.url,
                HttpMethod.POST,
                entity,
                String.class
        );

        // Parse le body JSON
        try {
            JsonNode body = objectMapper.readTree(response.getBody());
            String message = body.path("message").asText();

            if (!"Logged in".equalsIgnoreCase(message)) {
                throw new Exception("Échec de la connexion ERPNext : " + message);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        HttpHeaders responseHeaders = response.getHeaders();

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        String sid = null, systemUser = null, fullName = null, userId = null;

        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("sid=")) {
                    sid = extractCookieValue(cookie);
                } else if (cookie.startsWith("system_user=")) {
                    systemUser = extractCookieValue(cookie);
                } else if (cookie.startsWith("full_name=")) {
                    fullName = extractCookieValue(cookie);
                } else if (cookie.startsWith("user_id=")) {
                    userId = extractCookieValue(cookie);
                }
            }
        }

        return new LoginResponseHeaders(sid, systemUser, fullName, userId);
    }

    private String extractCookieValue(String cookie) {
        int endIndex = cookie.indexOf(';');
        if (endIndex != -1) {
            return cookie.substring(cookie.indexOf('=') + 1, endIndex);
        }
        return cookie.substring(cookie.indexOf('=') + 1);
    }
}