package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.canalnetsales.dto.CanalNetSalesResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/usuarios/gerencia")
public class UsuarioGerenciaController {

    @Autowired
    private UsuarioService service;

    @PostMapping(consumes = {"multipart/form-data"})
    public UsuarioDto save(@RequestPart(value = "usuario") @Validated UsuarioDto usuario,
                           @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return service.save(usuario, foto);
    }

    @PostMapping("backoffice")
    public UsuarioResponse saveBackoffice(@RequestBody @Valid UsuarioBackofficeRequest usuario) {
        return service.salvarUsuarioBackoffice(UsuarioBackofficeRequest.of(usuario));
    }

    @PostMapping("briefing")
    public UsuarioResponse saveBriefing(@RequestBody @Valid UsuarioBriefingRequest usuario) {
        return service.salvarUsuarioBriefing(UsuarioBriefingRequest.of(usuario));
    }

    @PutMapping
    public void alterar(@Validated @RequestBody UsuarioDto usuario) {
        service.save(UsuarioDto.convertFrom(usuario));
    }

    @GetMapping("{id}")
    public UsuarioDto getById(@PathVariable int id) {
        return service.getUsuarioById(id);
    }

    @GetMapping
    public Page<UsuarioConsultaDto> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        return service.getAll(pageRequest, filtros);
    }

    @GetMapping("chamados/usuarios-redirecionamento/{idNivel}")
    public List<UsuarioConsultaDto> getAllXbrainMsoAtivos(@PathVariable Integer idNivel) {
        return service.getAllXbrainMsoAtivos(idNivel);
    }

    @GetMapping("/hierarquia/{nivelId}")
    public List<UsuarioHierarquiaResponse> getUsuariosHierarquia(@PathVariable int nivelId) {
        return service.getUsuariosHierarquia(nivelId);
    }

    @PostMapping(value = "/cargo-superior/{cargoId}")
    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperior(@PathVariable int cargoId,
                                                                    @RequestBody UsuarioCargoSuperiorPost post) {
        return service.getUsuariosCargoSuperior(cargoId, post.getCidadeIds());
    }

    @PostMapping(value = "/cargo-superior/{cargoId}/{canal}")
    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperior(@PathVariable int cargoId,
                                                                    @RequestBody UsuarioCargoSuperiorPost post,
                                                                    @PathVariable Set<ECanal> canal,
                                                                    @RequestParam(required = false) Set<Integer> subCanais) {
        return service.getUsuariosCargoSuperiorByCanalAndSubCanal(cargoId, post, canal, subCanais);
    }

    @GetMapping(params = "email")
    public UsuarioDto getByEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    @PostMapping("/configuracao")
    public UsuarioDto saveConfiguracao(@Validated @RequestBody UsuarioConfiguracaoSaveDto dto) {
        return service.saveUsuarioConfiguracao(dto);
    }

    @PostMapping("/inativar")
    public void inativar(@Validated @RequestBody UsuarioInativacaoDto dto) {
        service.inativar(dto);
    }

    @PutMapping("/ativar")
    public void ativar(@Validated @RequestBody UsuarioAtivacaoDto dto) {
        service.ativar(dto);
    }

    @PutMapping("limpar-cpf/{id}")
    public void limparCpf(@PathVariable Integer id) {
        service.limparCpfUsuario(id);
    }

    @GetMapping("/{idUsuario}/permissoes")
    public UsuarioPermissaoResponse getFuncionalidadeByUsuario(@PathVariable Integer idUsuario) {
        return service.findPermissoesByUsuario(idUsuario);
    }

    @PutMapping("/{idUsuario}/senha")
    public void alterarSenhaEReenviarPorEmail(@PathVariable Integer idUsuario) {
        service.alterarSenhaEReenviarPorEmail(idUsuario);
    }

    @GetMapping("{idUsuario}/cidades")
    public List<CidadeResponse> getCidadesByUsuario(@PathVariable Integer idUsuario) {
        return service.getCidadesByUsuarioId(idUsuario);
    }

    @PutMapping("/acesso/email")
    public void alterarDadosAcessoEmail(@RequestBody UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        service.alterarDadosAcessoEmail(usuarioDadosAcessoRequest);
    }

    @PutMapping("/acesso/senha")
    public Integer alterarDadosAcessoSenha(@RequestBody UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        return service.alterarDadosAcessoSenha(usuarioDadosAcessoRequest);
    }

    @GetMapping("{idUsuario}/supervisor")
    public UsuarioResponse getUsuarioSuperior(@PathVariable Integer idUsuario) {
        return service.getUsuarioSuperior(idUsuario);
    }

    @GetMapping("{idUsuario}/supervisores")
    public List<UsuarioResponse> getUsuarioSuperiores(@PathVariable("idUsuario") Integer idUsuario) {
        return service.getUsuarioSuperiores(idUsuario);
    }

    @GetMapping("/csv")
    public void getCsv(@Validated UsuarioFiltros filtros, HttpServletResponse response) {
        service.exportUsuariosToCsv(filtros, response);
    }

    @GetMapping("existir/usuario")
    public Boolean validarSeUsuarioNovoCadastro(UsuarioExistenteValidacaoRequest usuarioParaValidar) {
        return service.validarSeUsuarioCpfEmailNaoCadastrados(usuarioParaValidar);
    }

    @PutMapping("remanejar-usuario")
    public void remanejarUsuario(@RequestBody UsuarioMqRequest request) {
        service.remanejarUsuario(request);
    }

    @GetMapping("existir/usuario/cpf-email")
    public void validarCpfEmailSocio(@RequestParam String cpf, @RequestParam String email) {
        service.validarSeUsuarioCpfEmailNaoCadastrados(cpf, email);
    }

    @PutMapping("inativar-limpar-dados/socio-principal/{id}")
    public void inativarELimparDadosAntigoSocioPrincipal(@PathVariable Integer id) {
        service.inativarELimparDadosAntigoSocioPrincipal(id);
    }

    @PutMapping("{canalNetSalesId}/migrar-usuarios-associados-ao-canal-net-sales")
    public void migrarDadosNetSales(@PathVariable Integer canalNetSalesId,
                                    @RequestBody CanalNetSalesResponse canalNetSalesResponse) {

        service.migrarDadosNetSales(canalNetSalesId, canalNetSalesResponse);
    }

    @PutMapping("{canalNetSalesId}/atualizar-usuarios-associados-ao-canal-net-sales")
    public void atualizarCanalNetSalesCodigo(@PathVariable Integer canalNetSalesId,
                                             @RequestBody CanalNetSalesResponse canalNetSalesResponse) {

        service.atualizarCanalNetSales(canalNetSalesId, canalNetSalesResponse);
    }
}
