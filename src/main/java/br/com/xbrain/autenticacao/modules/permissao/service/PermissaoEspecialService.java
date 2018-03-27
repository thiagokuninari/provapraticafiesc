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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissaoEspecialService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Permissão Especial não encontrada.");

    @Autowired
    private PermissaoEspecialRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public void save(PermissaoEspecialRequest request) {
        Usuario usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        List<PermissaoEspecial> itens = request.getFuncionalidadesIds()
                .stream()
                .map(item -> criarPermissaoEspecial(item, request.getUsuarioId(), usuarioAutenticado))
                .collect(Collectors.toList());
        repository.save(itens);
    }

    private PermissaoEspecial criarPermissaoEspecial(Integer item, Integer usuarioId,
                                                     Usuario usuarioAutenticado) {
        return PermissaoEspecial.builder()
                .id(null)
                .funcionalidade(new Funcionalidade(item))
                .usuario(new Usuario(usuarioId))
                .dataCadastro(LocalDateTime.now())
                .usuarioCadastro(usuarioAutenticado)
                .build();
    }

    public PermissaoEspecial remover(int usuarioId, int funcionalidadeId) {
        PermissaoEspecial permissaoEspecial = repository.findOneByUsuarioIdAndFuncionalidadeId(
                usuarioId, funcionalidadeId).orElseThrow(() -> EX_NAO_ENCONTRADO);
        permissaoEspecial.baixar(autenticacaoService.getUsuarioId());
        repository.save(permissaoEspecial);
        return permissaoEspecial;
    }
}
