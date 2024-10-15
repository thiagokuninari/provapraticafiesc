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
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAgendamentoService usuarioAgendamentoService;
    private final UsuarioServiceEsqueciSenha usuarioServiceEsqueciSenha;
    private final UsuarioFunilProspeccaoService usuarioFunilProspeccaoService;
    private final DeslogarUsuarioPorExcessoDeUsoService deslogarUsuarioPorExcessoDeUsoService;

    private Integer getUsuarioId(Principal principal) {
        return Integer.parseInt(principal.getName().split(Pattern.quote("-"))[0]);
    }

    @GetMapping
    public UsuarioAutenticado getUsuario(Principal principal) {
        return new UsuarioAutenticado(
            usuarioService.findByIdCompleto(getUsuarioId(principal)));
    }

    @PostMapping("por-ids")
    public Collection<UsuarioResponse> getAllPorIds(@RequestBody @Validated UsuarioPorIdFiltro filtro) {
        return usuarioService.findAllResponsePorIds(filtro);
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

    @GetMapping("ids/superiores/usuario-logado")
    public Set<Integer> getAllUsuariosIdsSuperiores() {
        return usuarioService.getAllUsuariosIdsSuperiores();
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

    @PostMapping("vendedores")
    public List<UsuarioResponse> getUsuarioVendedorByIdPost(@RequestBody List<Integer> ids) {
        return usuarioService.getVendedoresByIds(ids);
    }

    @GetMapping("ativos/operacao-comercial/cargo/{cargoId}")
    public List<UsuarioResponse> buscarColaboradoresAtivosOperacaoComericialPorCargo(@PathVariable Integer cargoId) {
        return usuarioService.buscarColaboradoresAtivosOperacaoComericialPorCargo(cargoId);
    }

    @GetMapping("/{id}")
    public UsuarioResponse getUsuarioById(@PathVariable("id") int id) {
        return UsuarioResponse.of(
            usuarioService.findCompleteById(id), usuarioService.getFuncionalidadeByUsuario(id).stream()
                .map(FuncionalidadeResponse::getRole).collect(Collectors.toList()));
    }

    @PostMapping("/buscar-todos")
    public List<UsuarioResponse> getUsuariosById(@RequestBody List<Integer> ids) {
        return usuarioService.getUsuariosByIdsTodasSituacoes(ids);
    }

    @RequestMapping(params = "nivel", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuarioByNivel(@RequestParam CodigoNivel nivel) {
        return usuarioService.getUsuarioByNivel(nivel);
    }

    @GetMapping(value = "ids", params = "nivel")
    public List<Integer> getUsuariosIdsByNivel(@RequestParam CodigoNivel nivel) {
        return usuarioService.getUsuariosIdsByNivel(nivel);
    }

    @GetMapping("{id}/cidades")
    public List<CidadeResponse> getCidadesByUsuario(@PathVariable int id) {
        return usuarioService.findCidadesByUsuario(id);
    }

    @GetMapping("nivel/canal")
    public List<UsuarioResponse> getUsuariosOperacaoCanalAa(@RequestParam CodigoNivel codigoNivel) {
        return usuarioService.getUsuariosOperacaoCanalAa(codigoNivel);
    }

    @GetMapping(value = "/{id}/subclusters")
    public List<SelectResponse> getSubclustersUsuario(@PathVariable("id") int usuarioId) {
        return usuarioService.getSubclusterUsuario(usuarioId);
    }

    @GetMapping(value = "/{id}/ufs")
    public List<SelectResponse> getUfsUsuario(@PathVariable("id") int usuarioId) {
        return usuarioService.getUfUsuario(usuarioId);
    }

    @RequestMapping(value = "/{id}/subordinados", method = RequestMethod.GET)
    public List<Integer> getSubordinados(@PathVariable("id") int id,
                                         @RequestParam(required = false, defaultValue = "false") boolean incluirProprio) {
        return usuarioService.getIdDosUsuariosSubordinados(id, incluirProprio);
    }

    @GetMapping("/coordenadores/subordinados")
    public List<Integer> getUsuariosSubordinadosIdsPorCoordenadoresIds(@RequestParam List<Integer> coordenadoresIds) {
        return usuarioService.getUsuariosSubordinadosIdsPorCoordenadoresIds(coordenadoresIds);
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

    @GetMapping("/hierarquia/subordinados-aas")
    public List<UsuarioHierarquiaDto> getSubordinadosAndAasDoUsuario(
        @RequestParam(required = false, defaultValue = "false") boolean incluirInativos) {
        return usuarioService.getSubordinadosAndAasDoUsuario(incluirInativos);
    }

    @GetMapping("/hierarquia/subordinados/gerente/{id}")
    public List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(@PathVariable Integer id) {
        return usuarioService.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(id);
    }

    @GetMapping("/executivos-comerciais")
    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial(CodigoCargo cargo) {
        return usuarioService.findAllExecutivosOperacaoDepartamentoComercial(cargo);
    }

    @GetMapping("responsaveis-ddd")
    public List<UsuarioAutoComplete> findAllResponsaveisDdd() {
        return usuarioService.findAllResponsaveisDdd();
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

    @PostMapping("ativos")
    public List<Integer> getUsuariosAtivosByIds(@RequestBody List<Integer> ids) {
        return usuarioService.getUsuariosAtivosByIds(ids);
    }

    @GetMapping("todas-situacoes")
    public List<UsuarioResponse> getUsuariosByIdsTodasSituacoes(@RequestParam Set<Integer> ids) {
        return usuarioService.getUsuariosByIdsTodasSituacoes(ids);
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

    @GetMapping(value = "obter-usuario-por-email", params = "email")
    public UsuarioResponse findUsuarioByEmailComSituacaoAtivoOuInativo(@RequestParam String email) {
        return usuarioService.findUsuarioByEmailComSituacaoAtivoOuInativo(email);
    }

    @PostMapping("emails")
    public List<UsuarioResponse> getUsuariosByEmails(@RequestBody List<String> emails,
                                                     @RequestParam(required = false) Boolean buscarAtivo) {
        return usuarioService.findByEmails(emails, buscarAtivo);
    }

    @GetMapping(params = "cpf")
    public UsuarioResponse getUsuarioByCpf(@RequestParam String cpf, @RequestParam(required = false) Boolean buscarAtivo) {
        Optional<UsuarioResponse> cpfAaOpt = usuarioService.findByCpfAa(cpf, buscarAtivo);
        return cpfAaOpt.orElse(null);
    }

    @GetMapping(value = "obter-usuario-por-cpf", params = "cpf")
    public UsuarioResponse findUsuarioByCpfComSituacaoAtivoOuInativo(@RequestParam String cpf) {
        return usuarioService.findUsuarioByCpfComSituacaoAtivoOuInativo(cpf);
    }

    @GetMapping(params = {"cpf", "situacao"})
    public UsuarioResponse findByCpfAndSituacaoIsNot(@RequestParam String cpf, @RequestParam ESituacao situacao) {
        return usuarioService.findByCpfAndSituacaoIsNot(cpf, situacao);
    }

    @PostMapping("cpfs")
    public List<UsuarioResponse> getUsuariosByCpfs(@RequestBody List<String> cpfs,
                                                   @RequestParam(required = false) Boolean buscarAtivo) {
        return usuarioService.findByCpfs(cpfs, buscarAtivo);
    }

    @GetMapping("atual/cpf")
    public UsuarioResponse buscarAtualByCpf(@RequestParam String cpf) {
        return usuarioService.buscarAtualByCpf(cpf);
    }

    @GetMapping("nao-realocado")
    public UsuarioResponse buscarNaoRealocadosPorCpf(@RequestParam String cpf) {
        return usuarioService.buscarNaoRealocadoByCpf(cpf);
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
        return usuarioService.getUsuarioByPermissaoEspecial(funcionalidade);
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
    public void esqueceuSenha(@RequestBody UsuarioDadosAcessoRequest dto) { //AQUI
        usuarioServiceEsqueciSenha.enviarConfirmacaoResetarSenha(dto.getEmailAtual());
    }

    @GetMapping(value = "/resetar-senha")
    public void resetarSenha(@RequestParam String token) {
        usuarioServiceEsqueciSenha.resetarSenha(token);
    } //AQUI 2

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

    @GetMapping("canais/organizacao")
    public List<SelectResponse> getCanaisPermitidosParaOrganizacao() {
        return usuarioService.getCanaisPermitidosParaOrganizacao();
    }

    @GetMapping("tipos-canal")
    public List<SelectResponse> getTiposCanal() {
        return usuarioService.getTiposCanalOptions();
    }

    @GetMapping("{usuarioId}/subordinados/cargo/{codigoCargo}")
    public List<Integer> getIdsDaHierarquia(@PathVariable Integer usuarioId, @PathVariable String codigoCargo) {
        return usuarioService.getIdsSubordinadosDaHierarquia(usuarioId, Set.of(codigoCargo));
    }

    @GetMapping("{usuarioId}/subordinados/cargos")
    public List<Integer> getIdsDasHierarquias(@PathVariable Integer usuarioId, @RequestParam Set<String> codigosCargos) {
        return usuarioService.getIdsSubordinadosDaHierarquia(usuarioId, codigosCargos);
    }

    @GetMapping("{id}/vendedores-hierarquia-ids")
    public List<Integer> getIdsVendedoresDaHierarquia(@PathVariable Integer id) {
        return usuarioService.getIdsVendedoresOperacaoDaHierarquia(id);
    }

    @GetMapping("{id}/vendedores-hierarquia")
    public List<UsuarioHierarquiaResponse> getVendedoresDaHierarquia(@PathVariable Integer id) {
        return usuarioService.getVendedoresOperacaoDaHierarquia(id);
    }

    @GetMapping("{id}/supervisores-hierarquia")
    public List<UsuarioHierarquiaResponse> getSupervisoresDaHierarquia(@PathVariable Integer id) {
        return usuarioService.getSupervisoresOperacaoDaHierarquia(id);
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
    public List<UsuarioDisponivelResponse> getUsuariosDisponiveis(@PathVariable Integer agenteAutorizadoId) {
        return usuarioAgendamentoService.recuperarUsuariosDisponiveisParaDistribuicao(agenteAutorizadoId);
    }

    @GetMapping("distribuicao/agendamentos/equipe-venda/{equipeVendaId}")
    public List<UsuarioDistribuicaoResponse> getUsuariosParaDistribuicaoByEquipeVendaId(@PathVariable Integer equipeVendaId) {
        return usuarioAgendamentoService.getUsuariosParaDistribuicaoByEquipeVendaId(equipeVendaId);
    }

    @SuppressWarnings("LineLength")
    @GetMapping("distribuicao/agendamentos/{usuarioId}/agenteautorizado/{agenteAutorizadoId}")
    public List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosParaDistribuicaoDeAgendamentos(@PathVariable Integer usuarioId,
                                                                                                      @PathVariable Integer agenteAutorizadoId,
                                                                                                      @RequestParam String tipoContato) {
        return usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(usuarioId, agenteAutorizadoId, tipoContato);
    }

    @GetMapping("usuario-funil-prospeccao")
    public FunilProspeccaoUsuarioDto findUsuarioProspeccaoByCidade(@RequestParam String cidade) {
        return usuarioFunilProspeccaoService.findUsuarioDirecionadoByCidade(cidade);
    }

    @GetMapping("executivos")
    public List<UsuarioExecutivoResponse> findUsuariosExecutivos() {
        return usuarioService.buscarExecutivosPorSituacao(ESituacao.A);
    }

    @GetMapping("ids/alvo/comunicado")
    public List<Integer> findUsuarioIdsAlvoDosComunicados(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        return usuarioService.getIdDosUsuariosAlvoDoComunicado(usuarioFiltros);
    }

    @GetMapping("cidades")
    public List<UsuarioCidadeDto> findCidadesDoUsuarioLogado() {
        return usuarioService.findCidadesDoUsuarioLogado();
    }

    @GetMapping("alvo/comunicado")
    public List<UsuarioNomeResponse> findUsuarioAlvoDosComunicados(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        return usuarioService.getUsuariosAlvoDoComunicado(usuarioFiltros);
    }

    @GetMapping("{id}/sem-permissoes")
    public UsuarioResponse findById(@PathVariable Integer id) {
        return usuarioService.findById(id);
    }

    @GetMapping("/cargo/{codigoCargo}")
    public List<UsuarioResponse> findUsuariosByCodigoCargo(@PathVariable CodigoCargo codigoCargo) {
        return usuarioService.findUsuariosByCodigoCargo(codigoCargo);
    }

    @GetMapping("cargos")
    public List<Integer> findIdUsuariosAtivosByCodigoCargos(@RequestParam List<CodigoCargo> codigoCargos) {
        return usuarioService.findIdUsuariosAtivosByCodigoCargos(codigoCargos);
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
    public List<SelectResponse> findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(
        @RequestParam Integer organizacaoId,
        @RequestParam(required = false, defaultValue = "true") boolean buscarInativos,
        @RequestParam(required = false) List<CodigoCargo> cargos) {

        return usuarioService.findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(organizacaoId, buscarInativos, cargos);
    }

    @GetMapping("suporte-vendas/operadores/{organizacaoId}")
    public List<SelectResponse> findOperadoresSuporteVendasByOrganizacao(@PathVariable Integer organizacaoId) {
        return usuarioService.findOperadoresSuporteVendasByOrganizacao(organizacaoId);
    }

    @GetMapping("bko-centralizado/{fornecedorId}")
    public List<UsuarioResponse> findOperadoresBkoCentralizadoByFornecedor(
        @PathVariable Integer fornecedorId,
        @RequestParam(required = false, defaultValue = "false") boolean buscarInativos) {
        return usuarioService.findOperadoresBkoCentralizadoByFornecedor(fornecedorId, buscarInativos);
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

    @GetMapping("{canal}/cargo/{codigoCargo}")
    public List<UsuarioNomeResponse> buscarUsuariosPorCanalECargo(@PathVariable ECanal canal,
                                                                  @PathVariable CodigoCargo codigoCargo) {
        return usuarioService.buscarUsuariosPorCanalECargo(canal, codigoCargo);
    }

    @GetMapping("usuarios-superiores/{usuarioId}")
    public List<UsuarioCargoResponse> buscarUsuariosSuperiores(@PathVariable Integer usuarioId) {
        return usuarioService.getSuperioresPorId(usuarioId);
    }

    @GetMapping("alterar-situacao-usuario-bloqueado/{usuarioId}")
    public void alterarSituacaoUsuarioBLoqueado(@PathVariable Integer usuarioId) {
        deslogarUsuarioPorExcessoDeUsoService.atualizarSituacaoUsuarioBloqueado(usuarioId);
    }

    @GetMapping("vendedores-receptivos")
    public List<SelectResponse> getAllVendedoresReceptivos() {
        return usuarioService.buscarTodosVendedoresReceptivos();
    }

    @GetMapping("vendedores-receptivos/por-ids")
    public List<UsuarioVendedorReceptivoResponse> getAllVendedoresReceptivosById(@RequestParam List<Integer> ids) {
        return usuarioService.buscarVendedoresReceptivosPorId(ids);
    }

    @GetMapping("usuarios-receptivos/{id}/organizacao")
    public List<Integer> getAllUsuariosReceptivosIdsByOrganizacaoId(@PathVariable Integer id) {
        return usuarioService.buscarUsuariosReceptivosIdsPorOrganizacaoId(id);
    }

    @GetMapping("permitidos/select/por-filtros")
    public List<SelectResponse> buscarSelectUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(UsuarioFiltros filtros) {
        return usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(filtros);
    }

    @GetMapping("{usuarioId}/subcanal/nivel")
    public UsuarioSubCanalNivelResponse findByUsuarioId(@PathVariable Integer usuarioId) {
        return usuarioService.findByUsuarioId(usuarioId);
    }

    @GetMapping("cpf")
    public UsuarioSubCanalNivelResponse findByCpf(@RequestParam String cpf) {
        return usuarioService.findByCpf(cpf);
    }

    @PostMapping("mover-avatar-minio")
    public void moverAvatarMinio() {
        usuarioService.moverAvatarMinio();
    }

    @GetMapping("d2d")
    public UsuarioSubCanalResponse findUsuarioD2dByCpf(@RequestParam String cpf) {
        return usuarioService.findUsuarioD2dByCpf(cpf);
    }

    @GetMapping("executivos-hierarquia")
    public List<UsuarioNomeResponse> getExecutivosPorCoodenadoresIds(@RequestParam List<Integer> coordenadoresIds) {
        return usuarioService.getExecutivosPorCoordenadoresIds(coordenadoresIds);
    }
}
