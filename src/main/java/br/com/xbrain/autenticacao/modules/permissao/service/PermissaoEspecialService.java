package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
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
    private static final Integer DESCARTAR_LEAD = 15012;
    private static final Integer AGENDAR_LEAD = 15005;
    private static final Integer DEPARTAMENTO_ID = 40;

    @Autowired
    private PermissaoEspecialRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioService usuarioService;

    public void save(PermissaoEspecialRequest request) {
        Usuario usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();

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

    public void processaPermissoesEspeciaisCoordenadoresGerentes() {
        var usuarioLogado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        var filtro = UsuarioFiltros.builder()
            .codigosCargos(List.of(AGENTE_AUTORIZADO_GERENTE, AGENTE_AUTORIZADO_COORDENADOR))
            .departamentoId(DEPARTAMENTO_ID)
            .build();
        var usuarioIds = usuarioService.getAllByPredicate(filtro);
        var request = new PermissaoEspecialRequest();
        var localDateTime = LocalDateTime.now();

        request.setFuncionalidadesIds(List.of(DESCARTAR_LEAD, AGENDAR_LEAD));
        usuarioIds.forEach(ids -> {
            repository.save(
                request.getFuncionalidadesIds()
                    .stream()
                    .map(id -> PermissaoEspecial
                        .builder()
                        .funcionalidade(Funcionalidade.builder().id(id).build())
                        .usuario(new Usuario(ids.getId()))
                        .dataCadastro(localDateTime)
                        .usuarioCadastro(usuarioLogado)
                        .build())
                    .collect(Collectors.toList()));
        });
    }
}
