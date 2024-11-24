package ciber.taller2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
public class MicroservicioController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getUserSubInfo")
    public ResponseEntity<String> getUserSubInfo() {
        // URL del endpoint para obtener el token
        String tokenUrl = "https://10.0.1.7:9443/oauth2/token";
        // URL del endpoint para obtener información del usuario
        String userInfoUrl = "https://10.0.1.7:9443/oauth2/userinfo";

        try {
            // **Paso 1: Obtener el Access Token**
            // Crear los headers para obtener el token
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            tokenHeaders.setBasicAuth("AgghErXR81MEvpNs3Tpnkq0sES0a", "5wR3CUtmPoqT2ch9E_7dwiUFhHUa"); // Credenciales del cliente

            // Crear los parámetros del cuerpo para obtener el token
            String tokenBody = UriComponentsBuilder.newInstance()
                    .queryParam("grant_type", "password")
                    .queryParam("username", "admin")
                    .queryParam("password", "admin")
                    .queryParam("scope", "openid")
                    .build()
                    .toUriString()
                    .substring(1); // Remover el "?" inicial

            // Crear la entidad de la solicitud para obtener el token
            HttpEntity<String> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);

            // Realizar la solicitud POST para obtener el token
            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    tokenUrl,
                    org.springframework.http.HttpMethod.POST,
                    tokenRequest,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(tokenResponse.getStatusCode())
                        .body("Error al obtener el token. Código de estado: " + tokenResponse.getStatusCode());
            }

            // Extraer el Access Token de la respuesta
            Map<String, Object> tokenResponseBody = tokenResponse.getBody();
            String accessToken = (String) tokenResponseBody.get("access_token");

            // **Paso 2: Usar el Access Token para Obtener Información del Usuario**
            // Crear los headers para la solicitud a /userinfo
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.set("Authorization", "Bearer " + accessToken);

            // Crear la entidad de la solicitud para /userinfo
            HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

            // Realizar la solicitud GET para obtener la información del usuario
            ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                    userInfoUrl,
                    org.springframework.http.HttpMethod.GET,
                    userInfoRequest,
                    String.class
            );

            if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(userInfoResponse.getStatusCode())
                        .body("Error al obtener la información del usuario. Código de estado: " + userInfoResponse.getStatusCode());
            }

            // Devolver la información del usuario
            return ResponseEntity.ok("Información del usuario: " + userInfoResponse.getBody());

        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al conectar con el WS02 API Manager: " + e.getMessage());
        }
    }
}
