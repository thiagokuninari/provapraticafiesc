package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioCadastroMqSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EmailService emailService;

    @Before
    public void setUp() {
        when(autenticacaoService.getUsuarioId()).thenReturn(101);
    }

    @Test
    public void deveSalvarUsuarioEEnviarParaFila() {
        UsuarioMqRequest usuarioMqRequest = umUsuario();
        service.saveFromQueue(usuarioMqRequest);
        UsuarioDto usuarioDto = service.findByEmail(usuarioMqRequest.getEmail());
        Assert.assertEquals(usuarioDto.getCpf(), usuarioMqRequest.getCpf());
        verify(sender, times(1)).sendSuccess(any());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
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

    @Test
    public void deveAlterarOCargoDoUsuario() throws Exception {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setCargo(CodigoCargo.EXECUTIVO);
        service.alterarCargoUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getCargoCodigo(), CodigoCargo.EXECUTIVO);
    }

    @Test
    public void deveAlterarOEmailDoUsuario() throws Exception {
        UsuarioAlteracaoRequest usuarioAlteracaoRequest = new UsuarioAlteracaoRequest();
        usuarioAlteracaoRequest.setId(100);
        usuarioAlteracaoRequest.setEmail("EMAILALTERADO@XBRAIN.COM.BR");
        service.alterarEmailUsuario(usuarioAlteracaoRequest);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getEmail(), "EMAILALTERADO@XBRAIN.COM.BR");
    }

    @Test
    public void deveInativarUmUsuario() throws Exception {
        UsuarioInativacaoDto usuarioInativacaoDto = new UsuarioInativacaoDto();
        usuarioInativacaoDto.setIdUsuario(100);
        usuarioInativacaoDto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        usuarioInativacaoDto.setDataCadastro(LocalDateTime.now());
        usuarioInativacaoDto.setObservacao("Teste inativar");
        service.inativar(usuarioInativacaoDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
    }

    @Test
    public void deveAtivarUmUsuario() throws Exception {
        UsuarioAtivacaoDto usuarioAtivacaoDto = new UsuarioAtivacaoDto();
        usuarioAtivacaoDto.setIdUsuario(100);
        usuarioAtivacaoDto.setObservacao("Teste ativar");
        service.ativar(usuarioAtivacaoDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.A);
    }

    @Test
    public void deveAlterarSenhaUsuario() throws Exception {
        UsuarioAlterarSenhaDto usuarioAlterarSenhaDto = new UsuarioAlterarSenhaDto();
        usuarioAlterarSenhaDto.setUsuarioId(100);
        usuarioAlterarSenhaDto.setAlterarSenha(Eboolean.V);
        service.alterarSenhaAa(usuarioAlterarSenhaDto);
        Usuario usuario = service.findById(100);
        Assert.assertEquals(usuario.getAlterarSenha(), Eboolean.V);
    }

    private UsuarioMqRequest umUsuario() {
        UsuarioMqRequest usuarioMqRequest = new UsuarioMqRequest();
        usuarioMqRequest.setNome("TESTE NOVO USUARIO PARCEIROS ONLINE");
        usuarioMqRequest.setEmail("novousuarioparceirosonline@xbrain.com.br");
        usuarioMqRequest.setCpf("76696512616");
        usuarioMqRequest.setUnidadesNegocio(Arrays.asList(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS));
        usuarioMqRequest.setNivel(CodigoNivel.AGENTE_AUTORIZADO);
        usuarioMqRequest.setCargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO);
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
