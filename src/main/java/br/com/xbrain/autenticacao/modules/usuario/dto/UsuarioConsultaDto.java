package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UsuarioConsultaDto {

    private int id;
    private String nome;
    private String email;
    private String cpf;
    private String unidadeNegocioNome;
    private String empresaNome;
    private String situacao;
    private String nivelCodigo;
    private String nivelNome;
    private String cargoNome;
    private String departamentoNome;
    private Set<String> tiposFeeder;

    public UsuarioConsultaDto() {
    }

    public static UsuarioConsultaDto convertFrom(Usuario usuario) {
        UsuarioConsultaDto response = new UsuarioConsultaDto();
        BeanUtils.copyProperties(usuario, response);
        response.setSituacao(usuario.getSituacao().toString());
        response.setNivelNome(usuario.getCargo().getNivel().getNome());
        response.setNivelCodigo(usuario.getCargo().getNivel().getCodigo().name());
        response.setCargoNome(usuario.getCargo().getNome());
        response.setDepartamentoNome(usuario.getDepartamento().getNome());
        response.setUnidadeNegocioNome(usuario.getUnidadesNegocios().stream()
                .map(UnidadeNegocio::getNome).collect(Collectors.joining(", ")));
        response.setEmpresaNome(usuario.getEmpresas().stream()
                .map(Empresa::toString).collect(Collectors.joining(", ")));
        return response;
    }
}
