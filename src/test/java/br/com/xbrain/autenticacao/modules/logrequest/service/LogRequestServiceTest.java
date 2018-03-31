package br.com.xbrain.autenticacao.modules.logrequest.service;

import br.com.xbrain.autenticacao.modules.logrequest.model.LogRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LogRequestServiceTest {

    @Autowired
    private LogRequestService service;

    @Test
    public void deveGravarOLog() {
        LogRequest res = service.save("/api/usuarios/gerencia", "POST",
                101, OPERACAO_GERENTE_COMERCIAL, 101, "200.0.0.1");
        assertNotNull(res.getId());
        assertNotNull(res.getIp());
        assertNotNull(res.getUrl());
        assertNotNull(res.getUsuario().getId());
        assertNotNull(res.getDataCadastro());
        assertNotNull(res.getUsuarioEmulador());
    }

    @Test
    public void deveNaoGravarOLog() {
        LogRequest res = service.save("/api/usuarios/gerencia", "POST",
                100, ADMIN, 101, "200.0.0.1");
        assertNull(res);
    }

    @Test
    public void deveNaoArmazenarUrlsNaoMapeadas() {
        LogRequest res = service.save("/api/teste", "POST",
                100, ADMIN, 101, "200.0.0.1");
        assertNull(res);
    }
}
