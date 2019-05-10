package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConfiguracaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import helpers.Usuarios;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.SOCIO_AA;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EmailService emailService;
    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Before
    public void setup() {
        Mockito.when(autenticacaoService.getUsuarioId())
                .thenReturn(100);
    }

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuarios")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/101")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarTodasAsCidadesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/100/cidades")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("LONDRINA")));
    }

    @Test
    public void deveRetornarNenhumaCidadeParaOUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/101/cidades")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deveRetornarUsuariosPorIds() throws Exception {
        mvc.perform(get("/api/usuarios?ids=100,101,104")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void deveNaoRetornarUsuariosPorIdsInativos() throws Exception {
        mvc.perform(get("/api/usuarios?ids=105")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deveRetornarUsuarioPorIdEVerificarPermissoes() throws Exception {
        mvc.perform(get("/api/usuarios/100")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.nome", is("ADMIN")))
                .andExpect(jsonPath("$.email", is("ADMIN@XBRAIN.COM.BR")))
                .andExpect(jsonPath("$.permissoes[0]", is("ROLE_AUT_ATUALIZAR_SENHA_REENVIAR_EMAIL")));
    }

    @Test
    public void deveRetornarUsuarioAutenticadoPorId() throws Exception {
        mvc.perform(get("/api/usuarios/autenticado/101")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.email", is("HELPDESK@XBRAIN.COM.BR")));
    }

    @Test
    public void deveRetornarOUsuarioPorEmail() throws Exception {
        mvc.perform(get("/api/usuarios?email=ADMIN@XBRAIN.COM.BR")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.nome", is("ADMIN")))
                .andExpect(jsonPath("$.email", is(Usuarios.ADMIN)));
    }

    @Test
    public void deveRetornarUsuarioPorCpf() throws Exception {
        mvc.perform(get("/api/usuarios?cpf=65710871036")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.email", is(Usuarios.HELP_DESK)));
    }

    @Test
    public void deveRetornarAsEmpresasDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/100/empresas")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveRetornarUsuariosPorNivelDoCargo() throws Exception {
        mvc.perform(get("/api/usuarios?nivel=XBRAIN")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void deveRetornarUsuariosPorPermissaoEspecial() throws Exception {
        mvc.perform(get("/api/usuarios?funcionalidade=POL_AGENTE_AUTORIZADO_APROVACAO_MSO_NOVO_CADASTRO")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveRetornarAsConfiguracoesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ramal", is(7006)));
    }

    @Test
    public void deveAdicionarConfiguracaoAoUsuario() throws Exception {
        mvc.perform(post("/api/usuarios/adicionar-configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioConfiguracaoDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ramal", is(1000)));
    }

    @Test
    public void deveRemoverConfiguracaoAoUsuario() throws Exception {
        long quantidadeAntes = configuracaoRepository.count();

        mvc.perform(put("/api/usuarios/remover-configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioConfiguracaoDto())))
                .andExpect(status().isOk());

        long quantidadeDepois = configuracaoRepository.count();

        Assert.assertTrue(quantidadeAntes > quantidadeDepois);
    }

    @Test
    public void deveRemoverRamalConfiguracaoAoUsuario() throws Exception {
        UsuarioConfiguracaoDto dto = umUsuarioConfiguracaoDto();
        Assert.assertFalse(configuracaoRepository.findByRamal(dto.getRamal()).isEmpty());

        mvc.perform(put("/api/usuarios/remover-ramal-configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());

        Assert.assertTrue(configuracaoRepository.findByRamal(dto.getRamal()).isEmpty());
    }

    @Test
    public void deveRemoverRamalConfiguracaoUsuarioRamalDuplicado() throws Exception {
        UsuarioConfiguracaoDto dto = umUsuarioComRamalDuplicado();
        Assert.assertFalse(configuracaoRepository.findByRamal(dto.getRamal()).isEmpty());
        Assert.assertFalse(configuracaoRepository.findByRamal(dto.getRamal()).isEmpty());

        mvc.perform(put("/api/usuarios/remover-ramal-configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());

        Assert.assertTrue(configuracaoRepository.findByRamal(dto.getRamal()).isEmpty());
    }

    @Test
    public void deveEnviarConfirmacaoDeResetDeSenhaEmail() throws Exception {
        mvc.perform(put("/api/usuarios/esqueci-senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umEsqueciSenha())))
                .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveNaoResetarSenhaDoUsuarioComToken() throws Exception {
        mvc.perform(put("/api/usuarios/esqueci-senha")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveResetarSenhaDoUsuarioComToken() throws Exception {
        String hash = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);

        mvc.perform(put("/api/usuarios/esqueci-senha?hash=" + hash)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umEsqueciSenha())))
                .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void getPermissoesPorCanal_permissoesComCanal_somentePermitidasAoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/permissoes-por-canal")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].permissao", is("ROLE_VDS_3021")))
                .andExpect(jsonPath("$[0].canais", hasSize(2)))
                .andExpect(jsonPath("$[0].canais[0]", is("AGENTE_AUTORIZADO")))
                .andExpect(jsonPath("$[0].canais[1]", is("ATIVO")));
    }

    @Test
    public void getSubclustersUsuario_deveRetornarOsSubclusters_conformeUsuarioIdInformado() throws Exception {
        mvc.perform(get("/api/usuarios/100/subclusters")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].value", is(189)))
                .andExpect(jsonPath("$[0].label", is("LONDRINA - Claro")));

    }

    private UsuarioConfiguracaoDto umUsuarioConfiguracaoDto() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1000);
        dto.setUsuario(100);
        return dto;
    }

    private UsuarioDadosAcessoRequest umEsqueciSenha() {
        UsuarioDadosAcessoRequest dto = new UsuarioDadosAcessoRequest();
        dto.setEmailAtual("HELPDESK@XBRAIN.COM.BR");
        return dto;
    }

    private UsuarioConfiguracaoDto umUsuarioComRamalDuplicado() {
        UsuarioConfiguracaoDto dto = new UsuarioConfiguracaoDto();
        dto.setRamal(1008);
        dto.setUsuario(105);
        return dto;
    }
}
