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

@Service
public class LogRequestService {

    @Autowired
    private LogRequestRepository repository;

    private static List<String> urlsArmazenar = Arrays.asList(
            "/oauth/token",
            "/api/emular",
            "/api/usuarios/gerencia",
            "/api/usuarios"
    );

    private static List<String> dominiosNaoPermitidos = Collections.singletonList("@XBRAIN.COM.BR");

    @Async
    public void saveAsync(String url,
                          String method,
                          Integer usuario,
                          String email,
                          Integer usuarioEmulador,
                          String ip) {
        save(url, method, usuario, email, usuarioEmulador, ip);
    }

    public LogRequest save(String url,
                           String method,
                           Integer usuario,
                           String email,
                           Integer usuarioEmulador,
                           String ip) {
        if (deveArmazenarLogUrl(url) && deveLogarUsuarioDominio(email)) {
            return save(LogRequest.build(usuario, url, method, usuarioEmulador, ip));
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
