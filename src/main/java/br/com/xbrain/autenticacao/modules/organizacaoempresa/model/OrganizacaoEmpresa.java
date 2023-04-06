package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANIZACAO_EMPRESA")
@EqualsAndHashCode(of = "id")
public class OrganizacaoEmpresa {

    @Id
    @SequenceGenerator(name = "SEQ_ORGANIZACAO_EMPRESA", sequenceName = "SEQ_ORGANIZACAO_EMPRESA", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ORGANIZACAO_EMPRESA", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "CODIGO", length = 80, nullable = false, unique = true)
    private String codigo;

    @Column(name = "CNPJ")
    private String cnpj;

    @JoinColumn(name = "FK_NIVEL", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_NIVEL_ORGANIZACAO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Nivel nivel;

    @ManyToMany
    @JoinTable(name = "ORGANIZACAO_MODALIDADE_EMPRESA", joinColumns = {
        @JoinColumn(name = "FK_ORGANIZACAO_EMPRESA", foreignKey = @ForeignKey(name = "FK_ORG_MOD_ORG_EMPRESA"),
            referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "FK_MODALIDADE_EMPRESA", foreignKey = @ForeignKey(name = "FK_ORG_MOD_MOD_EMPRESA"),
            referencedColumnName = "ID")})
    private List<ModalidadeEmpresa> modalidadesEmpresa;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacaoOrganizacaoEmpresa situacao;

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @JoinColumn(name = "FK_USUARIO_CADASTRO",
        foreignKey = @ForeignKey(name = "FK_USUARIO_CADASTRO_ORG"), referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    public static OrganizacaoEmpresa of(OrganizacaoEmpresaRequest request, Integer usuarioId, Nivel nivel,
                                        List<ModalidadeEmpresa> modalidadesEmpresa) {
        return OrganizacaoEmpresa.builder()
            .nome(request.getNome())
            .cnpj(request.getCnpjSemMascara())
            .nivel(nivel)
            .modalidadesEmpresa(modalidadesEmpresa)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(new Usuario(usuarioId))
            .codigo(request.getCodigo())
            .build();
    }

    public void of(OrganizacaoEmpresaRequest request, List<ModalidadeEmpresa> modalidades, Nivel nivel) {
        this.cnpj = request.getCnpjSemMascara();
        this.nome = request.getNome();
        this.modalidadesEmpresa = modalidades;
        this.nivel = nivel;
        this.codigo = request.getCodigo();
    }

    public void inativar() {
        situacao = ESituacaoOrganizacaoEmpresa.I;
    }

    public void ativar() {
        situacao = ESituacaoOrganizacaoEmpresa.A;
    }

    public Optional<NivelResponse> getNivelIdNome() {
        return Optional.ofNullable(nivel)
            .map(nivel -> new NivelResponse(nivel.getId(), nivel.getNome(), nivel.getCodigo().name()));
    }

    public List<SelectResponse> getModalidadesEmpresaIdNome() {
        if (!CollectionUtils.isEmpty(modalidadesEmpresa)) {
            return modalidadesEmpresa
                .stream()
                .map(modalidade -> new SelectResponse(modalidade.getId(), modalidade.getModalidadeEmpresa().name()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Boolean isAtivo() {
        return situacao == ESituacaoOrganizacaoEmpresa.A;
    }

    public String formataCnpj() {
        return CnpjUtil.formataCnpj(cnpj);
    }

    public OrganizacaoEmpresa(Integer id) {
        this.id = id;
    }
}
