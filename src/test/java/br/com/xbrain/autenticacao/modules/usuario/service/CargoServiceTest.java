package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CargoServiceTest {

    @InjectMocks
    private CargoService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private CargoSuperiorRepository cargoSuperiorRepository;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void getPermitidosPorNivel_deveRetornarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        when(cargoRepository.findAll(any(BooleanBuilder.class)))
                .thenReturn(umaListaDeCargos());

        assertThat(service.getPermitidosPorNivel(1))
                .extracting("id", "nome")
                .containsExactlyInAnyOrder(
                        tuple(1, "Analista"),
                        tuple(2, "Assistente"),
                        tuple(3, "Consultor"),
                        tuple(4, "Coordenador"),
                        tuple(6, "Diretor"),
                        tuple(5, "Executivo"),
                        tuple(7, "Gerente"),
                        tuple(10, "Supervisor"),
                        tuple(9, "Técnico"),
                        tuple(8, "Vendedor"),
                        tuple(94, "Assistente Hunter"),
                        tuple(95, "Executivo Hunter"));
    }

    @Test
    public void getPermitidosPorNivel_deveNaoRetornarOsCargos_somenteAbaixoDaHierarquiaDoCargo() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .cargoId(10)
                        .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                        .build());

        when(cargoRepository.findAll(any(BooleanBuilder.class)))
                .thenReturn(List.of(umCargo(8, "Vendedor", ESituacao.A)));

        assertThat(service.getPermitidosPorNivel(1))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(8, "Vendedor"));
    }

    @Test
    public void save_deveRetornarCargo_quandoForSalvo() throws Exception {
        when(cargoRepository.save(any(Cargo.class)))
                .thenReturn(umCargo(1, "Vendedor", ESituacao.A));

        assertThat(service.save(umCargo(1, "Vendedor", ESituacao.A)))
                .extracting("nome").contains("Vendedor");
    }

    @Test
    public void update_deveRetonarCargo_quandoForAtualizado() throws Exception {
        when(cargoRepository.exists(any()))
                .thenReturn(true);

        when(cargoRepository.findById(any()))
                .thenReturn(Optional.of(umCargo(1, "Assistente", ESituacao.A)));

        when(cargoRepository.save(any(Cargo.class)))
                .thenReturn(umCargo(1, "Vendedor", ESituacao.A));

        assertThat(service.update(umCargo(1, "Vendedor", ESituacao.A)))
                .extracting("nome").contains("Vendedor");
    }

    @Test
    public void situacao_deveRetonarCargo_quandoSituacaoForAlterado() throws Exception {
        when(cargoRepository.findById(any()))
                .thenReturn(Optional.of(umCargo(1, "Vendedor", ESituacao.A)));

        when(cargoRepository.save(any(Cargo.class)))
                .thenReturn(umCargo(1, "Vendedor", ESituacao.I));

        assertThat(service.situacao(umCargoRequest(1, "Vendedor", ESituacao.I)))
                .extracting("situacao").contains(ESituacao.I);
    }

    private Cargo umCargo(Integer id, String nome, ESituacao situacao) {
        return Cargo.builder()
                .id(id)
                .nome(nome)
                .situacao(situacao)
                .nivel(Nivel.builder()
                        .id(1)
                        .nome("Vendedor")
                        .build())
                .build();
    }

    private CargoRequest umCargoRequest(Integer id, String nome, ESituacao situacao) {
        return CargoRequest.builder()
                .id(id)
                .nome(nome)
                .situacao(situacao)
                .nivel(Nivel
                        .builder()
                        .id(1)
                        .build())
                .build();
    }

    private List<Cargo> umaListaDeCargos() {
        return List.of(umCargo(1, "Analista", ESituacao.A),
                umCargo(2, "Assistente", ESituacao.A),
                umCargo(3, "Consultor", ESituacao.A),
                umCargo(4, "Coordenador", ESituacao.A),
                umCargo(6, "Diretor", ESituacao.A),
                umCargo(5, "Executivo", ESituacao.A),
                umCargo(7, "Gerente", ESituacao.A),
                umCargo(10, "Supervisor", ESituacao.A),
                umCargo(9, "Técnico", ESituacao.A),
                umCargo(8, "Vendedor", ESituacao.A),
                umCargo(94, "Assistente Hunter", ESituacao.A),
                umCargo(95, "Executivo Hunter", ESituacao.A));
    }
}
