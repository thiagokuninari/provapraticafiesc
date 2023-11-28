package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE_SUPORTE_VENDAS;

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

    @Column(name = "CODIGO", length = 80, nullable = false)
    private String codigo;

    @JoinColumn(name = "FK_NIVEL", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_NIVEL_ORGANIZACAO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Nivel nivel;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacaoOrganizacaoEmpresa situacao;

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @JoinColumn(name = "FK_USUARIO_CADASTRO",
        foreignKey = @ForeignKey(name = "FK_USUARIO_CADASTRO_ORG"), referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @Column(name = "CANAL")
    @Enumerated(EnumType.STRING)
    private ECanal canal;

    public static OrganizacaoEmpresa of(OrganizacaoEmpresaRequest request, Integer usuarioId, Nivel nivel) {
        return OrganizacaoEmpresa.builder()
            .nome(request.getNome())
            .nivel(nivel)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(new Usuario(usuarioId))
            .codigo(request.getCodigo())
            .canal(request.getCanal())
            .build();
    }

    public void of(OrganizacaoEmpresaRequest request, Nivel nivel) {
        this.nome = request.getNome();
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

    public Boolean isAtivo() {
        return situacao == ESituacaoOrganizacaoEmpresa.A;
    }

    public Boolean isSuporteVendas() {
        return nivel != null
            && nivel.getCodigo() == BACKOFFICE_SUPORTE_VENDAS;
    }

    public OrganizacaoEmpresa(Integer id) {
        this.id = id;
    }
}
