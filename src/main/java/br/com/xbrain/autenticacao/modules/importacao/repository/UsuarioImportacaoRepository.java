package br.com.xbrain.autenticacao.modules.importacao.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.importacao.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile(value = "importacao")
public class UsuarioImportacaoRepository {

    @Autowired
    @Qualifier("parceiros")
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Empresa> getEmpresasAa(Integer usuarioId) {
        return jdbcTemplate.query("SELECT E.ID "
                        + "  FROM USUARIO U "
                        + "  JOIN AA_USUARIO AU ON AU.FK_USUARIO = U.ID "
                        + "  JOIN AGENTE_AUTORIZADO AA ON AA.ID = AU.FK_AGENTE_AUTORIZADO "
                        + "  JOIN AA_EMPRESA AE ON AE.FK_AGENTE_AUTORIZADO = AA.ID "
                        + "  JOIN EMPRESA E ON E.ID = AE.FK_EMPRESA "
                        + " WHERE U.ID = :_usuarioId ",
                new MapSqlParameterSource("_usuarioId", usuarioId),
                this::getEmpresas);
    }

    public Empresa getEmpresas(ResultSet rs, int rownum) throws SQLException {
        return new Empresa(rs.getInt("id"));
    }

    public List<UnidadeNegocio> getUnidadesNegociosAa(Integer usuarioId) {
        return jdbcTemplate.query("SELECT DISTINCT UN.ID UNIDADE_NEGOCIO_ID "
                        + "FROM AA_USUARIO AAU "
                        + "JOIN USUARIO U ON U.ID = AAU.FK_USUARIO "
                        + "JOIN AGENTE_AUTORIZADO AA ON AA.ID = AAU.FK_AGENTE_AUTORIZADO "
                        + "JOIN UNIDADE_NEGOCIO UN ON UN.ID = AA.FK_UNIDADE_NEGOCIO "
                        + "WHERE AAU.FK_USUARIO = :_usuarioId ",
                new MapSqlParameterSource("_usuarioId", usuarioId),
                this::getUnidadesNegocios);
    }

    public UnidadeNegocio getUnidadesNegocios(ResultSet rs, int rownum) throws SQLException {
        return new UnidadeNegocio(rs.getInt("UNIDADE_NEGOCIO_ID"));
    }

    public List<String> getCpfUsuarioColaboradorVendas(Integer usuarioId) {
        String query = "SELECT CV.CPF "
                + "FROM COLABORADOR_VENDAS CV "
                + "INNER JOIN USUARIO U ON U.ID = CV.FK_USUARIO "
                + "WHERE USUARIO_ID =  :_usuarioId "
                + "AND CV.CPF IS NOT NULL";

        return jdbcTemplate.query(query,
                new MapSqlParameterSource("_usuarioId", usuarioId),
                this::getCpf);
    }

    public String getCpf(ResultSet rs, int rownum) throws SQLException {
        return rs.getString("CPF");
    }

    public List<PermissaoEspecialImportacao> getAllPermissoesEspeciais() {

        return jdbcTemplate.query("SELECT PE.DATA_BAIXA "
                        + "     , PE.DATA_CADASTRO "
                        + "     , PE.FK_FUNCIONALIDADE "
                        + "     , F.ROLE "
                        + "     , PE.FK_USUARIO "
                        + "     , PE.FK_USUARIO_BAIXA "
                        + "     , PE.FK_USUARIO_CADASTRO "
                        + "  FROM PERMISSAO_ESPECIAL PE "
                        + "  JOIN FUNCIONALIDADE F ON F.ID = PE.FK_FUNCIONALIDADE ",
                new MapSqlParameterSource(),
                this::mapRowPermissaoEspecial);

    }

    @SuppressWarnings("unchecked")
    public List<CargoDepartamentoFuncionalidadeImportacao> getAllPermissoesPorCargoDepartamento() {
        return jdbcTemplate.query("SELECT CDF.ID "
                        + "     , CDF.DATA_CADASTRO "
                        + "     , CDF.FK_CARGO "
                        + "     , CDF.FK_DEPARTAMENTO "
                        + "     , CDF.FK_EMPRESA "
                        + "     , CDF.FK_FUNCIONALIDADE "
                        + "     , F.ROLE "
                        + "     , CDF.FK_UNIDADE_NEGOCIO "
                        + "     , CDF.FK_USUARIO "
                        + "  FROM CARGO_DEPART_FUNC CDF "
                        + "  JOIN FUNCIONALIDADE F ON F.ID = CDF.FK_FUNCIONALIDADE "
                        + " ORDER BY ID ",
                new MapSqlParameterSource(),
                this::mapRowPermissoes);
    }

    @SuppressWarnings("unchecked")
    public List<UsuarioCidadeImportacao> getAllCidadesUsuariosParceirosOnline(Integer usuarioId) {
        return jdbcTemplate.query("SELECT UC.DATA_BAIXA "
                        + ", UC.DATA_CADASTRO "
                        + ", UC.FK_CIDADE "
                        + ", UC.FK_USUARIO "
                        + ", UC.FK_USUARIO_BAIXA "
                        + ", UC.FK_USUARIO_CADASTRO "
                        + "FROM USUARIO_CIDADE UC "
                        + " WHERE UC.FK_USUARIO = :_usuarioId",
                new MapSqlParameterSource("_usuarioId", usuarioId),
                this::mapRowCidades);
    }

