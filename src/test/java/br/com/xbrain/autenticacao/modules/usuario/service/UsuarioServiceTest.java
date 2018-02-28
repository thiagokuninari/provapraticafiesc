package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioCadastroMqSender;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("oracle")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql"})
public class UsuarioServiceTest {

    @MockBean
    private UsuarioCadastroMqSender sender;

    @Autowired
    private UsuarioService service;

    @Test
    public void deveSalvarUsuarioEEnviarParaFila() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        service.saveFromQueue(usuarioMqRequest);
        UsuarioDto usuarioDto = service.findByEmail(usuarioMqRequest.getEmail());
        Assert.assertEquals(usuarioDto.getCpf(), usuarioMqRequest.getCpf());
        verify(sender, times(1)).sendSuccess(any());
    }

    @Test
    public void deveNaoSalvarUsuarioEEnviarParaFilaDeFalha() {
        try {
            service.saveFromQueue(new UsuarioMqRequest());
        } catch (Exception exception) {
            verify(sender, times(1)).sendWithFailure(any());
        }
    }

    @Test
    public void deveBuscarSuperioresDoUsuario() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = getFiltroHierarquia();
        List<UsuarioResponse> usuariosResponse = service.getUsuariosSuperiores(getFiltroHierarquia());
        Assert.assertEquals(1, usuariosResponse.size());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoCargo(), usuarioFiltrosHierarquia.getCodigoCargo());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoDepartamento(), usuarioFiltrosHierarquia.getCodigoDepartamento());
        Assert.assertEquals(usuariosResponse.get(0).getCodigoNivel(), usuarioFiltrosHierarquia.getCodigoNivel());
    }

    private UsuarioMqRequest umUsuario() {
        UsuarioMqRequest usuarioMqRequest = new UsuarioMqRequest();
        usuarioMqRequest.setNome("TESTE NOVO USUARIO PARCEIROS ONLINE");
        usuarioMqRequest.setEmail("novousuarioparceirosonline@xbrain.com.br");
        usuarioMqRequest.setCpf("76696512616");
        usuarioMqRequest.setUnidadesNegocio(Arrays.asList(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS));
        usuarioMqRequest.setNivel(CodigoNivel.AGENTE_AUTORIZADO);
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR);
        usuarioMqRequest.setDepartamento(CodigoDepartamento.AGENTE_AUTORIZADO);
        usuarioMqRequest.setEmpresa(Arrays.asList(CodigoEmpresa.CLARO_MOVEL));
        usuarioMqRequest.setUsuarioCadastroId(100);
        return usuarioMqRequest;
    }

    private UsuarioFiltrosHierarquia getFiltroHierarquia() {
        UsuarioFiltrosHierarquia usuarioFiltrosHierarquia = new UsuarioFiltrosHierarquia();
        usuarioFiltrosHierarquia.setUsuarioId(Collections.singletonList(101));
        usuarioFiltrosHierarquia.setCodigoNivel(CodigoNivel.OPERACAO);
        usuarioFiltrosHierarquia.setCodigoDepartamento(CodigoDepartamento.COMERCIAL);
        usuarioFiltrosHierarquia.setCodigoCargo(CodigoCargo.GERENTE_OPERACAO);
        return usuarioFiltrosHierarquia;
    }

}
