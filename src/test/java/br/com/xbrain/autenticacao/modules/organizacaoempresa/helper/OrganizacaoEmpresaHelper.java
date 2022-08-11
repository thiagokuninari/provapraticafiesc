package br.com.xbrain.autenticacao.modules.organizacaoempresa.helper;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.util.List;

public class OrganizacaoEmpresaHelper {

    public static OrganizacaoEmpresaRequest organizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .razaoSocial("Marcos AA")
            .cnpj("66.845.365/0001-25")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .modalidadesEmpresaIds(List.of(1))
            .nivelEmpresaId(1)
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
            .cnpj("79.742.597/0001-08")
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }
}
