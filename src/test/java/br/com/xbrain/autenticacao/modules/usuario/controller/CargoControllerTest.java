package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import helpers.TestsHelper;
import helpers.Usuarios;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.SUPERVISOR_ATIVO_RENTABILIZACAO;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CargoControllerTest {

    @Autowired
    private CargoRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mvc;

    // tests variables
    // INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL)
    // VALUES (74, 'SUPERVISOR_ATIVO_RENTABILIZACAO', 'Supervisor Ativo Rentabilização', 'A', 13);
    private Integer testObjectId = 74;
    private CodigoCargo testObjectCodigo = SUPERVISOR_ATIVO_RENTABILIZACAO;
    private String testObjectNome = "Supervisor Ativo Rentabilização";
    private ESituacao testObjectSituacao = A;
    private Integer testObjectNivel = 13;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/cargos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deveRetornarOsCargosAtivosPorNivel() throws Exception {
        mvc.perform(get("/api/cargos?nivelId=4")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].nome", is("Administrador")));
    }

    @Test
    public void deveRetornarSomenteExecutivoParaUsuario() throws Exception {
        mvc.perform(get("/api/cargos?nivelId=1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Executivo")));
    }

    @Test
    public void deveRetornarOsCargosAtivosPorNivelPaginacao() throws Exception {
        mvc.perform(get("/api/cargos/gerencia?nivelId=4")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].nome", is("Administrador")));
    }

    @Test
    public void deveRetornarSomenteNivelUmPaginacao() throws Exception {
        mvc.perform(get("/api/cargos/gerencia?nivelId=1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..content[?(@.nivel != 1)]", empty()));
    }

    @Test
    public void deveRetornarUmCargo() throws Exception {
        mvc.perform(get("/api/cargos/" + testObjectId)
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(testObjectNome)));
    }

    @Test
    public void deveCriarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests();
        cargoForTests.setId(null);
        cargoForTests.setNome("Cargo teste");

        mvc.perform(post("/api/cargos")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(cargoForTests.getNome())));
    }

    @Test
    public void deveNaoAtualizarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests();
        cargoForTests.setId(10000);
        cargoForTests.setCodigo(AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN);

        mvc.perform(put("/api/cargos/" + cargoForTests.getId())
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveAtualizarUmCargo() throws Exception {
        Cargo cargo = repository.findOne(testObjectId);
        Assert.assertEquals(cargo.getNome(), testObjectNome);

        CargoRequest cargoForTests = getCargoForTests(); // get default object using class variables
        cargoForTests.setNome("Analista Xbrain"); // change the name

        mvc.perform(put("/api/cargos/" + cargoForTests.getId())
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cargoForTests.getId())))
                .andExpect(jsonPath("$.nome", is(cargoForTests.getNome())))
                .andExpect(jsonPath("$.situacao", is(cargoForTests.getSituacao().name())))
                .andExpect(jsonPath("$.codigo", is(cargoForTests.getCodigo().name())))
                .andExpect(jsonPath("$.nivel", is(cargoForTests.getNivel().getId().intValue())))
                .andDo(MockMvcResultHandlers.print());

        refresh();

        Cargo cargoAfter = repository.findOne(testObjectId);
        Assert.assertEquals(cargoAfter.getNome(), cargoForTests.getNome());
    }

    @Test
    public void deveRetornarUmResultadoPaginacao() throws Exception {
        mvc.perform(get("/api/cargos/gerencia?page=0&size=1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(1)))
                .andExpect(jsonPath("$.size", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deveRetornarDezResultadosPaginacao() throws Exception {
        mvc.perform(get("/api/cargos/gerencia?page=0&size=10")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(10)))
                .andExpect(jsonPath("$.size", is(10)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void devePermitirCriarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests();
        cargoForTests.setId(null);
        cargoForTests.setNome("Cargo teste");

        mvc.perform(post("/api/cargos")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isCreated());
    }

    @Test
    public void devePermitirAtualizarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests(); // get default object using class variables
        cargoForTests.setNome("Analista Xbrain"); // change the name

        mvc.perform(put("/api/cargos/" + cargoForTests.getId())
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isOk());
    }

    @Test
    public void deveNaoPermitirCriarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests();
        cargoForTests.setId(null);
        cargoForTests.setNome("Cargo teste");

        mvc.perform(post("/api/cargos")
                .header("Authorization", getAccessToken(mvc, Usuarios.MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveNaoPermitirAtualizarUmCargo() throws Exception {
        CargoRequest cargoForTests = getCargoForTests(); // get default object using class variables
        cargoForTests.setNome("Analista Xbrain"); // change the name

        mvc.perform(put("/api/cargos/" + cargoForTests.getId())
                .header("Authorization", getAccessToken(mvc, Usuarios.MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(cargoForTests)))
                .andExpect(status().isUnauthorized());
    }

    public CargoRequest getCargoForTests() {
        return this.umCargo(testObjectId, testObjectCodigo, testObjectNome, testObjectSituacao, testObjectNivel);
    }

    private CargoRequest umCargo(Integer id, CodigoCargo codigo, String nome, ESituacao situacao, Integer nivel) {
        CargoRequest request = new CargoRequest();
        request.setId(id);
        request.setNome(nome);
        request.setSituacao(situacao);
        request.setCodigo(codigo);
        request.setNivel(new Nivel(nivel));
        return request;
    }

    public void refresh() {
        entityManager.flush();
        entityManager.clear();
    }
}