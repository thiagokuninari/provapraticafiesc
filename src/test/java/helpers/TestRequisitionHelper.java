package helpers;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestRequisitionHelper {

    @SneakyThrows
    public static void isOk(MockHttpServletRequestBuilder endpoint, MockMvc mvc) {
        mvc.perform(endpoint
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public static void isOk(MockHttpServletRequestBuilder endpoint, MockMvc mvc, Object content) {
        mvc.perform(endpoint
                .content(convertObjectToJsonBytes(content))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public static ResultActions isBadRequest(MockHttpServletRequestBuilder endpoint, MockMvc mvc, Object content) {
        return mvc.perform(endpoint
                .content(convertObjectToJsonBytes(content))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    public static ResultActions isBadRequest(MockHttpServletRequestBuilder endpoint,
                                             MockMvc mvc,
                                             Object request,
                                             ResultMatcher jsonPath) {
        return isBadRequest(endpoint, mvc, request).andExpect(jsonPath);
    }

    @SneakyThrows
    public static void isUnauthorized(MockHttpServletRequestBuilder endpoint, MockMvc mvc) {
        mvc.perform(endpoint
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    public static void isForbidden(MockHttpServletRequestBuilder endpoint, MockMvc mvc) {
        mvc.perform(endpoint
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
}
