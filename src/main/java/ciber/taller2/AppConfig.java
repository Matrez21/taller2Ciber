package ciber.taller2;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() throws Exception {
        // Configuración para aceptar todos los certificados
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

        // Crear el contexto SSL
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        // Configurar la fábrica de sockets SSL y omitir el chequeo del nombre del host
        var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier((hostname, session) -> true) // Ignorar verificación del nombre del host
                .build();

        // Crear el administrador de conexiones con la fábrica de sockets SSL
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Crear el cliente HTTP con el administrador de conexiones
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // Integrar el cliente HTTP en RestTemplate
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

}
