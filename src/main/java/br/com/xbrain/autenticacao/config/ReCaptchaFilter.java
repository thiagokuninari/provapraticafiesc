package br.com.xbrain.autenticacao.config;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReCaptchaFilter extends GenericFilterBean {

    private static final String RECAPTCHA_SECRET = "6Le_61EUAAAAAIPOqay7hFxkcmQliYJeIz-O4XDF";
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String RECAPTCHA_RESPONSE_PARAM = "g-recaptcha-response";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (servletRequest.getParameter(RECAPTCHA_RESPONSE_PARAM) != null && servletRequest.getRemoteAddr() != null) {

            PostMethod method = new PostMethod(RECAPTCHA_URL);
            method.addParameter("secret", RECAPTCHA_SECRET);
            method.addParameter("response", servletRequest.getParameter(RECAPTCHA_RESPONSE_PARAM));
            method.addParameter("remoteip", servletRequest.getRemoteAddr());

            HttpClient client = new HttpClient();
            client.executeMethod(method);
            BufferedReader br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            String readLine;
            StringBuffer response = new StringBuffer();

            while ((readLine = br.readLine()) != null) {
                response.append(readLine);
            }

            JSONObject jsonObject = new JSONObject(response.toString());
            boolean success = jsonObject.getBoolean("success");

            if (success) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                sendErrorValidation(servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void sendErrorValidation(ServletResponse servletResponse) throws IOException {
        ((HttpServletResponse) servletResponse)
                .sendError(HttpStatus.NOT_ACCEPTABLE.value(), "Acesso negado. Resolva o CAPTCHA.");
    }
}


