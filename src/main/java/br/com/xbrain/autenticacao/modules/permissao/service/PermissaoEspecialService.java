package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissaoEspecialService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Permissão Especial não encontrada.");

    @Autowired
    private PermissaoEspecialRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private FeederService feederService;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;

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

    public void processarPermissoesEspeciaisGerentesCoordenadores(List<Integer> aaId) {
        if (autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            var usuarioLogado = autenticacaoService.getUsuarioAutenticado().getId();
            var usuariosIds = aaId != null
                ? aaId
                : agenteAutorizadoService.getAaFeederPorCargo(List.of(
                CodigoCargo.AGENTE_AUTORIZADO_GERENTE, CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR));

            feederService.salvarPermissoesEspeciaisCoordenadoresGestores(usuariosIds, usuarioLogado);
        }
    }
}
