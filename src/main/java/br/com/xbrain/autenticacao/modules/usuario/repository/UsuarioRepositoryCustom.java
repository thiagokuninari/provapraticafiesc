package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Canal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioRepositoryCustom {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findUsuarioByEmail(String email);

    Optional<Usuario> findComplete(Integer id);

    Optional<List<Cidade>> findComCidade(Integer id);

    List<Integer> getUsuariosSubordinados(Integer usuarioId);

    List<Object[]> getSubordinadosPorCargo(Integer usuarioId, Set<String> codigoCargo);

    List<UsuarioSubordinadoDto> getUsuariosCompletoSubordinados(Integer usuarioId);

    List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(Integer usuarioId);

    List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial(Predicate predicate);

    List<UsuarioAutoComplete> findAllExecutivosAndAssistenteOperacaoDepartamentoComercial(Predicate predicate);

    List<UsuarioAutoComplete> findAllExecutivosDosIds(List<Integer> agenteAutorizadoId);

    List<Usuario> getSuperioresDoUsuario(Integer usuarioId);

    List<Usuario> getSuperioresDoUsuarioPorCargo(Integer usuarioId, CodigoCargo codigoCargo);

    List<Usuario> getUsuariosFilter(Predicate predicate);

    List<UsuarioResponse> getUsuariosSuperiores(UsuarioFiltrosHierarquia filtros);

    List<Integer> getUsuariosSuperiores(Integer usuarioId);

    List<Usuario> findAllLideresComerciaisDoExecutivo(Integer executivoId);

    List<Usuario> getUsuariosSuperioresDoExecutivoDoAa(Integer executivoId);

    Optional<UsuarioHierarquia> getUsuarioSuperior(Integer usuarioId);

    List<UsuarioHierarquia> getUsuarioSuperiores(Integer usuarioId);

    List<Integer> getUsuariosSuperioresIds(List<Integer> usuariosIds);

    List<PermissaoEspecial> getUsuariosByPermissaoEspecial(String codigoFuncionalidade);

    List<Usuario> getUsuariosByNivel(CodigoNivel codigoNivel);

    List<Integer> getUsuariosIdsByNivel(CodigoNivel nivel);

    Page<Usuario> findAll(Predicate predicate, Pageable pageable);

    Optional<Usuario> findComConfiguracao(Integer usuarioId);

    List<UsuarioHierarquiaResponse> findAllUsuariosHierarquia(Predicate predicate);

    List<UsuarioCsvResponse> getUsuariosCsv(Predicate predicate);

    Optional<Usuario> findByEmailIgnoreCaseAndSituacaoNot(String email, ESituacao situacao);

    List<UsuarioResponse> getUsuariosDaMesmaCidadeDoUsuarioId(Integer usuarioId,
                                                              List<CodigoCargo> cargos,
                                                              ECanal canal);

    List<UsuarioResponse> getUsuariosPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                    List<Integer> areasAtuacaoIds,
                                                    List<CodigoCargo> cargos,
                                                    Set<ECanal> canais);

    List<SubCluster> getSubclustersUsuario(Integer usuarioId);

    List<UsuarioPermissoesResponse> getUsuariosIdAndPermissoes(List<Integer> usuariosIds, List<String> permissoes);

    List<UsuarioDto> findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(
        LocalDateTime dataHoraInativarUsuario, List<String> emailsUsuariosViabilidade);

    FunilProspeccaoUsuarioDto findUsuarioGerenteByUf(Integer ufId);

    List<UsuarioAutoComplete> findAllExecutivosDosIdsCoordenadorGerente(List<Integer> agenteAutorizadoId, Integer usuarioId);

    List<Integer> findAllIds(Predicate predicate);

    List<Integer> findAllIds(PublicoAlvoComunicadoFiltros predicate);

    List<UsuarioNomeResponse> findAllNomesIds(PublicoAlvoComunicadoFiltros filtro);

    long deleteUsuarioHierarquia(Integer usuarioId);

    List<UsuarioExecutivoResponse> findAllExecutivosBySituacao(ESituacao situacao);

    List<UsuarioSituacaoResponse> findUsuariosByIds(List<Integer> usuariosIds);

    List<UsuarioResponse> findUsuariosAtivosOperacaoComercialByCargoId(Integer cargoId);

    List<Usuario> findUsuariosByCodigoCargo(CodigoCargo codigoCargo);

    List<Integer> findIdUsuariosAtivosByCodigoCargos(List<CodigoCargo> codigoCargos);

    List<Integer> buscarIdsUsuariosPorCargosIds(List<Integer> cargosIds);

    List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuarioPeloCanal(Integer usuarioId, ECanal canal);

    List<SelectResponse> findAllAtivosByNivelOperacaoCanalAa();

    List<Integer> obterIdsPorUsuarioCadastroId(Integer usuarioCadastroId);

    List<UsuarioNomeResponse> findAllUsuariosNomeComSituacao(Predicate predicate, OrderSpecifier<?>...orderSpecifiers);

    List<UsuarioSituacaoResponse> buscarUsuarioSituacao(Predicate predicate);

    List<Canal> getCanaisByUsuarioIds(List<Integer> usuarioIds);

    List<UsuarioNomeResponse> buscarUsuariosPorCanalECargo(ECanal canal, CodigoCargo cargo);

    List<UsuarioNomeResponse> findCoordenadoresDisponiveis(Predicate sitePredicate);

    List<UsuarioNomeResponse> findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(Integer usuarioId, CodigoCargo cargo);

    List<UsuarioNomeResponse> findSupervisoresSemSitePorCoordenadorId(Predicate sitePredicate);

    List<UsuarioNomeResponse> findVendedoresPorSiteId(Integer siteId);

    List<Integer> findUsuariosIdsPorSiteId(Integer siteId);

    List<UsuarioNomeResponse> findCoordenadoresDisponiveisExetoPorSiteId(Predicate sitePredicate, Integer siteId);

    List<UsuarioResponse> buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List<Integer> superioresIds,
                                                                                 Set<String> codigoCargo);

    List<UsuarioSituacaoResponse> findVendedoresDoSiteIdPorHierarquiaUsuarioId(List<Integer> usuarioId, Integer siteId);

    List<UsuarioNomeResponse> findCoordenadoresDoSiteId(Integer siteId);

    List<UsuarioNomeResponse> findSupervisoresDoSiteIdVinculadoAoCoordenador(Integer siteId, Predicate predicate);

    List<UsuarioCargoResponse> findSuperioresDoUsuarioId(Integer usuarioId);

    List<Usuario> findAllVendedoresReceptivos();

    List<Usuario> findAllVendedoresReceptivosByIds(List<Integer> ids);

    List<UsuarioDto> findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(
        LocalDateTime dataHoraInativarUsuario, List<String> emailsUsuariosViabilidade);

    List<Usuario> getUsuariosOperacaoCanalAa(CodigoNivel nivel);
}
