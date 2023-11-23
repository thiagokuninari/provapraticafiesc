package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestsHelper {

    public static String getAccessToken(MockMvc mvc, String usuario) {
        return "Bearer " + getAccessTokenObject(mvc, usuario).getAccessToken();
    }

    @SneakyThrows
    public static MockHttpServletResponse getTokenResponse(MockMvc mvc, String usuario) {
        return mvc
            .perform(
                post("/oauth/token")
                    .header("Authorization", "Basic "
                        + new String(Base64Utils.encode(("xbrain-app-client:xbrain").getBytes())))
                    .param("username", usuario)
                    .param("password", "123456")
                    .param("grant_type", "password"))
            .andReturn().getResponse();
    }

    public static OAuthToken getAccessTokenObject(MockMvc mvc, String usuario) {
        try {
            var response = getTokenResponse(mvc, usuario);
            return new ObjectMapper()
                .readValue(response.getContentAsByteArray(), OAuthToken.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new OAuthToken();
        }
    }

    public static String getAccessTokenComSenhaInvalida(MockMvc mvc, String usuario) {
        return "Bearer " + getAccessTokenObjectComSenhaInvalida(mvc, usuario).getAccessToken();
    }

    public static OAuthToken getAccessTokenObjectComSenhaInvalida(MockMvc mvc, String usuario) {
        try {
            var response = getTokenResponseComSenhaInvalida(mvc, usuario);
            return new ObjectMapper()
                .readValue(response.getContentAsByteArray(), OAuthToken.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new OAuthToken();
        }
    }

    @SneakyThrows
    public static MockHttpServletResponse getTokenResponseComSenhaInvalida(MockMvc mvc, String usuario) {
        return mvc
            .perform(
                post("/oauth/token")
                    .header("Authorization", "Basic "
                        + new String(Base64Utils.encode(("xbrain-app-client:xbrain").getBytes())))
                    .param("username", usuario)
                    .param("password", "000000")
                    .param("grant_type", "password"))
            .andReturn().getResponse();
    }

    @SneakyThrows
    public static byte[] convertObjectToJsonBytes(Object object) {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsBytes(object);
    }

    public static OAuthToken getAccessTokenClientCredentials(MockMvc mvc, String app) {
        try {
            var response = mvc
                .perform(
                    post("/oauth/token")
                        .header("Authorization", "Basic "
                            + new String(Base64Utils.encode((app).getBytes())))
                        .param("grant_type", "client_credentials"))
                .andReturn().getResponse();

            return new ObjectMapper()
                .readValue(response.getContentAsByteArray(), OAuthToken.class);

        } catch (Exception exception) {
            exception.printStackTrace();
            return new OAuthToken();
        }
    }

    @SneakyThrows
    public static String convertObjectToJsonString(Object object) {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }

    public static MockMultipartFile converterObjectParaMultipart(String nome, Object json) {
        return new MockMultipartFile(nome, null, MediaType.APPLICATION_JSON_VALUE, convertObjectToJsonBytes(json));
    }

    public static MockMultipartFile converterJsonStringParaMultipart(String nome, String json) {
        return new MockMultipartFile(nome, null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
    }
}
