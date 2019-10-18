package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryCustom {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findUsuarioByEmail(String email);

    Optional<Usuario> findComplete(Integer id);

    Optional<List<Cidade>> findComCidade(Integer id);

    List<Integer> getUsuariosSubordinados(Integer usuarioId);

    List<Object[]> getSubordinadosPorCargo(Integer usuarioId, String codigoCargo);

    List<UsuarioSubordinadoDto> getUsuariosCompletoSubordinados(Integer usuarioId);

    List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(Integer usuarioId);

    List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial();

    List<UsuarioAutoComplete> findAllExecutivosDosIds(List<Integer> agenteAutorizadoId);

    List<Usuario> getSuperioresDoUsuario(Integer usuarioId);

    List<Usuario> getSuperioresDoUsuarioPorCargo(Integer usuarioId, CodigoCargo codigoCargo);

    List<Usuario> getUsuariosFilter(Predicate predicate);

    List<UsuarioResponse> getUsuariosSuperiores(UsuarioFiltrosHierarquia filtros);

    List<Usuario> findAllLideresComerciaisDoExecutivo(Integer executivoId);

    List<Usuario> getUsuariosSuperioresDoExecutivoDoAa(Integer executivoId);

    Optional<UsuarioHierarquia> getUsuarioSuperior(Integer usuarioId);

    List<UsuarioHierarquia> getUsuarioSuperiores(Integer usuarioId);

    List<PermissaoEspecial> getUsuariosByPermissao(String codigoFuncionalidade);

    List<Usuario> getUsuariosByNivel(CodigoNivel codigoNivel);

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
                                                    CodigoCargo cargo,
                                                    ECanal canal);

    List<SubCluster> getSubclustersUsuario(Integer usuarioId);

    List<UsuarioPermissoesResponse> getUsuariosIdAndPermissoes(List<Integer> usuariosIds, List<String> permissoes);

    List<Usuario> findAllUsuariosSemDataUltimoAcesso();

    FunilProspeccaoUsuarioDto findUsuarioGerenteByUf(Integer ufId);

    List<UsuarioAutoComplete> findAllExecutivosDosIdsCoordenadorGerente(List<Integer> agenteAutorizadoId, Integer usuarioId);
}
