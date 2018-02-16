package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;

import java.util.List;

public class UsuarioPredicate {

    private QUsuario usuario = QUsuario.usuario;
    private BooleanBuilder builder;

    public UsuarioPredicate() {
        this.builder = new BooleanBuilder();
    }

    public UsuarioPredicate comNome(String nome) {
        if (nome != null) {
            builder.and(usuario.nome.likeIgnoreCase(nome));
        }
        return this;
    }

    public UsuarioPredicate comCpf(String cpf) {
        if (cpf != null) {
            builder.and(
                    Expressions.stringTemplate("REGEXP_REPLACE({0}, '[^0-9]+', '')", usuario.cpf)
                            .like("%" + StringUtil.getOnlyNumbers(cpf) + "%"));
        }
        return this;
    }

    public UsuarioPredicate comEmpresas(List<Integer> empresasIds) {
        if (empresasIds.size() > 0) {
            builder.and(usuario.empresas.any().id.in(empresasIds));
        }
        return this;
    }

    public UsuarioPredicate comUnidadesNegocio(List<Integer> unidadesNegocioIds) {
        if (unidadesNegocioIds.size() > 0) {
            builder.and(usuario.unidadesNegocios.any().id.in(unidadesNegocioIds));
        }
        return this;
    }

    public UsuarioPredicate isAtivo(Eboolean ativo) {
        if (ativo == Eboolean.V) {
            builder.and(usuario.situacao.eq(ESituacao.A));
        }
        return this;
    }

    public UsuarioPredicate comNivel(List<CodigoNivel> codigoNivelList) {
        if (codigoNivelList.size() > 0) {
            builder.and(usuario.cargo.nivel.codigo.in(codigoNivelList));
        }
        return this;
    }

    public UsuarioPredicate comCargo(List<CodigoCargo> codigoCargoList) {
        if (codigoCargoList.size() > 0) {
            builder.and(usuario.cargo.codigo.in(codigoCargoList));
        }
        return this;
    }

    public UsuarioPredicate comDepartamento(List<CodigoDepartamento> codigoDepartamentoList) {
        if (codigoDepartamentoList.size() > 0) {
            builder.and(usuario.departamento.codigo.in(codigoDepartamentoList));
        }
        return this;
    }

    public UsuarioPredicate comCidade(List<Integer> cidadesIds) {
        if (cidadesIds.size() > 0) {
            builder.and(usuario.cidades.any().cidade.id.in(cidadesIds));
        }
        return this;
    }

    public UsuarioPredicate comIds(List<Integer> usuariosIds) {
        if (usuariosIds.size() > 0) {
            builder.and(usuario.id.in(usuariosIds));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
