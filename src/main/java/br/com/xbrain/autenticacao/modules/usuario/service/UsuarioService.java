package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private UnidadeNegocioRepository unidadeNegocioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioMqSender usuarioMqSender;

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
        return repository.findComHierarquia(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.parse(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
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
        validar(usuario);
        if (usuario.isNovoCadastro()) {
            String senhaDescriptografada = getSenhaRandomica(QUANTIDADE_CARACTERES_SENHA);
            configurar(usuario, senhaDescriptografada);
            usuario = repository.save(usuario);
            enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            return UsuarioDto.parse(usuario);
        }
        return UsuarioDto.parse(repository.save(usuario));
    }

    private void configurar(Usuario usuario, String senhaDescriptografada) {
        usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSituacao(ESituacao.A);
        if (!usuario.hasUsuarioCadastro()) {
            usuario.setUsuarioCadastro(new Usuario(autenticacaoService.getUsuarioId()));
        }
    }

    @Transactional
    public void saveFromQueue(UsuarioMqRequest usuarioMqRequest) {
        UsuarioDto usuarioDto = UsuarioDto.parse(usuarioMqRequest);
        configurarUsuario(usuarioMqRequest, usuarioDto);
        usuarioDto = save(usuarioDto);
        usuarioMqSender.send(usuarioDto);
    }

    private void configurarUsuario(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        configurarCargo(usuarioMqRequest, usuarioDto);
        configurarDepartamento(usuarioMqRequest, usuarioDto);
        configurarNivel(usuarioMqRequest, usuarioDto);
        configurarUnidadesNegocio(usuarioMqRequest, usuarioDto);
        configurarEmpresas(usuarioMqRequest, usuarioDto);
    }

    private void configurarCargo(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Cargo cargo = cargoRepository.findByCodigo(usuarioMqRequest.getCargo());
        usuarioDto.setCargoId(cargo.getId());
    }

    private void configurarDepartamento(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Departamento departamento = departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento());
        usuarioDto.setDepartamentoId(departamento.getId());
    }

    private void configurarNivel(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Nivel nivel = nivelRepository.findByCodigo(usuarioMqRequest.getNivel());
        usuarioDto.setNivelId(nivel.getId());
    }

    private void configurarUnidadesNegocio(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        List<UnidadeNegocio> unidadesNegocios = unidadeNegocioRepository
                .findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        usuarioDto.setUnidadesNegociosId(unidadesNegocios.stream()
                .map(UnidadeNegocio::getId).collect(Collectors.toList()));
    }

    private void configurarEmpresas(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        List<Empresa> empresas = empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa());
        usuarioDto.setEmpresasId(empresas.stream().map(Empresa::getId).collect(Collectors.toList()));
    }

    private void validar(Usuario usuario) {
        validarCpfExistente(usuario);
        validarEmailExistente(usuario);
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
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

    public List<UsuarioResponse> getUsuariosByIds(List<Integer> idsUsuarios) {
        List<Usuario> usuarios = repository.findBySituacaoAndIdIn(ESituacao.A, idsUsuarios);
        return usuarios.stream()
                .map(UsuarioResponse::convertFrom)
                .collect(Collectors.toList());
    }
}
