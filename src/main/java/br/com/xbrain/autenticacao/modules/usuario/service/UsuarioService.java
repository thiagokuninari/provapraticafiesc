package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.F;

@Service
public class UsuarioService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuario não encontrado.");

    @Getter
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public Usuario findById(int id) {
        return repository
                .findComplete(id)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public UsuarioDto findByCpf(String cpf) {
        Usuario usuario = repository
                .findByCpf(cpf)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
        return UsuarioDto.parse(usuario);
    }

    public UsuarioDto findByEmail(String email) {
        Usuario usuario = repository
                .findByEmail(email)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
        return UsuarioDto.parse(usuario);
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public UsuarioDto saveUsuarioCidades(UsuarioCidadeSaveDto usuarioCidadeSaveDto) {
        Usuario usuario = findById(usuarioCidadeSaveDto.getUsuarioId());
        usuarioCidadeSaveDto.getCidadesId().forEach(idCidade -> usuario.adicionarCidade(
                new UsuarioCidade(new UsuarioCidadePk(usuario.getId(), idCidade),
                        usuario,
                        new Cidade(idCidade),
                        new Usuario(autenticacaoService.getUsuarioId()),
                        LocalDateTime.now())));
        return UsuarioDto.parse(repository.save(usuario));
    }

    public UsuarioDto save(UsuarioDto usuarioDto) {
        Usuario usuario = Usuario.parse(usuarioDto);
        usuario.removerCaracteresDoCpf();
        if (usuario.isNovoCadastro()) {
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setAlterarSenha(F);
            usuario.setSenha("123456");
            usuario.setUsuarioCadastro(new Usuario(autenticacaoService.getUsuarioId()));
            usuario.setSituacao(ESituacao.A);
        }
        return UsuarioDto.parse(repository.save(usuario));
    }

    public void ativar(UsuarioAtivacaoDto dto) {
        Usuario usuario = repository.findComplete(dto.getIdUsuario()).get();
        usuario.setSituacao(ESituacao.A);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .usuarioAlteracao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.A)
                .build());
        repository.save(usuario);
    }

    public void inativar(UsuarioInativacaoDto dto) {
        Usuario usuario = repository.findComplete(dto.getIdUsuario()).get();
        usuario.setSituacao(ESituacao.I);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(dto.getDataCadastro())
                .motivoInativacao(new MotivoInativacao(dto.getIdMotivoInativacao()))
                .usuario(usuario)
                .usuarioAlteracao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.I)
                .build());
        repository.save(usuario);
    }

}
