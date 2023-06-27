package br.com.xbrain.autenticacao.modules.organizacaoempresa.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargo;

public class ControllerTestHelper {

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
