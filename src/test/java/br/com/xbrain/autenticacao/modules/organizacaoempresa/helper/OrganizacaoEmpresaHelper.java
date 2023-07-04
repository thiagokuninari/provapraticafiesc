package br.com.xbrain.autenticacao.modules.organizacaoempresa.helper;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
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
import java.util.stream.Collectors;

public class OrganizacaoEmpresaHelper {

    public static OrganizacaoEmpresaRequest organizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .razaoSocial("Marcos AA")
            .cnpj("66.845.365/0001-25")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .modalidadesEmpresaIds(List.of(1))
            .nivelId(1)
            .build();
    }

    public static OrganizacaoEmpresa organizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .razaoSocial("Marcos AA")
            .cnpj("66.845.365/0001-25")
            .id(2)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    public static OrganizacaoEmpresa organizacaoEmpresaNull() {
        return OrganizacaoEmpresa.builder()
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
            .razaoSocial("Teste AA")
            .cnpj("54.238.644/0001-41")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    public static OrganizacaoEmpresa umaOutraOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .id(2)
            .razaoSocial("Teste AA Dois")
            .nivel(umNivel())
            .cnpj("79.742.597/0001-08")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    public static List<OrganizacaoEmpresaResponse> umaListaOrganizacaoEmpresaResponse() {
        return List.of(OrganizacaoEmpresaResponse.builder()
                .id(1)
                .situacao(ESituacaoOrganizacaoEmpresa.A)
                .build(),
            OrganizacaoEmpresaResponse.builder()
                .id(2)
                .situacao(ESituacaoOrganizacaoEmpresa.A)
                .build());
    }

    public static List<SelectResponse> duasOrganizacoesEmpresaSelectResponse() {
        return duasOrganizacoesEmpresaSelectResponse().stream()
            .map(organizacaoEmpresa ->
                new SelectResponse(organizacaoEmpresa().getId(), organizacaoEmpresa().getNivel().getNome()))
            .collect(Collectors.toList());
    }

    public static Nivel umNivel() {
        return Nivel.builder()
            .id(1)
            .nome("VAREJO")
            .codigo(CodigoNivel.VAREJO)
            .build();
    }

    public static NivelResponse umNivelResponse() {
        return NivelResponse.builder()
            .id(1)
            .nome("VAREJO")
            .codigo(CodigoNivel.VAREJO.name())
            .build();
    }
}
