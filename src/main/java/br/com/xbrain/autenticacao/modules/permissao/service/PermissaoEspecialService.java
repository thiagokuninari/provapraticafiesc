package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class PermissaoEspecialService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Permissão Especial não encontrada.");

    @Autowired
    private PermissaoEspecialRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

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
}
