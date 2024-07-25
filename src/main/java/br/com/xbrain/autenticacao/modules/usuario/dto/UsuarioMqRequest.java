package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMqRequest {

    private Integer id;
    private String cpf;
    private String nome;
    private String email;
    private String email02;
    private String telefone;
    private String telefone02;
    private String telefone03;
    private String rg;
    private String loginNetSales;
    private String orgaoExpedidor;
    private ESituacao situacao;
    private LocalDateTime nascimento;
    private CodigoNivel nivel;
    private CodigoDepartamento departamento;
    private CodigoCargo cargo;
    private List<CodigoUnidadeNegocio> unidadesNegocio;
    private List<CodigoEmpresa> empresa;
    private Integer usuarioCadastroId;
    private String usuarioCadastroNome;
    private String exception;
    private Set<ECanal> canais;
    private Set<SubCanalDto> subCanais;
    private Integer colaboradorId;
    private Integer agenteAutorizadoId;
    private ETipoFeeder agenteAutorizadoFeeder;
    private boolean isCadastroSocioPrincipal;
    private boolean equipeTecnica;
    private boolean tecnicoIndicador;
    private boolean isAtualizarSocioPrincipal;
    private List<Integer> agentesAutorizadosIds;
    private List<Integer> antigosSociosPrincipaisIds;

    public boolean isNovoCadastro() {
        return this.id == null;
    }

    public boolean isNovoCadastroSocioPrincipal() {
        return Objects.isNull(id) && isCadastroSocioPrincipal;
    }

    public boolean isNovoCadastroSocioSecundario() {
        return this.id == null && this.cargo == CodigoCargo.AGENTE_AUTORIZADO_SOCIO_SECUNDARIO;
    }
}
