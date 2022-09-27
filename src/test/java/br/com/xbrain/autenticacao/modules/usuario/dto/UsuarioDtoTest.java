package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.VAREJO_VENDEDOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.VAREJO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioDtoTest {

    @Test
    public void convertFrom_deveRetornarFeeders_quandoCadastrarUsuarioNivelMso() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL));
    }

    @Test
    public void convertFrom_deveRetornarFeedersVazio_quandoCadastradoUsuarioComOutroNilvel() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoOuvidoria()))
            .extracting("nome", "cpf", "tiposFeeder")
            .containsExactly("OUVIDORIA NAO FEEDER", "286.250.583-88", Set.of());
    }

    @Test
    public void convertFrom_deveRetornarNivelId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "nivelId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), 2);
    }

    @Test
    public void convertFrom_naoDeveRetornarNivelId_quandoNaoPassadoNoDto() {
        var usuarioSemNivelId = umUsuarioDtoMso();
        usuarioSemNivelId.setNivelId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemNivelId))
            .extracting("nome", "cpf", "tiposFeeder", "nivelId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(), null);
    }

    @Test
    public void convertFrom_deveRetornarOrganizacaoEmpresaId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "organizacaoEmpresa")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL),
                new OrganizacaoEmpresa(1));
    }

    @Test
    public void convertFrom_naoDeveRetornarOrganizacaoEmpresaId_quandoNaoPassadoNoDto() {
        var usuarioSemOrganizacaoEmpresaId = umUsuarioDtoMso();
        usuarioSemOrganizacaoEmpresaId.setOrganizacaoEmpresaId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemOrganizacaoEmpresaId))
            .extracting("nome", "cpf", "tiposFeeder", "organizacaoEmpresa")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }

    @Test
    public void convertFrom_deveRetornarUsuarioCadastroId_quandoPassadoNoDto() {
        assertThat(UsuarioDto.convertFrom(umUsuarioDtoMso()))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastro")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), new Usuario(1));
    }

    @Test
    public void convertFrom_naoDeveRetornarUsuarioCadastroId_quandoNaoPassadoNoDto() {
        var usuarioSemUsuarioCadastroId = umUsuarioDtoMso();
        usuarioSemUsuarioCadastroId.setUsuarioCadastroId(null);

        assertThat(UsuarioDto.convertFrom(usuarioSemUsuarioCadastroId))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastro")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }

    @Test
    public void of_deveRetornarUsuarioCadastroId_quandoPassado() {
        assertThat(UsuarioDto.of(umUsuarioMso()))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastroId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), 101112);
    }

    @Test
    public void of_naoDeveRetornarUsuarioCadastroId_quandoNulo() {
        var usuarioComUsuarioCadastroNulo = umUsuarioMso();
        usuarioComUsuarioCadastroNulo.setUsuarioCadastro(null);

        assertThat(UsuarioDto.of(usuarioComUsuarioCadastroNulo))
            .extracting("nome", "cpf", "tiposFeeder", "usuarioCadastroId")
            .containsExactly("MSO FEEDER", "873.616.099-70", Set.of(EMPRESARIAL, RESIDENCIAL), null);
    }

    @Test
    public void of_deveRetornarUsuarioDto_quandoSolicitado() {
        var atual = UsuarioDto.of(umUsuarioCompleto(
            ESituacao.I, VAREJO_VENDEDOR, 120, VAREJO, CodigoDepartamento.COMERCIAL, ECanal.VAREJO));

        var esperado = UsuarioDto
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .loginNetSales("login123")
            .cargoId(120)
            .cargoCodigo(VAREJO_VENDEDOR)
            .cargoQuantidadeSuperior(50)
            .nivelId(5)
            .nivelCodigo(VAREJO)
            .departamentoId(1)
            .organizacaoEmpresaId(1)
            .unidadeNegocioId(1)
            .unidadesNegociosId(List.of(1))
            .empresasId(List.of(1))
            .hierarquiasId(List.of(65))
            .canais(Set.of(ECanal.VAREJO))
            .recuperarSenhaTentativa(0)
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void convertFrom_deveRetornarUsuario_quandoSolicitado() {
        var atual = UsuarioDto.convertFrom(umUsuarioCompletoDto());

        var esperado = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(1)
                .codigo(VAREJO_VENDEDOR)
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(1)
                .codigo(CodigoDepartamento.VAREJO)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .id(1)
                .nome("EMPRESA UM")
                .build()))
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .id(1)
                .razaoSocial("Thiago teste")
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .usuarioCadastro(Usuario
                .builder()
                .id(1)
                .nome("Thiago teste")
                .build())
            .usuariosHierarquia(null)
            .situacao(ESituacao.A)
            .cidades(Set.of())
            .canais(Set.of())
            .usuariosHierarquia(Set.of())
            .tiposFeeder(Set.of())
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void convertFrom_deveRetornarUsuario_seOrganizacaoIsNotEmpty() {
        var atual = UsuarioDto.convertFrom(umUsuarioOrganizacao());

        var esperado = Usuario
            .builder()
            .id(1)
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .id(1)
                .razaoSocial("Thiago teste")
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .cargo(Cargo
                .builder()
                .id(1)
                .codigo(VAREJO_VENDEDOR)
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(1)
                .codigo(CodigoDepartamento.VAREJO)
                .nome("DEPARTAMENTO UM")
                .build())
            .usuariosHierarquia(null)
            .cidades(Set.of())
            .usuariosHierarquia(Set.of())
            .tiposFeeder(Set.of())
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void convertFrom_deveRetornarUsuario_seOrganizacaoEmpresaIsNotEmpty() {
        var atual = UsuarioDto.convertFrom(umUsuarioOrganizacaoEmpresa());

        var esperado = Usuario
            .builder()
            .id(1)
            .organizacaoEmpresa(OrganizacaoEmpresa
                .builder()
                .id(1)
                .razaoSocial("Thiago teste")
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .cargo(Cargo
                .builder()
                .id(1)
                .codigo(VAREJO_VENDEDOR)
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(1)
                .codigo(CodigoDepartamento.VAREJO)
                .nome("DEPARTAMENTO UM")
                .build())
            .usuariosHierarquia(null)
            .cidades(Set.of())
            .usuariosHierarquia(Set.of())
            .tiposFeeder(Set.of())
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void convertFrom_deveRetornarUsuario_seUsuarioCadastroIsNotEmpty() {
        var atual = UsuarioDto.convertFrom(umUsuarioComUsuarioCadastro());

        var esperado = Usuario
            .builder()
            .id(1)
            .cargo(Cargo
                .builder()
                .id(1)
                .codigo(VAREJO_VENDEDOR)
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(1)
                .codigo(CodigoDepartamento.VAREJO)
                .nome("DEPARTAMENTO UM")
                .build())
            .usuarioCadastro(Usuario
                .builder()
                .id(1)
                .build())
            .cidades(Set.of())
            .usuariosHierarquia(Set.of())
            .tiposFeeder(Set.of())
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void of_deveRetornarUsuarioDto_quandoSolicitado_sePermiteEditarCompletoIsTrue() {
        var atual = UsuarioDto.of(umUsuarioCompleto(ESituacao.I, VAREJO_VENDEDOR, 120, VAREJO,
            CodigoDepartamento.COMERCIAL, ECanal.VAREJO), true);

        var esperado = UsuarioDto
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .loginNetSales("login123")
            .cargoId(120)
            .cargoCodigo(VAREJO_VENDEDOR)
            .cargoQuantidadeSuperior(50)
            .nivelId(5)
            .nivelCodigo(VAREJO)
            .departamentoId(1)
            .organizacaoEmpresaId(1)
            .unidadeNegocioId(1)
            .unidadesNegociosId(List.of(1))
            .empresasId(List.of(1))
            .hierarquiasId(List.of(65))
            .canais(Set.of(ECanal.VAREJO))
            .recuperarSenhaTentativa(0)
            .permiteEditarCompleto(true)
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    @Test
    public void parse_deveRetornarUsuarioDto_quandoSolicitado() {
        var atual = UsuarioDto.parse(umUsuarioMqRequest());

        var esperado = UsuarioDto
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .loginNetSales("login123")
            .canais(Set.of(ECanal.VAREJO))
            .agenteAutorizadoId(111)
            .usuarioCadastroId(2222)
            .unidadesNegociosId(List.of())
            .empresasId(List.of())
            .recuperarSenhaTentativa(0)
            .build();

        assertThat(atual).isEqualToComparingFieldByField(esperado);
    }

    private Usuario umUsuarioCompleto(ESituacao situacao, CodigoCargo codigoCargo, Integer idCargo,
                                      CodigoNivel nivel, CodigoDepartamento departamento, ECanal canal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(situacao)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(idCargo)
                .codigo(codigoCargo)
                .quantidadeSuperior(50)
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(nivel)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(1)
                .codigo(departamento)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .id(1)
                .nome("EMPRESA UM")
                .build()))
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .id(1)
                .razaoSocial("Thiago teste")
                .nivel(Nivel
                    .builder()
                    .id(5)
                    .codigo(VAREJO)
                    .build())
                .build())
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(canal)
            )
        );

        return usuario;
    }

    private UsuarioDto umUsuarioCompletoDto() {
        return UsuarioDto
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .loginNetSales("login123")
            .cargoId(1)
            .cargoCodigo(VAREJO_VENDEDOR)
            .cargoQuantidadeSuperior(50)
            .nivelId(5)
            .nivelCodigo(VAREJO)
            .departamentoId(1)
            .organizacaoEmpresaId(1)
            .usuarioCadastroId(1)
            .unidadesNegociosId(List.of(1))
            .empresasId(List.of(1))
            .hierarquiasId(null)
            .canais(Collections.emptySet())
            .cidadesId(null)
            .hierarquiasId(null)
            .situacao(ESituacao.A)
            .recuperarSenhaTentativa(0)
            .build();
    }

    private UsuarioDto umUsuarioOrganizacao() {
        return UsuarioDto
            .builder()
            .id(1)
            .organizacaoEmpresaId(1)
            .cargoId(1)
            .departamentoId(1)
            .cidadesId(null)
            .hierarquiasId(null)
            .build();
    }

    private UsuarioDto umUsuarioOrganizacaoEmpresa() {
        return UsuarioDto
            .builder()
            .id(1)
            .organizacaoEmpresaId(1)
            .cargoId(1)
            .departamentoId(1)
            .cidadesId(null)
            .hierarquiasId(null)
            .build();
    }

    private UsuarioDto umUsuarioComUsuarioCadastro() {
        return UsuarioDto
            .builder()
            .id(1)
            .usuarioCadastroId(1)
            .cargoId(1)
            .departamentoId(1)
            .cidadesId(null)
            .hierarquiasId(null)
            .build();
    }

    private UsuarioMqRequest umUsuarioMqRequest() {
        return UsuarioMqRequest.builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.I)
            .loginNetSales("login123")
            .canais(Set.of(ECanal.VAREJO))
            .agenteAutorizadoId(111)
            .usuarioCadastroId(2222)
            .cargo(CodigoCargo.AGENTE_AUTORIZADO_VENDEDOR_D2D)
            .build();
    }
}
