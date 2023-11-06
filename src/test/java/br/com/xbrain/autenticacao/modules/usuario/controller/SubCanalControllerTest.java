package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalCompletDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalInativoCompletDto;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.MSO_ANALISTAADM_CLAROMOVEL_PESSOAL;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class SubCanalControllerTest {
    private static final String API_URI = "/api/sub-canais";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private SubCanalService subCanalService;

    @Test
    public void getAllSubCanais_deveRetornarOsSubCanais_quandoSolicitado() throws Exception {
        when(subCanalService.getAll()).thenReturn(List.of(
            new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A),
            new SubCanalDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A)));

        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].codigo", is("PAP")))
            .andExpect(jsonPath("$[0].nome", is("PAP")))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].codigo", is("PAP_PME")))
            .andExpect(jsonPath("$[1].nome", is("PAP PME")))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    public void getAllSubCanalById_deveRetornarSubCanal_quandoExistir() throws Exception {
        when(subCanalService.getSubCanalById(anyInt())).thenReturn(
            new SubCanalDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A));

        mvc.perform(get(API_URI + "/2")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.codigo", is("PAP_PME")))
            .andExpect(jsonPath("$.nome", is("PAP PME")))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getSubCanalCompletById_deveRetornarUnauthorized_quandoUsuarioNaoLogado() throws Exception {
        mvc.perform(get(API_URI + "/1/detalhar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(subCanalService, never()).getSubCanalCompletById(any());
    }

    @Test
    public void getSubCanalCompletById_deveRetornarSubCanal_quandoOk() throws Exception {
        when(subCanalService.getSubCanalCompletById(1)).thenReturn(
            new SubCanalCompletDto(1, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A, Eboolean.V));

        mvc.perform(get(API_URI + "/1/detalhar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.codigo", is("PAP_PME")))
            .andExpect(jsonPath("$.nome", is("PAP PME")))
            .andExpect(jsonPath("$.situacao", is("A")))
            .andExpect(jsonPath("$.novaChecagemCredito", is("V")));

        verify(subCanalService).getSubCanalCompletById(1);
    }

    @Test
    public void getSubCanalCompletById_deveRetornarBadRequest_quandoSubCanalNaoEncontrado() throws Exception {
        when(subCanalService.getSubCanalCompletById(1))
            .thenThrow(new ValidacaoException("Erro, subcanal não encontrado."));

        mvc.perform(get(API_URI + "/1/detalhar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Erro, subcanal não encontrado.")));

        verify(subCanalService).getSubCanalCompletById(1);
    }

    @Test
    public void getAllSubCanaisConfiguracoes_deveRetornarPageDeSubCanais_quandoOk() throws Exception {
        var pageRequest = new PageRequest();
        var filtros = new SubCanalFiltros();
        when(subCanalService.getAllConfiguracoes(pageRequest, filtros))
            .thenReturn(new PageImpl<>(List.of(
            new SubCanalCompletDto(1, ETipoCanal.PAP, "PAP", ESituacao.A, Eboolean.F),
            new SubCanalCompletDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A, Eboolean.V))));

        mvc.perform(get(API_URI + "/listar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].codigo", is("PAP")))
            .andExpect(jsonPath("$.content[0].nome", is("PAP")))
            .andExpect(jsonPath("$.content[0].situacao", is("A")))
            .andExpect(jsonPath("$.content[0].novaChecagemCredito", is("F")))
            .andExpect(jsonPath("$.content[1].id", is(2)))
            .andExpect(jsonPath("$.content[1].codigo", is("PAP_PME")))
            .andExpect(jsonPath("$.content[1].nome", is("PAP PME")))
            .andExpect(jsonPath("$.content[1].novaChecagemCredito", is("V")))
            .andExpect(jsonPath("$.content[1].situacao", is("A")));

        verify(subCanalService).getAllConfiguracoes(pageRequest, filtros);
    }

    @Test
    public void getAllSubCanaisConfiguracoes_deveRetornarUnauthorized_quandoUsuarioNaoLogado() throws Exception {
        mvc.perform(get(API_URI + "/listar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(subCanalService, never()).getAllConfiguracoes(any(), any());
    }

    @Test
    public void editar_deveEditarSubCanal_quandoOk() throws Exception {
        var dto = umSubCanalInativoCompletDto(2, ETipoCanal.PAP_PREMIUM, "Um Outro Nome");

        mvc.perform(post(API_URI + "/editar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());

        verify(subCanalService).editar(dto);
    }

    @Test
    public void editar_deveRetornarUnauthorized_quandoUserNaoForAdmin() throws Exception {
        mvc.perform(post(API_URI + "/editar")
                .header("Authorization",
                    getAccessToken(mvc, MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SubCanalDto())))
            .andExpect(status().isUnauthorized());

        verify(subCanalService, never()).editar(any());
    }

    @Test
    public void editar_deveRetornarException_quandoCamposObrigatoriosNaoPresentes() throws Exception {
        var dto = new SubCanalDto();

        mvc.perform(post(API_URI + "/editar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo id é obrigatório.",
                "O campo codigo é obrigatório.",
                "O campo nome Não pode estar em branco",
                "O campo situacao é obrigatório.",
                "O campo novaChecagemCredito é obrigatório.")));

        verify(subCanalService, never()).editar(any());
    }

    @Test
    public void editar_deveRetornarException_quandoUsuarioLogadoNaoForAdmin() throws Exception {
        var dto = umSubCanalInativoCompletDto(2, ETipoCanal.PAP_PREMIUM, "Um Outro Nome");
        dto.setNovaChecagemCredito(Eboolean.V);

        doThrow(PermissaoException.class).when(subCanalService).editar(dto);

        mvc.perform(post(API_URI + "/editar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Usuário sem permissão sobre a entidade requisitada.")));

        verify(subCanalService).editar(dto);
    }

    @Test
    public void editar_deveRetornarUnauthorized_quandoNaoHouverUsuarioLogado() throws Exception {
        var dto = umSubCanalInativoCompletDto(2, ETipoCanal.PAP_PREMIUM, "Um Outro Nome");
        dto.setSituacao(ESituacao.I);

        mvc.perform(post(API_URI + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isUnauthorized());

        verify(subCanalService, never()).editar(any());
    }

    @Test
    public void isNovaChecagemCredito_deveDevolverBoolean_quandoOk() throws Exception {
        when(subCanalService.isNovaChecagemCreditoD2d(1))
            .thenReturn(Eboolean.V);

        mvc.perform(get(API_URI + "/1/verificar-nova-checagem-credito-d2d")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Eboolean.V.toString())));

        verify(subCanalService).isNovaChecagemCreditoD2d(eq(1));
    }

    @Test
    public void isNovaChecagemCredito_deveRetornarUnauthorized_quandoUsuarioNaoLogado() throws Exception {
        when(subCanalService.isNovaChecagemCreditoD2d(1))
            .thenReturn(Eboolean.V);

        mvc.perform(get(API_URI + "/1/verificar-nova-checagem-credito-d2d"))
            .andExpect(status().isUnauthorized());

        verify(subCanalService, never()).isNovaChecagemCreditoD2d(any());
    }

    @Test
    public void isNovaChecagemCredito_deveRetornarBadRequest_quandoSubCanalNaoEncontrado() throws Exception {
        when(subCanalService.isNovaChecagemCreditoD2d(1))
            .thenThrow(new ValidacaoException("Erro, subcanal não encontrado."));

        mvc.perform(get(API_URI + "/1/verificar-nova-checagem-credito-d2d")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Erro, subcanal não encontrado.")));
        verify(subCanalService).isNovaChecagemCreditoD2d(eq(1));
    }
}
