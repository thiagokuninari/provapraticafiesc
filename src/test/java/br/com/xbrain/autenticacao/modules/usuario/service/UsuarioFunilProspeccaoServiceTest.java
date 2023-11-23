package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.FunilProspeccaoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioFunilProspeccaoServiceTest {

    private static final String CIDADE_BUSCA_LONDRINA = "LONDRINA";
    private static final String CIDADE_BUSCA_SAO_PAULO = "SAO PAULO";
    private static final String CIDADE_BUSCA_RIO_DE_JANEIRO = "RIO DE JANEIRO";
    private static final String CIDADE_BUSCA_CAPITOLIO = "CAPITOLIO";
    private static final String CIDADE_BUSCA_GAMA = "GAMA";
    private static final String CIDADE_BUSCA_UMUARAMA = "UMUARAMA";
    private static final ValidacaoException USUARIO_NOT_FOUND_EXCEPTION =
        new ValidacaoException("Usuário não encontrado");
    private static final List<Cargo> LISTA_CARGOS_NIVEL_OPERACAO = List.of(
        umCargoCoordernador(),
        umCargoExecutivo(),
        umCargoGerente(),
        umCargoExecutivoHunter()
    );

    @InjectMocks
    private UsuarioFunilProspeccaoService usuarioFunilProspeccaoService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private CidadeRepository cidadeRepository;
    @Mock
    private UsuarioCidadeRepository usuarioCidadeRepository;

    @Before
    public void setUp() throws Exception {
        when(cargoRepository.findByCodigoIn(List.of(EXECUTIVO_HUNTER, EXECUTIVO, COORDENADOR_OPERACAO, GERENTE_OPERACAO)))
            .thenReturn(LISTA_CARGOS_NIVEL_OPERACAO);
        when(cidadeRepository.findCidadeByNomeLike(CIDADE_BUSCA_LONDRINA)).thenReturn(List.of(umaCidadeLondrina()));
        when(cidadeRepository.findCidadeByNomeLike(CIDADE_BUSCA_SAO_PAULO)).thenReturn(List.of(umaCidadeSaoPaulo()));
        when(cidadeRepository.findCidadeByNomeLike(CIDADE_BUSCA_RIO_DE_JANEIRO)).thenReturn(List.of(umaCidadeRioDeJaneiro()));
        when(cidadeRepository.findCidadeByNomeLike(CIDADE_BUSCA_CAPITOLIO)).thenReturn(List.of(umaCidadeCapitolio()));
        when(cidadeRepository.findCidadeByNomeLike(CIDADE_BUSCA_GAMA)).thenReturn(List.of(umaCidadeGama()));
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmGerente_quandoHouverMaisDeUmExecutivoOuCoordenador() {
        var usuarioIds = List.of(1, 2, 3, 4, 5, 6, 7, 8);

        when(usuarioCidadeRepository.findUsuarioByCidadeIn(List.of(umaCidadeLondrina())))
            .thenReturn(umaListaUsuarioCidadesDeLondrina());

        when(usuarioRepository.findByIdInAndCargoIn(eq(usuarioIds), eq(LISTA_CARGOS_NIVEL_OPERACAO)))
            .thenReturn(umaListaDeUsuariosDeLondrina());

        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioComCargo(3, umCargoGerente())));

        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_LONDRINA);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(3));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(GERENTE_OPERACAO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmCoordenador_quandoHouverMaisDeUmExecutivo() {
        var usuarioIds = List.of(1, 2, 3, 4, 5, 7, 8);

        when(usuarioCidadeRepository.findUsuarioByCidadeIn(List.of(umaCidadeSaoPaulo())))
            .thenReturn(umaListaUsuarioCidadesDeSaoPaulo());

        when(usuarioRepository.findByIdInAndCargoIn(eq(usuarioIds), eq(LISTA_CARGOS_NIVEL_OPERACAO)))
            .thenReturn(umaListaDeUsuariosDeSaoPaulo());

        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioComCargo(5, umCargoCoordernador())));

        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_SAO_PAULO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);

        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(5));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(COORDENADOR_OPERACAO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmExecutivo_quandoHouverApenasUmNaCidade() {
        var usuarioIds = List.of(1, 3, 4, 5, 7, 8);

        when(usuarioCidadeRepository.findUsuarioByCidadeIn(List.of(umaCidadeRioDeJaneiro())))
            .thenReturn(umaListaUsuarioCidadesDeRioDeJaneiro());

        when(usuarioRepository.findByIdInAndCargoIn(eq(usuarioIds), eq(LISTA_CARGOS_NIVEL_OPERACAO)))
            .thenReturn(umaListaDeUsuariosDeRioDeJaneiro());

        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioComCargo(1, umCargoExecutivo())));

        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_RIO_DE_JANEIRO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);

        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(1));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(EXECUTIVO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmExecutivoHunter_quandoHouverApenasUmNaCidade() {
        var usuarioIds = List.of(1, 3, 7);

        when(usuarioCidadeRepository.findUsuarioByCidadeIn(List.of(umaCidadeCapitolio())))
            .thenReturn(umaListaUsuarioCidadesDeCapitolio());

        when(usuarioRepository.findByIdInAndCargoIn(eq(usuarioIds), eq(LISTA_CARGOS_NIVEL_OPERACAO)))
            .thenReturn(umaListaDeUsuariosDeCapitolio());

        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioComCargo(7, umCargoExecutivoHunter())));

        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_CAPITOLIO);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);

        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(7));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(EXECUTIVO_HUNTER);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveRetornarUmGerente_quandoNaoHouverUsuarioNaCidade() {
        when(usuarioRepository.findUsuarioGerenteByUf(any())).thenReturn(new FunilProspeccaoUsuarioDto(9));

        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioComCargo(9, umCargoGerente())));

        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_GAMA);
        var usuario = usuarioRepository.findById(usuarioRediredionado.getUsuarioId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);

        assertThat(usuarioRediredionado).isEqualTo(new FunilProspeccaoUsuarioDto(9));
        assertThat(usuario.getCargo().getCodigo()).isEqualTo(GERENTE_OPERACAO);
    }

    @Test
    public void findUsuarioDirecionadoByCidade_deveDtoNula_quandoNaoHouverUsuarioNaCidade() {
        var usuarioRediredionado = usuarioFunilProspeccaoService
            .findUsuarioDirecionadoByCidade(CIDADE_BUSCA_UMUARAMA);
        assertThat(usuarioRepository.findById(usuarioRediredionado.getUsuarioId()).isEmpty()).isTrue();
    }
}
