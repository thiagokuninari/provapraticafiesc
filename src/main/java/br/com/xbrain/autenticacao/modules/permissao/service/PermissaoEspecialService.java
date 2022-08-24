package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltros;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_GERENTE;

@Service
public class PermissaoEspecialService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Permissão Especial não encontrada.");
    private static final Integer DEPARTAMENTO_ID = 40;

    @Autowired
    private PermissaoEspecialRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private FeederService feederService;
    @Autowired
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;

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
                .collect(Collectors.toList()));
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

    public void processarPermissoesEspeciaisGerentesCoordenadores() {
        if (autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            var usuarioLogado = autenticacaoService.getUsuarioAutenticado().getId();
            var usuariosFeeder = buscaGerentesCoordenadoresFeeder();
            feederService.salvarPermissoesEspeciaisCoordenadoresGestores(usuariosFeeder, usuarioLogado);
        }
    }

    private List<Integer> buscaGerentesCoordenadoresFeeder() {
        var filtro = UsuarioFiltros.builder()
            .codigosCargos(List.of(AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_COORDENADOR))
            .departamentoId(DEPARTAMENTO_ID)
            .situacoes(List.of(ESituacao.A))
            .build();
        var usuarioIds = usuarioService.getAllByPredicate(filtro);
        var ids = usuarioIds
            .stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());
        return agenteAutorizadoNovoService.buscarAasFeederPorUsuario(ids);
    }
}
