package br.com.xbrain.autenticacao.modules.site.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SITE")
@EqualsAndHashCode(of = "id")
public class Site {

    @Id
    @SequenceGenerator(name = "SEQ_SITE", sequenceName = "SEQ_SITE", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SITE", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "TIME_ZONE", length = 25, nullable = false)
    @Enumerated(EnumType.STRING)
    private ETimeZone timeZone;

    @JoinTable(name = "SITE_ESTADO", joinColumns = {
        @JoinColumn(name = "FK_SITE", foreignKey = @ForeignKey(name = "FK_SITE_ESTADO_SITE"),
            referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "FK_ESTADO", foreignKey = @ForeignKey(name = "FK_SITE_ESTADO_ESTADO"),
            referencedColumnName = "ID")})
    @ManyToMany
    private Set<Uf> estados;

    @JoinTable(name = "SITE_CIDADE", joinColumns = {
        @JoinColumn(name = "FK_SITE", foreignKey = @ForeignKey(name = "FK_SITE_CIDADE_SITE"),
            referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "FK_CIDADE", foreignKey = @ForeignKey(name = "FK_SITE_CIDADE_CIDADE"),
            referencedColumnName = "ID")})
    @ManyToMany
    private Set<Cidade> cidades;

    @JoinTable(name = "SITE_SUPERVISOR", joinColumns = {
        @JoinColumn(name = "FK_SITE", foreignKey = @ForeignKey(name = "FK_SITE_SUPERVISOR_SITE"),
            referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_SITE_SUPERVISOR_USU"),
            referencedColumnName = "id")})
    @ManyToMany
    private Set<Usuario> supervisores;

    @JoinTable(name = "SITE_COORDENADOR", joinColumns = {
        @JoinColumn(name = "FK_SITE", foreignKey = @ForeignKey(name = "FK_SITE_COORDENADOR_SITE"),
            referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_SITE_COORDENADOR_USU"),
            referencedColumnName = "id")})
    @ManyToMany
    private Set<Usuario> coordenadores;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "DISCADORA_ID")
    private Integer discadoraId;

    public void inativar() {
        situacao = ESituacao.I;
    }

    @PrePersist
    public void ativar() {
        situacao = ESituacao.A;
    }

    public static Site of(SiteRequest request) {
        return Site.builder()
            .id(request.getId())
            .nome(request.getNome())
            .estados(Uf.of(request.getEstadosIds()))
            .timeZone(request.getTimeZone())
            .supervisores(Usuario.of(request.getSupervisoresIds()))
            .coordenadores(Usuario.of(request.getCoordenadoresIds()))
            .cidades(Cidade.of(request.getCidadesIds()))
            .build();
    }

    public void update(SiteRequest request) {
        var site = of(request);
        BeanUtils.copyProperties(site, this, "situacao");
    }

}
