package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    public static final int QUANTIDADE_CARACTERES_SENHA = 6;
    public static final int RADIX = 36;

    @Getter
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public Usuario findById(int id) {
        return repository
                .findComplete(id)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    //FIXME refatorar esse método
    public List<CidadeResponse> findCidadesByUsuario(int usuarioId) {
        Usuario usuario = repository.findComCidade(usuarioId)
                .orElse(null);
        if (usuario != null) {
            return usuario.getCidades()
                    .stream()
                    .map(c -> CidadeResponse.parse(c.getCidade()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public Usuario findComHierarquia(int id) {
        return repository
                .findComHierarquia(id)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public UsuarioDto findByEmail(String email) {
        Usuario usuario = repository
                .findByEmail(email)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
        return UsuarioDto.parse(usuario);
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtros.toPredicate();
        return repository.findAll(predicate.build(), pageRequest);
    }

    public UsuarioDto saveUsuarioCidades(UsuarioCidadeSaveDto usuarioCidadeSaveDto) {
        Usuario usuario = findById(usuarioCidadeSaveDto.getUsuarioId());
        adicionarCidadeParaUsuario(usuarioCidadeSaveDto, usuario);
        return UsuarioDto.parse(repository.save(usuario));
    }

    private void adicionarCidadeParaUsuario(UsuarioCidadeSaveDto usuarioCidadeSaveDto, Usuario usuario) {
        usuarioCidadeSaveDto.getCidadesId().forEach(idCidade -> usuario.adicionarCidade(
                criarUsuarioCidade(usuario, idCidade)));
    }

    private UsuarioCidade criarUsuarioCidade(Usuario usuario, Integer idCidade) {
        return UsuarioCidade.criar(usuario, idCidade, autenticacaoService.getUsuarioId());
    }

    public UsuarioDto saveUsuarioHierarquia(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto) {
        Usuario usuario = findComHierarquia(usuarioHierarquiaSaveDto.getUsuarioId());
        removerUsuarioSuperior(usuarioHierarquiaSaveDto, usuario);
        adicionarUsuarioSuperior(usuarioHierarquiaSaveDto, usuario);
        return UsuarioDto.parse(repository.save(usuario));
    }

    private void adicionarUsuarioSuperior(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto, Usuario usuario) {
        usuarioHierarquiaSaveDto.getHierarquiasId()
                .forEach(idHierarquia -> usuario.adicionarHierarquia(criarUsuarioHierarquia(usuario, idHierarquia)));
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idHierarquia) {
        return UsuarioHierarquia.criar(usuario, idHierarquia, autenticacaoService.getUsuarioId());
    }

    private void removerUsuarioSuperior(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto, Usuario usuario) {
        usuario.getUsuariosHierarquia()
                .removeIf(h -> !usuarioHierarquiaSaveDto.getHierarquiasId().contains(h.getUsuarioSuperiorId()));
    }

    public List<Integer> getIdDosUsuariosSubordinados(Integer usuarioId, Boolean incluirProprio) {
        List<Integer> usuariosSubordinados = repository.getUsuariosSubordinados(usuarioId);
        if (incluirProprio) {
            usuariosSubordinados.add(usuarioId);
        }
        return usuariosSubordinados;
    }

    public UsuarioDto save(UsuarioDto usuarioDto) {
        Usuario usuario = Usuario.parse(usuarioDto);
        validarCpfExistente(usuario);
        validarEmailExistente(usuario);
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
        if (usuario.isNovoCadastro()) {
            String senhaDescriptografada = getSenhaRandomica(QUANTIDADE_CARACTERES_SENHA);
            usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setAlterarSenha(Eboolean.V);
            usuario.setSituacao(ESituacao.A);
            usuario.setUsuarioCadastro(new Usuario(autenticacaoService.getUsuarioId()));
            usuario = repository.save(usuario);
            enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            return UsuarioDto.parse(usuario);
        }
        return UsuarioDto.parse(repository.save(usuario));
    }

    public void enviarEmailDadosDeAcesso(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail());
        context.setVariable("senha", senhaDescriptografada);

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Nova Conta",
                "confirmacao-cadastro",
                context);
    }

    public String getSenhaRandomica(int size) {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX);
        return tag.substring(0, size);
    }

    private void validarCpfExistente(Usuario usuario) {
        repository
                .findTop1UsuarioByCpf(usuario.getCpf())
                .ifPresent(u -> {
                    if (usuario.isNovoCadastro() || !u.getId().equals(usuario.getId())) {
                        throw new ValidacaoException("Cpf já cadastrado.");
                    }
                });
    }

    private void validarEmailExistente(Usuario usuario) {
        repository
                .findTop1UsuarioByEmailIgnoreCase(usuario.getEmail())
                .ifPresent(u -> {
                    if (usuario.isNovoCadastro() || !u.getId().equals(usuario.getId())) {
                        throw new ValidacaoException("Email já cadastrado.");
                    }
                });
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

    public List<UsuarioDto> getUsuariosFiltros(UsuarioFiltrosDto usuarioFiltrosDto) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate()
                .comEmpresas(usuarioFiltrosDto.getEmpresasIds())
                .comUnidadesNegocio(usuarioFiltrosDto.getUnidadesNegocioIds())
                .comNivel(usuarioFiltrosDto.getCodigoNivelList())
                .comCargo(usuarioFiltrosDto.getCodigoCargoList())
                .comDepartamento(usuarioFiltrosDto.getCodigoDepartamentoList())
                .comCidade(usuarioFiltrosDto.getCidadesIds())
                .comIds(usuarioFiltrosDto.getUsuariosAAsNacionais())
                .isAtivo();

        List<Usuario> usuarioList = repository.getUsuariosFilter(usuarioPredicate.build());

        return usuarioList.stream()
                .map(UsuarioDto::parse)
                .collect(Collectors.toList());
    }
}