    @SuppressWarnings("unchecked")
    public List<UsuarioHierarquiaImportacao> getAllHierarquiasUsuariosParceirosOnline(Integer usuarioId) {
        return jdbcTemplate.query("SELECT UH.DATA_CADASTRO "
                        + ", UH.FK_USUARIO "
                        + ", UH.FK_USUARIO_CADASTRO "
                        + ", UH.FK_USUARIO_SUPERIOR "
                        + "FROM USUARIO_HIERARQUIA UH "
                        + " WHERE UH.FK_USUARIO = :_usuarioId",
                new MapSqlParameterSource("_usuarioId", usuarioId),
                this::mapRowHierarquias);
    }

    @SuppressWarnings("unchecked")
    public List<UsuarioImportacao> getAllUsuariosParceirosOnline() {
        return jdbcTemplate.query("SELECT U.ID "
                        + "     , U.NOME "
                        + "     , U.EMAIL_01 "
                        + "     , U.EMAIL_02 "
                        + "     , U.EMAIL_03 "
                        + "     , U.TELEFONE_01 "
                        + "     , U.TELEFONE_02 "
                        + "     , U.TELEFONE_03 "
                        + "     , U.CPF "
                        + "     , U.RG "
                        + "     , U.ORGAO_EXPEDIDOR "
                        + "     , U.LOGIN_NET_SALES "
                        + "     , U.NASCIMENTO "
                        + "     , U.FK_UNIDADE_NEGOCIO "
                        + "     , (SELECT LISTAGG(UE.FK_EMPRESA, ',') WITHIN GROUP (ORDER BY UE.FK_EMPRESA) "
                        + " FROM USUARIO_EMPRESA UE WHERE UE.FK_USUARIO = U.ID) AS EMPRESAS "
                        + "     , (SELECT LISTAGG(UH.FK_USUARIO_SUPERIOR, ',') WITHIN GROUP "
                        + " (ORDER BY UH.FK_USUARIO_SUPERIOR) "
                        + " FROM USUARIO_HIERARQUIA UH WHERE UH.FK_USUARIO = U.ID) AS HIERARQUIAS "
                        + "     , FK_CARGO "
                        + "     , FK_DEPARTAMENTO "
                        + "     , DATA_CADASTRO "
                        + "     , FK_USUARIO_CADASTRO "
                        + "     , SENHA "
                        + "     , ALTERAR_SENHA "
                        + "     , CASE WHEN DATA_INATIVACAO IS NULL THEN 'A' ELSE 'I' END AS SITUACAO "
                        + "  FROM USUARIO U "
                        //+ " WHERE U.FK_CARGO != 50 AND U.FK_DEPARTAMENTO != 50 "
                        + "GROUP BY U.ID "
                        + "       , U.NOME "
                        + "       , U.EMAIL_01 "
                        + "       , U.EMAIL_02 "
                        + "       , U.EMAIL_03 "
                        + "       , U.TELEFONE_01 "
                        + "       , U.TELEFONE_02  "
                        + "       , U.TELEFONE_03  "
                        + "       , U.CPF "
                        + "       , U.RG "
                        + "       , U.ORGAO_EXPEDIDOR "
                        + "       , U.LOGIN_NET_SALES "
                        + "       , U.NASCIMENTO "
                        + "       , U.FK_UNIDADE_NEGOCIO "
                        + "       , FK_CARGO "
                        + "       , FK_DEPARTAMENTO "
                        + "       , DATA_CADASTRO "
                        + "       , FK_USUARIO_CADASTRO "
                        + "       , SENHA "
                        + "       , ALTERAR_SENHA "
                        + "       , DATA_INATIVACAO "
                        + "ORDER BY U.ID ",
                new MapSqlParameterSource(),
                this::mapRow);
    }

    private UsuarioImportacao mapRow(ResultSet rs, int rowNumber) throws SQLException {
        rowNumber = rowNumber;
        UsuarioImportacao usuarioImportacao = new UsuarioImportacao();
        usuarioImportacao.setId(rs.getInt("id"));
        usuarioImportacao.setNome(rs.getString("nome"));
        usuarioImportacao.setEmail(rs.getString("email_01"));
        usuarioImportacao.setEmail02(rs.getString("email_02"));
        usuarioImportacao.setEmail02(rs.getString("email_03"));
        usuarioImportacao.setTelefone(rs.getString("telefone_01"));
        usuarioImportacao.setTelefone02(rs.getString("telefone_02"));
        usuarioImportacao.setTelefone03(rs.getString("telefone_03"));
        usuarioImportacao.setCpf(rs.getString("cpf"));
        usuarioImportacao.setRg(rs.getString("rg"));
        usuarioImportacao.setOrgaoExpedidor(rs.getString("orgao_expedidor"));
        usuarioImportacao.setLoginNetSales(rs.getString("login_net_sales"));
        usuarioImportacao.setNascimento(toLocalDateTime(rs.getTimestamp("nascimento")));
        usuarioImportacao.setUnidadeNegocioId(rs.getInt("fk_unidade_negocio"));
        usuarioImportacao.setEmpresasId(getIntegerList(rs.getString("empresas")));
        usuarioImportacao.setCargoId(rs.getInt("fk_cargo"));
        usuarioImportacao.setDepartamentoId(rs.getInt("fk_departamento"));
        usuarioImportacao.setDataCadastro(toLocalDateTime(rs.getTimestamp("data_cadastro")));
        usuarioImportacao.setUsuarioCadastroId(rs.getInt("fk_usuario_cadastro"));
        usuarioImportacao.setSenha(rs.getString("senha"));
        usuarioImportacao.setAlterarSenha(valueOf(rs.getString("alterar_senha")));
        usuarioImportacao.setSituacao(ESituacao.valueOf(rs.getString("situacao")));
        return usuarioImportacao;
    }

