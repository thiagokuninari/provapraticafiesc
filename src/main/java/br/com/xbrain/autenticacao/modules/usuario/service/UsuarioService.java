package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAtivacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
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
    @Autowired
    private UsuarioHistoricoRepository historicoRepository;

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

    public void save(UsuarioDto usuarioDto) {
        Usuario usuarioAutenticado = new Usuario(autenticacaoService.getUsuarioId());
        Usuario usuario = Usuario.parse(usuarioDto);
        usuario.removerCaracteresDoCpf();
        usuario.setAlterarSenha(F);
        if (usuario.isNovoCadastro()) {
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setSenha("123456");
            usuario.setUsuarioCadastro(usuarioAutenticado);
            usuario.setSituacao(ESituacao.A);
        }
        repository.save(usuario);
    }

    public void ativar(UsuarioAtivacaoDto dto) {
        Usuario usuario = findById(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.A);
        repository.save(usuario);
        UsuarioHistorico historico = UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .usuarioInativacao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.A)
                .build();
        historicoRepository.save(historico);
    }

    public void inativar(UsuarioInativacaoDto dto) {
        Usuario usuario = findById(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.I);
        repository.save(usuario);
        UsuarioHistorico historico = UsuarioHistorico.builder()
                .dataCadastro(dto.getDataCadastro())
                .motivoInativacao(new MotivoInativacao(dto.getIdMotivoInativacao()))
                .usuario(usuario)
                .usuarioInativacao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.I)
                .build();
        historicoRepository.save(historico);
    }

}
