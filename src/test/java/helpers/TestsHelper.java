package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class TestsHelper {

    public static String getAccessToken(MockMvc mvc, String usuario) throws Exception {
        return "Bearer " + getAccessTokenObject(mvc, usuario).accessToken;
    }

    public static OAuthToken getAccessTokenObject(MockMvc mvc, String usuario) {
        try {
            MockHttpServletResponse response = mvc
                    .perform(
                            post("/oauth/token")
                                    .header("Authorization", "Basic "
                                            + new String(Base64Utils.encode(("xbrain-app-client:xbrain").getBytes())))
                                    .param("username", usuario)
                                    .param("password", "123456")
                                    .param("grant_type", "password"))
                    .andReturn().getResponse();

            return new ObjectMapper()
                    .readValue(response.getContentAsByteArray(), OAuthToken.class);

        } catch (Exception exception) {
            exception.printStackTrace();
            return new OAuthToken();
        }
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule( new JavaTimeModule());
        return mapper.writeValueAsBytes(object);
    }
}