    private UsuarioHierarquiaImportacao mapRowHierarquias(ResultSet rs, int rowNumber) throws SQLException {
        rowNumber = rowNumber;
        UsuarioHierarquiaImportacao usuarioHierarquiaImportacao = new UsuarioHierarquiaImportacao();
        usuarioHierarquiaImportacao.setDataCadastro(toLocalDateTime(rs.getTimestamp("data_cadastro")));
        usuarioHierarquiaImportacao.setUsuarioId(rs.getInt("fk_usuario"));
        usuarioHierarquiaImportacao.setUsuarioCadastroId(rs.getInt("fk_usuario_cadastro"));
        usuarioHierarquiaImportacao.setUsuarioSuperiorId(rs.getInt("fk_usuario_superior"));
        return usuarioHierarquiaImportacao;
    }

    private UsuarioCidadeImportacao mapRowCidades(ResultSet rs, int rowNumber) throws SQLException {
        rowNumber = rowNumber;
        UsuarioCidadeImportacao usuarioCidadeImportacao = new UsuarioCidadeImportacao();
        usuarioCidadeImportacao.setDataBaixa(toLocalDateTime(rs.getTimestamp("data_baixa")));
        usuarioCidadeImportacao.setDataCadastro(toLocalDateTime(rs.getTimestamp("data_cadastro")));
        usuarioCidadeImportacao.setCidadeId(rs.getInt("fk_cidade"));
        usuarioCidadeImportacao.setUsuarioId(rs.getInt("fk_usuario"));
        usuarioCidadeImportacao.setUsuarioBaixaId(rs.getInt("fk_usuario_baixa"));
        usuarioCidadeImportacao.setUsuarioCadastroId(rs.getInt("fk_usuario_cadastro"));
        return usuarioCidadeImportacao;
    }

    private CargoDepartamentoFuncionalidadeImportacao mapRowPermissoes(ResultSet rs, int rowNumber)
            throws SQLException {
        rowNumber = rowNumber;
        CargoDepartamentoFuncionalidadeImportacao dto = new CargoDepartamentoFuncionalidadeImportacao();
        dto.setId(rs.getInt("id"));
        dto.setDataCadastro(toLocalDateTime(rs.getTimestamp("data_cadastro")));
        dto.setCargoId(rs.getInt("fk_cargo"));
        dto.setDepartamentoId(rs.getInt("fk_departamento"));
        dto.setFuncionalidadeId(rs.getInt("fk_funcionalidade"));
        dto.setRole("POL_" + rs.getString("role"));
        dto.setUsuarioId(rs.getInt("fk_usuario"));
        return dto;
    }

    private PermissaoEspecialImportacao mapRowPermissaoEspecial(ResultSet rs, int rowNumber)
            throws SQLException {
        rowNumber = rowNumber;
        PermissaoEspecialImportacao dto = new PermissaoEspecialImportacao();
        dto.setDataBaixa(toLocalDateTime(rs.getTimestamp("data_baixa")));
        dto.setDataCadastro(toLocalDateTime(rs.getTimestamp("data_cadastro")));
        dto.setFuncionalidadeId(rs.getInt("fk_funcionalidade"));
        dto.setRole("POL_" + rs.getString("role"));
        dto.setUsuarioId(rs.getInt("fk_usuario"));
        dto.setUsuarioBaixaId(rs.getInt("fk_usuario_baixa"));
        dto.setUsuarioCadastroId(rs.getInt("fk_usuario_cadastro"));
        return dto;
    }

    private List<Integer> getIntegerList(String empresasIds) {
        if (empresasIds == null) {
            return null;
        }
        String[] ids = empresasIds.split(",");
        List<Integer> lista = new ArrayList<>();
        for (String id : ids) {
            lista.add(Integer.parseInt(id));
        }
        return lista;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp != null) {
            return timestamp.toLocalDateTime();
        }
        return null;
    }

    private Eboolean valueOf(String name) {
        try {
            if (name != null && !name.isEmpty()) {
                return Eboolean.valueOf(name);
            }
        } catch (Exception ex) {
            System.out.println(name);
        }
        return null;
    }

}
