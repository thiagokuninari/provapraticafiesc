package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PermissaoEspecialService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Permissão Especial não encontrada.");
    public static final List<Integer> FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR =
        List.of(3046, 15000, 15005, 15012, 16101);

    private final PermissaoEspecialRepository repository;
    private final AutenticacaoService autenticacaoService;
    private final FeederService feederService;
    private final AgenteAutorizadoService agenteAutorizadoService;
    private final UsuarioService usuarioService;

    public void save(PermissaoEspecialRequest request) {
        var usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();

        repository.save(
            request.getFuncionalidadesIds()
                .stream()
                .map(id -> PermissaoEspecial
                    .builder()
                    .funcionalidade(Funcionalidade.builder().id(id).build())
                    .usuario(new Usuario(request.getUsuarioId()))
                    .dataCadastro(LocalDateTime.now())
                    .usuarioCadastro(usuario)
                    .build())
                .collect(toList()));
    }

    public void save(List<PermissaoEspecial> permissoes) {
        repository.save(permissoes);
    }

    public PermissaoEspecial remover(int usuarioId, int funcionalidadeId) {
        return repository
            .findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(usuarioId, funcionalidadeId)
            .map(p -> {
                p.baixar(autenticacaoService.getUsuarioId());
                return repository.save(p);
            })
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public void processarPermissoesEspeciaisGerentesCoordenadores(List<Integer> aaIds) {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        var usuarioLogado = autenticacaoService.getUsuarioAutenticado().getId();
        var usuariosIds = agenteAutorizadoService.getUsuariosAaFeederPorCargo(aaIds, List.of(
                CodigoCargo.AGENTE_AUTORIZADO_GERENTE, CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR))
            .stream()
            .filter(Objects::nonNull)
            .collect(toList());

        feederService.salvarPermissoesEspeciaisCoordenadoresGerentes(usuariosIds, usuarioLogado);
    }

    public void reprocessarPermissoesEspeciaisSociosSecundarios(List<Integer> aaIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        usuarioAutenticado.validarAdministrador();
        var usuariosIds = agenteAutorizadoService.getUsuariosAaFeederPorCargo(aaIds, List.of(
                CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO))
            .stream()
            .filter(Objects::nonNull)
            .collect(toList());

        feederService.salvarPermissoesEspeciaisSociosSecundarios(usuariosIds, usuarioAutenticado.getId());
    }

    public boolean hasPermissaoEspecialAtiva(Integer usuarioId, Integer funcionalidadeId) {
        return repository.existsByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(usuarioId, funcionalidadeId);
    }

    public void deletarPermissoesEspeciaisBy(List<Integer> funcionalidadesIds, List<Integer> usuariosIds) {
        repository.deletarPermissaoEspecialBy(funcionalidadesIds, usuariosIds);
    }

    @Transactional
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal(UsuarioDto socio) {
        if (socio.getAntigosSociosPrincipaisIds() != null) {
            getFuncionalidadesIds(FUNC_FEEDER_E_ACOMP_INDICACOES_TECNICO_VENDEDOR, socio.getAntigosSociosPrincipaisIds())
                .forEach(funcionalidadeId -> criarESalvarPermissaoEspecial(socio, funcionalidadeId));
        }
    }

    private List<Integer> getFuncionalidadesIds(List<Integer> permissoesIds, List<Integer> usuariosIds) {
        return usuariosIds.stream()
            .map(usuarioId -> getFuncionalidadesId(permissoesIds, usuarioId))
            .flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }

    private List<Integer> getFuncionalidadesId(List<Integer> permissoesIds, Integer usuarioId) {
        return getPermissoesEspeciais(permissoesIds, usuarioId).stream()
            .map(PermissaoEspecial::getFuncionalidade)
            .map(Funcionalidade::getId)
            .distinct()
            .collect(toList());
    }

    private List<PermissaoEspecial> getPermissoesEspeciais(List<Integer> permissoesIds, Integer usuarioId) {
        return repository.findAllByFuncionalidadeIdInAndUsuarioIdAndDataBaixaIsNull(permissoesIds, usuarioId);
    }

    private void criarESalvarPermissaoEspecial(UsuarioDto usuario, Integer funcionalidadeId) {
        repository.save(PermissaoEspecial.of(usuario.getId(), funcionalidadeId, usuario.getUsuarioCadastroId()));
    }
}
