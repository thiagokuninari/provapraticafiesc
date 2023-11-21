package br.com.xbrain.autenticacao.modules.organizacaoempresa.helper;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa.A;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;

public class OrganizacaoEmpresaHelper {

    public static OrganizacaoEmpresaRequest organizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Marcos AA")
            .situacao(A)
            .nivelId(1)
            .codigo("CODIGO")
            .build();
    }

    public static OrganizacaoEmpresa organizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .nome("Marcos AA")
            .id(2)
            .situacao(A)
            .build();
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsBytes(object);
    }

    public static Page<OrganizacaoEmpresa> organizacaoEmpresaPage() {
        return new PageImpl<>(List.of(umaOrganizacaoEmpresa(), umaOutraOrganizacaoEmpresa()));
    }

    public static OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .nome("Teste AA")
            .situacao(A)
            .codigo("codigo")
            .build();
    }

    public static OrganizacaoEmpresa umaOutraOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .id(2)
            .nome("Teste AA Dois")
            .nivel(umNivel())
            .situacao(A)
            .codigo("codigo2")
            .build();
    }

    public static List<OrganizacaoEmpresaResponse> umaListaOrganizacaoEmpresaResponse() {
        return List.of(OrganizacaoEmpresaResponse.builder()
                .id(1)
                .situacao(A)
                .build(),
            OrganizacaoEmpresaResponse.builder()
                .id(2)
                .situacao(A)
                .build());
    }

    public static List<OrganizacaoEmpresaResponse> umaListaOrganizacaoEmpresaResponseComNivel() {
        return List.of(OrganizacaoEmpresaResponse.builder()
                .id(1)
                .situacao(A)
                .nivel(umNivelResponse())
                .build(),
            OrganizacaoEmpresaResponse.builder()
                .id(2)
                .situacao(A)
                .nivel(umNivelResponseReceptivo())
                .build());
    }

    public static Nivel umNivel() {
        return Nivel.builder()
            .id(1)
            .nome("BACKOFFICE")
            .codigo(CodigoNivel.BACKOFFICE)
            .build();
    }

    public static Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(10)
            .nome("OPERACAO")
            .codigo(CodigoNivel.OPERACAO)
            .build();
    }

    public static Nivel umNivelBackoffice() {
        return Nivel.builder()
            .id(2)
            .nome("BACKOFFICE")
            .codigo(CodigoNivel.BACKOFFICE)
            .build();
    }

    public static Nivel umNivelReceptivo() {
        return Nivel.builder()
            .id(3)
            .nome("RECEPTIVO")
            .codigo(CodigoNivel.RECEPTIVO)
            .build();
    }

    public static NivelResponse umNivelResponse() {
        return NivelResponse.builder()
            .id(1)
            .nome("BACKOFFICE")
            .codigo(CodigoNivel.BACKOFFICE.name())
            .build();
    }

    public static NivelResponse umNivelResponseReceptivo() {
        return NivelResponse.builder()
            .id(2)
            .nome("RECEPTIVO")
            .codigo(CodigoNivel.RECEPTIVO.name())
            .build();
    }

    public static Nivel umNivelSuporteVendas() {
        return Nivel.builder()
            .id(1)
            .nome("BACKOFFICE_SUPORTE_VENDAS")
            .codigo(CodigoNivel.BACKOFFICE_SUPORTE_VENDAS)
            .build();
    }

    public static OrganizacaoEmpresaRequest umaOrganizacaoEmpresaSuporteVendasRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Organizacao Suporte Vendas")
            .codigo("Suporte Vendas")
            .nivelId(1)
            .situacao(A)
            .build();
    }

    public static OrganizacaoEmpresa umaOrganizacaoEmpresaSuporteVendas(Integer id, String nome, String codigo) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .nome(nome)
            .nivel(umNivelSuporteVendas())
            .situacao(A)
            .dataCadastro(LocalDateTime.now())
            .usuarioCadastro(umUsuario())
            .codigo(codigo)
            .build();
    }

}
