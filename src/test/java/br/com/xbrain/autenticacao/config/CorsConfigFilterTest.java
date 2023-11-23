package br.com.xbrain.autenticacao.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService.HEADER_USUARIO_EMULADOR;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CorsConfigFilterTest {

    private static final String ENDPOINT = "/api/feriado";
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate testTemplate;
    @LocalServerPort
    private int port;
    @Value("#{'${app-config.url-origin-cors}'.split(',')}")
    private List<String> urlsOriginCors;

    @Test
    public void doFilter_deveRetornarCabecalho_seOriginForVazia() {
        var headers = new HttpHeaders();
        headers.set("Origin", "");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getHeaders().getAccessControlAllowCredentials()).isEqualTo(true);

        assertThat(response.getHeaders().getAccessControlAllowMethods().toString())
            .isEqualTo("[POST, PUT, GET, OPTIONS, DELETE]");

        assertThat(response.getHeaders().getAccessControlMaxAge()).isEqualTo(3600L);

        assertThat(response.getHeaders().getAccessControlAllowHeaders().toString())
            .isEqualTo("[Authorization, Origin, X-Requested-With, Content-Type, Accept, X-Usuario-Canal, "
                + HEADER_USUARIO_EMULADOR + "]");

        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarCabecalho_seAccessControlAllowMethodsForCorrespondenteComOsDeclaradosNaClasse() {
        var headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getHeaders().getAccessControlAllowMethods().size()).isEqualTo(5);
        assertThat(response.getHeaders().getAccessControlAllowMethods().toString())
            .isEqualTo("[POST, PUT, GET, OPTIONS, DELETE]");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarUnauthorized_seOMetodosHttpOptionNaoForUsado() {

        var headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        headers.setAccessControlAllowMethods(List.of(HttpMethod.PATCH));
        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void doFilter_deveRetornarCabecalho_seAccessControlMaxAgeForCorrespondenteComODeclaradoNaClasse() {
        var headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getHeaders().getAccessControlMaxAge()).isEqualTo(3600L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarCabecalho_seAccessControlAllowHeadersForCorrespondenteComOsDeclaradosNaClasse() {
        var headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getHeaders().getAccessControlAllowHeaders().toString())
            .isEqualTo("[Authorization, Origin, X-Requested-With, Content-Type, Accept, X-Usuario-Canal, "
                + HEADER_USUARIO_EMULADOR + "]");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarCabecalho_seAccessControlAllowOriginsForCorrespondenteComOsDeclaradosNaClasse() {
        var headers = new HttpHeaders();
        headers.set("Origin", "http://localhost");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);
        urlsOriginCors.forEach(url -> {
            assertThat(response.getHeaders().getAccessControlAllowOrigin().toString().contains(url));
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarCabecalho_seAccessControlAllowCredentialsForCorrespondenteComODeclaradosNaClasse() {
        var headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + ENDPOINT,
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getHeaders().getAccessControlAllowCredentials()).isEqualTo(true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void doFilter_deveRetornarNotFound_seRotaNaoEncontrado() {
        var headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testTemplate
            .exchange("http://localhost:" + port + "nao/existe/rota/valida",
                HttpMethod.OPTIONS, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void doFilter_deveRetornarNotFound_seRotaNaoEncontradovalo() {
        var lista = List.of("http://localhost");
        assertThat(lista).isEqualTo(urlsOriginCors);
    }
}
