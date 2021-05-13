package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.UsuarioExcessoUsoResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.service.DeslogarUsuarioPorExcessoDeUsoService;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederFiltros;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioFunilProspeccaoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioServiceEsqueciSenha;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;

@RestController
@RequestMapping(value = "api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioServiceEsqueciSenha usuarioServiceEsqueciSenha;
    @Autowired
    private UsuarioAgendamentoService usuarioAgendamentoService;
    @Autowired
    private UsuarioFunilProspeccaoService usuarioFunilProspeccaoService;
    @Autowired
    private DeslogarUsuarioPorExcessoDeUsoService deslogarUsuarioPorExcessoDeUsoService;

    private Integer getUsuarioId(Principal principal) {
        return Integer.parseInt(principal.getName().split(Pattern.quote("-"))[0]);
    }

    @GetMapping
    public UsuarioAutenticado getUsuario(Principal principal) {
        return new UsuarioAutenticado(
            usuarioService.findByIdCompleto(getUsuarioId(principal)));
    }

    @GetMapping("ativos/nivel/operacao/canal-aa")
    public List<SelectResponse> buscarUsuariosAtivosNivelOperacaoCanalAa() {
        return usuarioService.buscarUsuariosAtivosNivelOperacaoCanalAa();
    }

    @PutMapping("ativar-socio")
    public void ativarSocioPrincipal(@RequestParam String email) {
        usuarioService.ativarSocioPrincipal(email);
    }

    @PutMapping("ativar/{id}")
    public void ativar(@PathVariable Integer id) {
        usuarioService.ativar(id);
    }

    @PutMapping("inativar-socio")
    public void inativarSocioPrincipal(@RequestParam String email) {
        usuarioService.inativarSocioPrincipal(email);
    }

    @PutMapping("inativar/{id}")
    public void inativar(@PathVariable Integer id) {
        usuarioService.inativar(id);
    }

    @GetMapping("/autenticado/{id}")
    public UsuarioAutenticado getUsuarioAutenticadoById(@PathVariable("id") int id) {
        return new UsuarioAutenticado(
            usuarioService.findCompleteById(id));
    }

    @GetMapping("/autenticado-com-login-netsales/{id}")
    public UsuarioAutenticado getUsuarioAutenticadoComLoginNetSalesById(@PathVariable int id) {
        return new UsuarioAutenticado(
            usuarioService.findCompleteByIdComLoginNetSales(id));
    }

    @GetMapping("vendedores")
    public List<UsuarioResponse> getUsuarioVendedorById(@RequestParam List<Integer> ids) {
        return usuarioService.getVendedoresByIds(ids);
    }

    @GetMapping("ativos/operacao-comercial/cargo/{cargoId}")
    public List<UsuarioResponse> buscarColaboradoresAtivosOperacaoComericialPorCargo(@PathVariable Integer cargoId) {
        return usuarioService.buscarColaboradoresAtivosOperacaoComericialPorCargo(cargoId);
    }

    @GetMapping("/{id}")
    public UsuarioResponse getUsuarioById(@PathVariable("id") int id) {
        return UsuarioResponse.of(
            usuarioService.findByIdComAa(id), usuarioService.getFuncionalidadeByUsuario(id).stream()
                .map(FuncionalidadeResponse::getRole).collect(Collectors.toList()));
    }

    @RequestMapping(params = "nivel", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuarioByNivel(@RequestParam CodigoNivel nivel) {
        return usuarioService.getUsuarioByNivel(nivel);
    }

    @GetMapping(value = "/{id}/cidades")
    public List<CidadeResponse> getCidadesByUsuario(@PathVariable("id") int id) {
        return usuarioService.findCidadesByUsuario(id);
    }

    @GetMapping(value = "/{id}/subclusters")
    public List<SelectResponse> getSubclustersUsuario(@PathVariable("id") int usuarioId) {
        return usuarioService.getSubclusterUsuario(usuarioId);
    }

    @RequestMapping(value = "/{id}/subordinados", method = RequestMethod.GET)
    public List<Integer> getSubordinados(@PathVariable("id") int id,
                                         @RequestParam(required = false, defaultValue = "false") boolean incluirProprio) {
        return usuarioService.getIdDosUsuariosSubordinados(id, incluirProprio);
    }

    @RequestMapping(value = "/{id}/subordinados/vendas", method = RequestMethod.GET)
    public List<Integer> getSubordinadosVendas(@PathVariable("id") int id) {
        return usuarioService.getIdDosUsuariosSubordinados(id, true);
    }

    @GetMapping("/hierarquia/superiores/{id}")
    public List<UsuarioHierarquiaResponse> getSuperioresByUsuario(@PathVariable Integer id) {
        return usuarioService.getSuperioresDoUsuario(id);
    }

    @GetMapping("/hierarquia/superiores/{id}/{codigoCargo}")
    public List<UsuarioHierarquiaResponse> getSuperioresByUsuarioPorCargo(@PathVariable Integer id,
                                                                          @PathVariable CodigoCargo codigoCargo) {
        return usuarioService.getSuperioresDoUsuarioPorCargo(id, codigoCargo);
    }

    @GetMapping("/hierarquia/subordinados/{id}")
    public List<UsuarioSubordinadoDto> getSubordinadosByUsuario(@PathVariable Integer id) {
        return usuarioService.getSubordinadosDoUsuario(id);
    }

    @GetMapping("/hierarquia/subordinados/gerente/{id}")
    public List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(@PathVariable Integer id) {
        return usuarioService.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(id);
    }

    @GetMapping("/executivos-comerciais")
    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial() {
        return usuarioService.findAllExecutivosOperacaoDepartamentoComercial();
    }

    @PostMapping("/vincula/hierarquia")
    public void vincularUsuariosComSuperior(@RequestParam List<Integer> idsUsuarios,
                                            @RequestParam Integer idUsuarioSuperior) {
        usuarioService.vincularUsuario(idsUsuarios, idUsuarioSuperior);
    }

    @PostMapping("/alterar/hierarquia")
    public void vincularUsuariosComSuperior(@RequestBody AlteraSuperiorRequest superiorDto) {
        usuarioService.vincularUsuarioParaNovaHierarquia(superiorDto);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public List<UsuarioDto> getUsuariosFilter(UsuarioFiltrosDto usuarioFiltrosDto) {
        return usuarioService.getUsuariosFiltros(usuarioFiltrosDto);
    }

    @RequestMapping(params = "ids", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByIds(@RequestParam List<Integer> ids) {
        return usuarioService.getUsuariosByIds(ids);
    }

    @GetMapping("inativos")
    public List<UsuarioResponse> getUsuariosInativosByIds(@RequestParam List<Integer> usuariosInativosIds) {

        return Lists.partition(usuariosInativosIds, QTD_MAX_IN_NO_ORACLE)
                .stream()
                .map(ids -> usuarioService.getUsuariosInativosByIds(ids))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @GetMapping(params = "email")
    public UsuarioResponse getUsuarioByEmail(@RequestParam String email, @RequestParam(required = false) Boolean buscarAtivo) {
        Optional<UsuarioResponse> emailAaOptional = usuarioService.findByEmailAa(email, buscarAtivo);
        return emailAaOptional.orElse(null);

    }

    @GetMapping(params = "cpf")
    public UsuarioResponse getUsuarioByCpf(@RequestParam String cpf, @RequestParam(required = false) Boolean buscarAtivo) {
        Optional<UsuarioResponse> cpfAaOpt = usuarioService.findByCpfAa(cpf, buscarAtivo);
        return cpfAaOpt.orElse(null);
    }

    @GetMapping("atual/cpf")
    public UsuarioResponse buscarAtualByCpf(@RequestParam String cpf) {
        return usuarioService.buscarAtualByCpf(cpf);
    }

    @GetMapping("atual/email")
    public UsuarioResponse buscarAtualByEmail(@RequestParam String email) {
        return usuarioService.buscarAtualByEmail(email);
    }

    @RequestMapping(value = "/{id}/empresas", method = RequestMethod.GET)
    public List<EmpresaResponse> getEmpresasDoUsuario(@PathVariable("id") int id) {
        return usuarioService.findEmpresasDoUsuario(id);
    }

    @GetMapping("/hierarquia/supervisores")
    public List<UsuarioResponse> getUsuariosSupervisores(UsuarioFiltrosHierarquia filtrosHierarquia) {
        return usuarioService.getUsuariosSuperiores(filtrosHierarquia);
    }

    @GetMapping("/hierarquia/supervisores/{executivoId}")
    public List<UsuarioAutoComplete> getAllLideresComerciaisDoExecutivo(@PathVariable Integer executivoId) {
        return usuarioService.findAllLideresComerciaisDoExecutivo(executivoId);
    }

    @GetMapping("/hierarquia/supervisores-aa-auto-complete/{executivoId}")
    public List<UsuarioSuperiorAutoComplete> getUsuariosSupervisoresDoAaAutoComplete(@PathVariable Integer executivoId) {
        return usuarioService.getUsuariosSupervisoresDoAaAutoComplete(executivoId);
    }

    @GetMapping("/executivos-comerciais-agente-autorizado")
    public List<UsuarioAutoComplete> findExecutivosPorIds(@RequestParam List<Integer> usuariosExecutivos) {
        return usuarioService.findExecutivosPorIds(usuariosExecutivos);
    }

    @RequestMapping(params = "funcionalidade", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByPermissao(
        @RequestParam String funcionalidade) {
        return usuarioService.getUsuarioByPermissao(funcionalidade);
    }

    @RequestMapping(value = "/configuracao", method = RequestMethod.GET)
    public ConfiguracaoResponse getConfiguracaoByUsuario() {
        return usuarioService.getConfiguracaoByUsuario();
    }

    @RequestMapping(value = "/adicionar-configuracao", method = RequestMethod.POST)
    public ConfiguracaoResponse adicionarConfiguracao(@RequestBody UsuarioConfiguracaoDto dto) {
        return usuarioService.adicionarConfiguracao(dto);
    }

    @RequestMapping(value = "usuarios-hierarquias-save", method = RequestMethod.POST)
    public void saveUsuarioHierarquia(@RequestBody List<UsuarioHierarquiaCarteiraDto> novasHierarquias) {
        usuarioService.saveUsuarioHierarquia(novasHierarquias);
    }

    @RequestMapping(value = "/remover-configuracao", method = RequestMethod.PUT)
    public void removerConfiguracao(@RequestBody UsuarioConfiguracaoDto dto) {
        usuarioService.removerConfiguracao(dto);
    }

    @RequestMapping(value = "/remover-ramal-configuracao", method = RequestMethod.PUT)
    public void removerRamalConfiguracao(@RequestBody UsuarioConfiguracaoDto dto) {
        usuarioService.removerRamalConfiguracao(dto);
    }

    @RequestMapping(value = "/remover-ramais-configuracao", method = RequestMethod.PUT)
    public void removerRamaisDeConfiguracao(@RequestBody List<UsuarioConfiguracaoDto> usuarioConfiguracaoDtoList) {
        usuarioService.removerRamaisDeConfiguracao(usuarioConfiguracaoDtoList);
    }

    @RequestMapping(value = "/esqueci-senha", method = RequestMethod.PUT)
    public void esqueceuSenha(@RequestBody UsuarioDadosAcessoRequest dto) {
        usuarioServiceEsqueciSenha.enviarConfirmacaoResetarSenha(dto.getEmailAtual());
    }

    @GetMapping(value = "/resetar-senha")
    public void resetarSenha(@RequestParam String token) {
        usuarioServiceEsqueciSenha.resetarSenha(token);
    }

    @PutMapping("inativar-colaboradores")
    public void inativarColaboradores(@RequestParam String cnpj) {
        usuarioService.inativarColaboradores(cnpj);
    }

    @GetMapping("/canais")
    public Iterable<SelectResponse> getCanais() {
        return ECanal.getCanaisAtivos()
            .stream()
            .map(item -> SelectResponse.of(item.name(), item.getDescricao()))
            .sorted(Comparator.comparing(SelectResponse::getLabel))
            .collect(Collectors.toList());
    }

    @GetMapping("{usuarioId}/subordinados/cargo/{codigoCargo}")
    public List<Integer> getIdsDaHierarquia(@PathVariable Integer usuarioId, @PathVariable String codigoCargo) {
        return usuarioService.getIdsSubordinadosDaHierarquia(usuarioId, codigoCargo);
    }

    @GetMapping("{id}/vendedores-hierarquia-ids")
    public List<Integer> getIdsVendedoresDaHierarquia(@PathVariable Integer id) {
        return usuarioService.getIdsVendedoresOperacaoDaHierarquia(id);
    }

    @GetMapping("{id}/vendedores-hierarquia")
    public List<UsuarioHierarquiaResponse> getVendedoresDaHierarquia(@PathVariable Integer id) {
        return usuarioService.getVendedoresOperacaoDaHierarquia(id);
    }

    @GetMapping("permissoes-por-canal")
    public List<UsuarioPermissaoCanal> getPermissoesPorCanal() {
        return usuarioService.getPermissoesUsuarioAutenticadoPorCanal();
    }

    @PostMapping("permissoes-por-usuario")
    public List<UsuarioPermissoesResponse> findUsuarioByPermissoes(
            @Validated @RequestBody UsuarioPermissoesRequest usuarioPermissoesRequest) {
        return usuarioService.findUsuariosByPermissoes(usuarioPermissoesRequest);
    }

    @GetMapping("distribuicao/agendamentos/{agenteAutorizadoId}/disponiveis")
    public List<UsuarioAgendamentoResponse> getUsuariosDisponiveis(@PathVariable Integer agenteAutorizadoId) {
        return usuarioAgendamentoService.recuperarUsuariosDisponiveisParaDistribuicao(agenteAutorizadoId);
    }

    @GetMapping("distribuicao/agendamentos/{usuarioId}/agenteautorizado/{agenteAutorizadoId}")
    public List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosParaDistribuicaoDeAgendamentos(
            @PathVariable Integer usuarioId, @PathVariable Integer agenteAutorizadoId) {
        return usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(usuarioId, agenteAutorizadoId);
    }

    @GetMapping("usuario-funil-prospeccao")
    public FunilProspeccaoUsuarioDto findUsuarioProspeccaoByCidade(@RequestParam String cidade) {
        return usuarioFunilProspeccaoService.findUsuarioDirecionadoByCidade(cidade);
    }

    @GetMapping("executivos")
    public List<UsuarioExecutivoResponse> findUsuariosExecutivos() {
        return usuarioService.buscarExecutivosPorSituacao(ESituacao.A);
    }

    @GetMapping("{id}/sem-permissoes")
    public UsuarioResponse findById(@PathVariable Integer id) {
        return usuarioService.findById(id);
    }

    @GetMapping("/cargo/{codigoCargo}")
    public List<UsuarioResponse> findUsuariosByCodigoCargo(@PathVariable CodigoCargo codigoCargo) {
        return usuarioService.findUsuariosByCodigoCargo(codigoCargo);
    }

    @GetMapping("usuario-situacao")
    public List<UsuarioSituacaoResponse> findUsuariosByIds(@RequestParam List<Integer> usuariosIds) {
        return usuarioService.findUsuariosByIds(usuariosIds);
    }

    @GetMapping("inativado-por-excesso-de-uso/{usuarioId}")
    public UsuarioExcessoUsoResponse validarUsuarioBloqueadoPorExcessoDeUso(@PathVariable Integer usuarioId) {
        return deslogarUsuarioPorExcessoDeUsoService.validarUsuarioBloqueadoPorExcessoDeUso(usuarioId);
    }

    @GetMapping("{id}/url-loja-online")
    public UrlLojaOnlineResponse getUrlLojaOnline(@PathVariable Integer id) {
        return usuarioService.getUrlLojaOnline(id);
    }

    @GetMapping("{id}/com-login-netsales")
    public UsuarioComLoginNetSalesResponse getUsuarioByIdComLoginNetSales(@PathVariable Integer id) {
        return usuarioService.getUsuarioByIdComLoginNetSales(id);
    }

    @GetMapping(params = "organizacaoId")
    public List<SelectResponse> findUsuariosOperadoresBackofficeByOrganizacao(@RequestParam Integer organizacaoId) {
        return usuarioService.findUsuariosOperadoresBackofficeByOrganizacao(organizacaoId);
    }

    @GetMapping("permitidos")
    public List<Integer> getAllUsuariosDaHierarquiaD2dDoUserLogado() {
        return usuarioService.getAllUsuariosDaHierarquiaD2dDoUserLogado();
    }

    @GetMapping("permitidos/select")
    public List<SelectResponse> buscarUsuariosDaHierarquiaDoUsuarioLogadoPorCargp(CodigoCargo codigoCargo) {
        return usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogado(codigoCargo);
    }

    @GetMapping("/backoffices-socios-por-agentes-autorizado-id")
    public List<UsuarioAgenteAutorizadoResponse> buscarBackOfficesAndSociosAaPorAaIds(
        @RequestParam List<Integer> agentesAutorizadoId) {
        return usuarioService.buscarBackOfficesAndSociosAaPorAaIds(agentesAutorizadoId);
    }

    @GetMapping("vendedores-feeder")
    public List<VendedoresFeederResponse> buscarVendedoresFeeder(@Validated VendedoresFeederFiltros filtros) {
        return usuarioService.buscarVendedoresFeeder(filtros);
    }

    @GetMapping("{id}/nome")
    public String obterNomeUsuarioPorId(@PathVariable Integer id) {
        return usuarioService.obterNomeUsuarioPorId(id);
    }

    @PostMapping("usuario-situacao/por-ids")
    public List<UsuarioSituacaoResponse> buscarUsuarioSituacaoPorIds(@RequestBody @Validated UsuarioSituacaoFiltro filtro) {
        return usuarioService.buscarUsuarioSituacaoPorIds(filtro);
    }
}
