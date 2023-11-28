package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.MSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USUARIO")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@DynamicUpdate
public class Usuario {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_USUARIO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @NotNull
    @Email
    @Size(max = 80)
    @Column(name = "EMAIL_01", nullable = false, length = 80)
    private String email;

    @Email
    @Size(max = 80)
    @Column(name = "EMAIL_02", length = 80)
    private String email02;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "TELEFONE_01")
    private String telefone;

    @Column(name = "TELEFONE_02")
    private String telefone02;

    @Column(name = "TELEFONE_03")
    private String telefone03;

    @Column(name = "IMEI")
    private Long imei;

    @NotNull
    @CPF
    @Column(name = "CPF", length = 14)
    private String cpf;

    @Size(max = 25)
    @Column(name = "RG", length = 25)
    private String rg;

    @Size(max = 30)
    @Column(name = "ORGAO_EXPEDIDOR", length = 30)
    private String orgaoExpedidor;

    @Size(max = 120)
    @Column(name = "LOGIN_NET_SALES", length = 120)
    private String loginNetSales;

    @Column(name = "NASCIMENTO")
    private LocalDateTime nascimento;

    @NotAudited
    @JsonIgnore
    @NotEmpty
    @JoinTable(name = "USUARIO_UNIDADE_NEGOCIO", joinColumns = {
        @JoinColumn(name = "FK_USUARIO", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_USUARIO_UNID_NEGOCIO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_UNIDADE_NEGOCIO", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_USUARIO_UNID_NEGOCIO"))})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<UnidadeNegocio> unidadesNegocios;

    @NotAudited
    @OrderBy("id")
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UsuarioCidade> cidades = new HashSet<>();

    @NotAudited
    @Basic(optional = false)
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Configuracao configuracao;

    @NotAudited
    @JsonIgnore
    @NotEmpty
    @JoinTable(name = "USUARIO_EMPRESA", joinColumns = {
        @JoinColumn(name = "FK_USUARIO", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_USUARIO_EMPRESA_USUARIO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_EMPRESA", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_USUARIO_EMPRESA_EMPRESA"))})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Empresa> empresas;

    @NotAudited
    @OrderBy("id")
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioHierarquia> usuariosHierarquia = new HashSet<>();

    @NotNull
    @JoinColumn(name = "FK_CARGO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO_CARGO"), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cargo cargo;

    @NotNull
    @JoinColumn(name = "FK_DEPARTAMENTO", referencedColumnName = "ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_USUARIO_DEPART"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Departamento departamento;

    @NotAudited
    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;

    @NotAudited
    @Column(name = "DATA_ULTIMO_ACESSO")
    private LocalDateTime dataUltimoAcesso;

    @NotAudited
    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO_USUARIO_CADASTRO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @JsonIgnore
    @Column(name = "SENHA", nullable = false, updatable = false, length = 80)
    private String senha;

    @Column(name = "ALTERAR_SENHA", nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;

    @NotAudited
    @Column(name = "RECUPERAR_SENHA_HASH")
    private String recuperarSenhaHash;

    @NotAudited
    @Column(name = "RECUPERAR_SENHA_TENTATIVA")
    @NotNull
    private Integer recuperarSenhaTentativa = 0;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "FOTO_DIRETORIO")
    private String fotoDiretorio;

    @Column(name = "FOTO_NOME_ORIGINAL")
    private String fotoNomeOriginal;

    @Column(name = "FOTO_CONTENT_TYPE")
    private String fotoContentType;

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UsuarioHistorico> historicos;

    @NotAudited
    @CollectionTable(name = "USUARIO_CANAL", joinColumns = @JoinColumn(name = "FK_USUARIO"))
    @Column(name = "CANAL", nullable = false, length = 20)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<ECanal> canais;

    @Column(name = "TIPO_CANAL")
    @Enumerated(EnumType.STRING)
    private ETipoCanal tipoCanal;

    @JoinColumn(name = "FK_ORGANIZACAO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO_ORGANIZACAO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Organizacao organizacao;

    @Column(name = "URL_LOJA_BASE", length = 200)
    private String urlLojaBase;

    @Column(name = "URL_LOJA_PROSPECT", length = 200)
    private String urlLojaProspect;

    @Column(name = "URL_LOJA_PROSPECT_NEXTEL", length = 200)
    private String urlLojaProspectNextel;

    @Column(name = "CUPOM_LOJA", length = 100)
    private String cupomLoja;

    @Transient
    private List<Integer> hierarquiasId;

    @Transient
    private List<Integer> cidadesId;

    @Transient
    private Integer agenteAutorizadoId;

    @Transient
    private boolean isAtualizarSocioPrincipal;

    @Transient
    private List<Integer> agentesAutorizadosIds;

    @Transient
    private List<Integer> antigosSociosPrincipaisIds;

    @Transient
    private String senhaDescriptografada;

    public Usuario(Integer id) {
        this.id = id;
    }

    public Usuario(Collection<Empresa> empresas, Collection<UnidadeNegocio> unidadeNegocios) {
        this.empresas = new ArrayList<>(empresas);
        this.unidadesNegocios = new ArrayList<>(unidadeNegocios);
    }

    public Usuario(Integer id, String email) {
        this.id = id;
        this.email = email;
    }

    public static Usuario parse(UsuarioMqRequest usuarioMqRequest) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioMqRequest, usuario);
        usuario.setUsuarioCadastro(new Usuario(usuarioMqRequest.getUsuarioCadastroId()));
        return usuario;
    }

    public boolean isNovoCadastro() {
        return id == null;
    }

    public Usuario forceLoad() {
        empresas.size();
        cidades.size();
        usuariosHierarquia.forEach(u -> u.getUsuarioSuperior().getId());
        cargo.getId();
        unidadesNegocios.size();
        departamento.getId();
        canais.size();
        return this;
    }

    public List<Integer> getEmpresasId() {
        return empresas != null && Hibernate.isInitialized(empresas)
            ? empresas
            .stream()
            .map(Empresa::getId)
            .collect(Collectors.toList())
            : null;
    }

    public void setEmpresasId(List<Integer> ids) {
        if (ids != null) {
            empresas = ids
                .stream()
                .map(Empresa::new)
                .collect(Collectors.toList());
        }
    }

    public List<String> getEmpresasNome() {
        return empresas != null && Hibernate.isInitialized(empresas)
            ? empresas
            .stream()
            .map(Empresa::getNome)
            .collect(Collectors.toList())
            : null;
    }

    public List<Integer> getUnidadesNegociosId() {
        if (!Hibernate.isInitialized(unidadesNegocios)) {
            Hibernate.initialize(unidadesNegocios);
        }

        return unidadesNegocios != null
            ? unidadesNegocios
            .stream()
            .map(UnidadeNegocio::getId)
            .collect(Collectors.toList())
            : null;
    }

    public void setUnidadesNegociosId(List<Integer> ids) {
        if (ids != null) {
            unidadesNegocios = ids
                .stream()
                .map(UnidadeNegocio::new)
                .collect(Collectors.toList());
        }
    }

    public Set<UsuarioCidade> getCidades() {
        return Collections.unmodifiableSet(this.cidades);
    }

    public void adicionarCidade(UsuarioCidade usuarioCidade) {
        if (CollectionUtils.isEmpty(this.cidades)) {
            this.cidades = new HashSet<>();
        }
        this.cidades.add(usuarioCidade);
    }

    public void adicionarHierarquia(UsuarioHierarquia usuarioHierarquia) {
        if (!this.usuariosHierarquia.contains(usuarioHierarquia)) {
            this.usuariosHierarquia.add(usuarioHierarquia);
        }
    }

    public void configurarRamal(Integer ramal) {
        if (this.configuracao != null) {
            this.configuracao.setRamal(ramal);
        }
    }

    public void tratarEmails() {
        this.email = this.email.trim().toUpperCase();

        if (!StringUtils.isEmpty(this.email02)) {
            this.email02 = this.email02.trim().toUpperCase();
        }
    }

    public void removerCaracteresDoCpf() {
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("[.-]", "");
        }
    }

    public Integer getCargoId() {
        return this.cargo != null ? this.cargo.getId() : null;
    }

    public CodigoCargo getCargoCodigo() {
        return this.cargo != null ? this.cargo.getCodigo() : null;
    }

    public Set<Integer> getCargosSuperioresId() {
        return !ObjectUtils.isEmpty(cargo) && !ObjectUtils.isEmpty(cargo.getSuperiores())
            ? cargo.getSuperiores().stream().map(Cargo::getId).collect(Collectors.toSet())
            : null;
    }

    public Set<CodigoCargo> getCodigoCargoByCanais() {
        if (!ObjectUtils.isEmpty(canais)) {
            if (this.canais.size() > 1) {
                return Set.of(OPERACAO_TELEVENDAS, VENDEDOR_OPERACAO);
            } else if (this.canais.contains(ECanal.ATIVO_PROPRIO)) {
                return Set.of(OPERACAO_TELEVENDAS);
            }
        }
        return Set.of(VENDEDOR_OPERACAO);
    }

    public Integer getDepartamentoId() {
        return this.departamento != null ? this.departamento.getId() : null;
    }

    public CodigoDepartamento getDepartamentoCodigo() {
        return this.departamento != null ? this.departamento.getCodigo() : null;
    }

    public Integer getNivelId() {
        if (this.cargo != null && this.cargo.getNivel() != null) {
            return this.cargo.getNivel().getId();
        }
        return null;
    }

    public CodigoNivel getNivelCodigo() {
        if (this.cargo != null && this.cargo.getNivel() != null) {
            return this.cargo.getNivel().getCodigo();
        }
        return null;
    }

    public String getNivelNome() {
        if (!ObjectUtils.isEmpty(this.cargo) && !ObjectUtils.isEmpty(this.cargo.getNivel())) {
            return this.cargo.getNivel().getNome();
        }
        return null;
    }

    public List<CodigoEmpresa> getCodigosEmpresas() {
        if (!CollectionUtils.isEmpty(empresas)) {
            return empresas.stream().map(Empresa::getCodigo).collect(Collectors.toList());
        }
        return null;
    }

    public List<CodigoUnidadeNegocio> getCodigosUnidadesNegocio() {
        if (!CollectionUtils.isEmpty(unidadesNegocios)) {
            return unidadesNegocios.stream().map(UnidadeNegocio::getCodigo).collect(Collectors.toList());
        }
        return List.of();
    }

    public String getLogin() {
        return id + "-" + email;
    }

    public boolean isEmpty() {
        return id == null && nome == null && cpf == null && email == null;
    }

    public boolean hasUsuarioCadastro() {
        return usuarioCadastro != null && !usuarioCadastro.isEmpty();
    }

    public boolean hasConfiguracao() {
        return configuracao != null;
    }

    public boolean isUsuarioEquipeVendas() {
        return !ObjectUtils.isEmpty(cargo) && !ObjectUtils.isEmpty(cargo.getCodigo())
            && List.of(VENDEDOR_OPERACAO, OPERACAO_EXECUTIVO_VENDAS, ASSISTENTE_OPERACAO, SUPERVISOR_OPERACAO,
                OPERACAO_TELEVENDAS)
            .contains(cargo.getCodigo());
    }

    public Integer getRecuperarSenhaTentativa() {
        return recuperarSenhaTentativa == null ? 0 : recuperarSenhaTentativa;
    }

    @JsonIgnore
    public Set<String> getCanaisString() {
        return canais.stream().map(Enum::toString).collect(Collectors.toSet());
    }

    public boolean isAgenteAutorizado() {
        return !ObjectUtils.isEmpty(cargo) && !ObjectUtils.isEmpty(cargo.getNivel())
            && cargo.getNivel().getCodigo().equals(CodigoNivel.AGENTE_AUTORIZADO);
    }

    public boolean isSocioPrincipal() {
        return Objects.nonNull(this.cargo)
            && Objects.equals(this.cargo.getCodigo(), AGENTE_AUTORIZADO_SOCIO);
    }

    public boolean isBackoffice() {
        return Objects.nonNull(cargo) && Objects.nonNull(cargo.getNivel())
            && cargo.getNivel().getCodigo().equals(CodigoNivel.BACKOFFICE);
    }

    public void adicionarHistorico(UsuarioHistorico historico) {
        if (Objects.isNull(this.historicos)) {
            this.historicos = new ArrayList<>();
        }

        this.historicos.add(historico);
    }

    @JsonIgnore
    public boolean permiteEditar(UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.isUsuarioEquipeVendas()) {
            return Objects.equals(getCargoCodigo(), VENDEDOR_OPERACAO);
        }
        return usuarioAutenticado.isXbrain() || usuarioAutenticado.getId() != id;
    }

    @JsonIgnore
    public boolean isAtivo() {
        return ESituacao.A.equals(situacao);
    }

    @JsonIgnore
    public boolean isCargo(CodigoCargo codigoCargo) {
        return cargo.getCodigo().equals(codigoCargo);
    }

    public boolean hasCanal(ECanal canal) {
        return Objects.nonNull(canais) && canais.stream().anyMatch(c -> Objects.equals(c, canal));
    }

    public boolean hasLoginNetSales() {
        return !StringUtils.isEmpty(loginNetSales);
    }

    public void verificarPermissaoCargoSobreCanais() {
        if (!ObjectUtils.isEmpty(canais) && canais.stream().noneMatch(cargo::hasPermissaoSobreOCanal)) {
            throw new ValidacaoException("Usuário sem permissão para o cargo com os canais.");
        }
    }

    public static Set<Integer> convertFrom(Set<Usuario> usuarios) {
        return usuarios.stream()
            .map(Usuario::getId)
            .collect(Collectors.toSet());
    }

    public static Set<Usuario> of(List<Integer> usuarios) {
        return usuarios.stream()
            .map(Usuario::new)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean isOperadorTelevendasAtivoLocal() {
        return isCargo(OPERACAO_TELEVENDAS)
            && hasCanal(ECanal.ATIVO_PROPRIO);
    }

    public boolean isXbrain() {
        return XBRAIN == getNivelCodigo();
    }

    public boolean isXbrainOuMso() {
        return isXbrain() || isMso();
    }

    public boolean isMso() {
        return MSO == getNivelCodigo();
    }

    @JsonIgnore
    public boolean isCanalAtivoLocalRemovido(Set<ECanal> canaisNovos) {
        return isCanalRemovido(ECanal.ATIVO_PROPRIO, canaisNovos);
    }

    @JsonIgnore
    public boolean isCanalAgenteAutorizadoRemovido(Set<ECanal> canaisNovos) {
        return isCanalRemovido(ECanal.AGENTE_AUTORIZADO, canaisNovos);
    }

    private boolean isCanalRemovido(ECanal canalValidado, Set<ECanal> canaisNovos) {
        if (canalValidado == null || ObjectUtils.isEmpty(canais)) {
            return false;
        }

        return canais.contains(canalValidado)
            && (ObjectUtils.isEmpty(canaisNovos) || !canaisNovos.contains(canalValidado));
    }

    @JsonIgnore
    public boolean isCoordenadorOuSupervisorOperacao() {
        return cargo.getCodigo() == COORDENADOR_OPERACAO || cargo.getCodigo() == SUPERVISOR_OPERACAO;
    }

    @JsonIgnore
    public boolean isNivelOperacao() {
        return !ObjectUtils.isEmpty(cargo) && !ObjectUtils.isEmpty(cargo.getNivel())
            && cargo.getNivel().getCodigo() == CodigoNivel.OPERACAO;
    }
}
