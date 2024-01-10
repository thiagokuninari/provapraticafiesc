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
import java.util.List;

public class OrganizacaoEmpresaHelper {

    public static OrganizacaoEmpresaRequest organizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest
            .builder()
            .descricao("Marcos AA")
            .nome("MARCOS_AA")
            .nivelId(1)
            .codigo("CODIGO")
            .build();
    }

    public static OrganizacaoEmpresa organizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .descricao("Marcos AA")
            .nome("MARCOS_AA")
            .id(2)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
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
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .codigo("codigo")
            .build();
    }

    public static OrganizacaoEmpresa umaOutraOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .id(2)
            .nome("Teste AA Dois")
            .nivel(umNivel())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .codigo("codigo2")
            .build();
    }

    public static OrganizacaoEmpresaResponse umaOrganizacaoEmpresaResponse() {
        return OrganizacaoEmpresaResponse.of(umaOrganizacaoEmpresa());
    }

    public static List<OrganizacaoEmpresaResponse> umaListaOrganizacaoEmpresaResponse() {
        return List.of(OrganizacaoEmpresaResponse.builder()
                .id(1)
                .nome("MOTIVA")
                .codigo("MO1234")
                .descricao("MOTIVA")
                .situacao(ESituacaoOrganizacaoEmpresa.A)
                .build(),
            OrganizacaoEmpresaResponse.builder()
                .id(2)
                .nome("BCC")
                .codigo("BCC")
                .descricao("BRASIL CENTER")
                .situacao(ESituacaoOrganizacaoEmpresa.A)
                .build());
    }

    public static List<OrganizacaoEmpresaResponse> umaListaOrganizacaoEmpresaResponseComNivel() {
        return List.of(OrganizacaoEmpresaResponse.builder()
                .id(1)
                .situacao(ESituacaoOrganizacaoEmpresa.A)
                .nivel(umNivelResponse())
                .build(),
            OrganizacaoEmpresaResponse.builder()
                .id(2)
                .situacao(ESituacaoOrganizacaoEmpresa.A)
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
}
