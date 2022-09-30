package br.com.xbrain.autenticacao.modules.organizacaoempresa.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargo;

public class ControllerTestHelper {

    public static List<ModalidadeEmpresa> duasModalidadesEmpresa() {
        return List.of(ModalidadeEmpresa.builder()
                .id(1)
                .modalidadeEmpresa(EModalidadeEmpresa.PAP)
            .build(),
            ModalidadeEmpresa.builder()
                .id(2)
                .modalidadeEmpresa(EModalidadeEmpresa.TELEVENDAS)
                .build());
    }

    public static List<Nivel> doisNiveis() {
        return List.of(Nivel.builder()
                .id(1)
                .codigo(CodigoNivel.VAREJO)
                .build(),
                Nivel.builder()
                .id(2)
                .codigo(CodigoNivel.RECEPTIVO)
                .build());
    }

    public static List<SelectResponse> doisNiveisSelectResponse() {
        return doisNiveis().stream()
            .map(nivel -> new SelectResponse(nivel.getId(), nivel.getCodigo().name()))
            .collect(Collectors.toList());
    }

    public static List<SelectResponse> duasModalidadesEmpresaSelectResponse() {
        return duasModalidadesEmpresa().stream()
            .map(modalidade -> new SelectResponse(modalidade.getId(), modalidade.getModalidadeEmpresa().name()))
            .collect(Collectors.toList());
    }

    public static UsuarioAutenticado umUsuarioAdminAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder()
                .id(1)
                .situacao(ESituacao.A)
                .cargo(umCargo(50, CodigoCargo.ADMINISTRADOR))
                .departamento(Departamento.builder()
                    .id(50)
                    .codigo(CodigoDepartamento.ADMINISTRADOR)
                    .nome("ADMIN")
                    .build())
                .email("ADMIN@XBRAIN.COM.BR")
                .cpf("12345678910")
                .build())
            .build();
    }

    public static UsuarioAutenticado umUsuarioMsoConsultorAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder()
                .id(1)
                .situacao(ESituacao.A)
                .cargo(umCargo(22, CodigoCargo.MSO_CONSULTOR))
                .departamento(Departamento.builder()
                    .id(22)
                    .codigo(CodigoDepartamento.ADMINISTRATIVO)
                    .nome("MSO")
                    .build())
                .email("MSOCONSULTOR@XBRAIN.COM.BR")
                .cpf("12345678911")
                .organizacaoEmpresa(OrganizacaoEmpresa.builder().id(1).build())
                .build())
            .build();
    }

    public static UsuarioAutenticado umUsuarioVendedorAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder()
                .id(1)
                .situacao(ESituacao.A)
                .cargo(umCargo(8, CodigoCargo.VENDEDOR_OPERACAO))
                .departamento(Departamento.builder()
                    .id(67)
                    .codigo(CodigoDepartamento.COMERCIAL)
                    .nome("COMERCIAL")
                    .build())
                .email("vendedor@XBRAIN.COM.BR")
                .cpf("12345678911")
                .build())
            .build();
    }
}
