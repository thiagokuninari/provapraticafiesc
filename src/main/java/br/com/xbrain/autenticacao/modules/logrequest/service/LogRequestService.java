package br.com.xbrain.autenticacao.modules.logrequest.service;

import br.com.xbrain.autenticacao.modules.logrequest.model.LogRequest;
import br.com.xbrain.autenticacao.modules.logrequest.repository.LogRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class LogRequestService {

    private static final int TAMANHO_MAXIMO = 255;
    private static List<String> urlsArmazenar = Arrays.asList(
            "/oauth/token",
            "/api/emular",
            "/api/usuarios/gerencia",
            "/api/usuarios"
    );
    private static List<String> dominiosNaoPermitidos = Collections.singletonList("@XBRAIN.COM.BR");
    @Autowired
    private LogRequestRepository repository;

    @Async
    public void saveAsync(String url,
                          String method,
                          String urlParam,
                          String dados,
                          Integer usuario,
                          String email,
                          Integer usuarioEmulador,
                          String ip) {
        save(url, method, urlParam, dados, usuario, email, usuarioEmulador, ip);
    }

    public LogRequest save(String url,
                           String method,
                           String urlParam,
                           String dados,
                           Integer usuario,
                           String email,
                           Integer usuarioEmulador,
                           String ip) {
        if (deveArmazenarLogUrl(url) && deveLogarUsuarioDominio(email)) {
            if (!Objects.isNull(urlParam) && urlParam.length() > TAMANHO_MAXIMO) {
                urlParam = urlParam.substring(0, TAMANHO_MAXIMO);
            }
            return save(LogRequest.build(usuario, url, method, urlParam, dados, usuarioEmulador, ip));
        }
        return null;
    }

    public LogRequest save(LogRequest log) {
        log.setDataCadastro(LocalDateTime.now());
        return repository.save(log);
    }

    private boolean deveArmazenarLogUrl(String url) {
        return urlsArmazenar
                .stream()
                .anyMatch(url::startsWith);
    }

    private boolean deveLogarUsuarioDominio(String emailUsuario) {
        return dominiosNaoPermitidos
                .stream()
                .noneMatch(x -> emailUsuario.toUpperCase().endsWith(x.toUpperCase()));
    }

    public List<LogRequest> findAll() {
        return repository.findAll();
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
