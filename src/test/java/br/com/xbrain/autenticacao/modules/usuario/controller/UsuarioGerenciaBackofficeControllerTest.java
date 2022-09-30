package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioBackofficeDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_usuario_backoffice.sql"})
public class UsuarioGerenciaBackofficeControllerTest {

    private static final String API_URI_BACKOFFICE = "/api/usuarios/gerencia/backoffice";
    private static final String API_URI_GERENCIA = "/api/usuarios/gerencia";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmailService emailService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private CargoSuperiorRepository cargoSuperiorRepository;

    @Test
    public void save_deveRetornarUsuarioSalvo_quandoNivelForBackoffice() throws Exception {
        var usuario = umUsuarioDtoBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        mvc.perform(post(API_URI_BACKOFFICE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(usuario))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty());

        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void save_badRequest_quandoValidarOsCamposObrigatorios() throws Exception {
        mvc.perform(post(API_URI_BACKOFFICE)
            .content(convertObjectToJsonBytes(new UsuarioBackofficeDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome é obrigatório.",
                "O campo cpf é obrigatório.",
                "O campo nascimento é obrigatório.",
                "O campo email é obrigatório.",
                "O campo cargoId é obrigatório.",
                "O campo departamentoId é obrigatório.")));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacao_quandoCargoGerenteCsu()
        throws Exception {

        var gerenteCsu = umUsuarioAutenticadoNivelBackoffice();
        gerenteCsu.setId(1000);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(113, 112, 111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(gerenteCsu);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.content[0].id", is(1000)))
            .andExpect(jsonPath("$.content[0].email", is("GERENTE_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1001)))
            .andExpect(jsonPath("$.content[1].email", is("COORD_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1002)))
            .andExpect(jsonPath("$.content[2].email", is("SUPERVISOR_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[3].id", is(1003)))
            .andExpect(jsonPath("$.content[3].email", is("ANALISTA_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[4].id", is(1004)))
            .andExpect(jsonPath("$.content[4].email", is("OPERADOR_CSU@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(gerenteCsu.getCargoId()));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacao_quandoCargoCoordenadorCsu()
        throws Exception {

        var coordCsu = umUsuarioAutenticadoNivelBackoffice();
        coordCsu.setId(1001);
        coordCsu.setCargoId(113);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(112, 111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(coordCsu);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)))
            .andExpect(jsonPath("$.content[0].id", is(1001)))
            .andExpect(jsonPath("$.content[0].email", is("COORD_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1002)))
            .andExpect(jsonPath("$.content[1].email", is("SUPERVISOR_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1003)))
            .andExpect(jsonPath("$.content[2].email", is("ANALISTA_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[3].id", is(1004)))
            .andExpect(jsonPath("$.content[3].email", is("OPERADOR_CSU@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(coordCsu.getCargoId()));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacao_quandoCargoSupervisorCsu()
        throws Exception {

        var supervisorCsu = umUsuarioAutenticadoNivelBackoffice();
        supervisorCsu.setId(1002);
        supervisorCsu.setCargoId(112);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(supervisorCsu);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].id", is(1002)))
            .andExpect(jsonPath("$.content[0].email", is("SUPERVISOR_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1003)))
            .andExpect(jsonPath("$.content[1].email", is("ANALISTA_CSU@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1004)))
            .andExpect(jsonPath("$.content[2].email", is("OPERADOR_CSU@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(supervisorCsu.getCargoId()));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacaoEmpresa_quandoCargoGerenteMotiva()
        throws Exception {

        var gerenteMotiva = umUsuarioAutenticadoNivelBackoffice();
        gerenteMotiva.setId(1005);
        gerenteMotiva.setOrganizacaoId(9);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(113, 112, 111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(gerenteMotiva);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.content[0].id", is(1005)))
            .andExpect(jsonPath("$.content[0].email", is("GERENTE_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1006)))
            .andExpect(jsonPath("$.content[1].email", is("COORD_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1007)))
            .andExpect(jsonPath("$.content[2].email", is("SUPERVISOR_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[3].id", is(1008)))
            .andExpect(jsonPath("$.content[3].email", is("ANALISTA_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[4].id", is(1009)))
            .andExpect(jsonPath("$.content[4].email", is("OPERADOR_MOTIVA@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(gerenteMotiva.getCargoId()));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacaoEmpresa_quandoCargoCoordenadorMotiva()
        throws Exception {

        var coordMotiva = umUsuarioAutenticadoNivelBackoffice();
        coordMotiva.setId(1006);
        coordMotiva.setOrganizacaoId(9);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(112, 111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(coordMotiva);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)))
            .andExpect(jsonPath("$.content[0].id", is(1006)))
            .andExpect(jsonPath("$.content[0].email", is("COORD_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1007)))
            .andExpect(jsonPath("$.content[1].email", is("SUPERVISOR_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1008)))
            .andExpect(jsonPath("$.content[2].email", is("ANALISTA_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[3].id", is(1009)))
            .andExpect(jsonPath("$.content[3].email", is("OPERADOR_MOTIVA@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(coordMotiva.getCargoId()));
    }

    @Test
    public void getAll_deveRetornarOsUsuariosDeCargoInferioresDaMesmaOrganizacaoEmpresa_quandoCargoSupervisorMotiva()
        throws Exception {

        var supervisorMotiva = umUsuarioAutenticadoNivelBackoffice();
        supervisorMotiva.setId(1007);
        supervisorMotiva.setOrganizacaoId(9);

        when(cargoSuperiorRepository.getCargosHierarquia(any()))
            .thenReturn(List.of(111, 110));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(supervisorMotiva);

        mvc.perform(get(API_URI_GERENCIA)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].id", is(1007)))
            .andExpect(jsonPath("$.content[0].email", is("SUPERVISOR_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[1].id", is(1008)))
            .andExpect(jsonPath("$.content[1].email", is("ANALISTA_MOTIVA@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].id", is(1009)))
            .andExpect(jsonPath("$.content[2].email", is("OPERADOR_MOTIVA@NET.COM.BR")));

        verify(cargoSuperiorRepository, atLeastOnce()).getCargosHierarquia(eq(supervisorMotiva.getCargoId()));
    }

    private UsuarioBackofficeDto umUsuarioDtoBackoffice() {
        var usuario = new UsuarioBackofficeDto();
        usuario.setNome("USUARIO BACKOFFICE");
        usuario.setCargoId(110);
        usuario.setEmail("usuarioBackoffice@gmail.com");
        usuario.setDepartamentoId(69);
        usuario.setCpf("870.371.018-18");
        usuario.setNascimento(LocalDate.of(2000, 1, 1));
        return usuario;
    }
}
