package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CargoServiceTest {

    @Autowired
    private CargoService service;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private CargoSuperiorRepository cargoSuperiorRepository;

    @Test
    public void getPermitidosPorNivel_deveRetornarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        assertThat(service.getPermitidosPorNivel(1))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(1, "Analista"),
                        tuple(2, "Assistente"),
                        tuple(3, "Consultor"),
                        tuple(4, "Coordenador"),
                        tuple(6, "Diretor"),
                        tuple(5, "Executivo"),
                        tuple(7, "Gerente"),
                        tuple(10, "Supervisor"),
                        tuple(9, "Técnico"),
                        tuple(8, "Vendedor"));
    }

    @Test
    public void getPermitidosPorNivel_deveNaoRetornarOsCargos_somenteAbaixoDaHierarquiaDoCargo() {

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .cargoId(10)
                        .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                        .build());

        when(cargoSuperiorRepository.getCargosHierarquia(eq(10)))
                .thenReturn(List.of(8));

        assertThat(service.getPermitidosPorNivel(1))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(8, "Vendedor"));
    }
}
