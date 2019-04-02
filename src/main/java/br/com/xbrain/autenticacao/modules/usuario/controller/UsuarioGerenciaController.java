package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/usuarios/gerencia")
public class UsuarioGerenciaController {

    @Autowired
    private UsuarioService service;

    @PostMapping(consumes = {"multipart/form-data"})
    public UsuarioDto save(@RequestPart(value = "usuario") @Validated UsuarioDto usuario,
                           @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return service.save(UsuarioDto.convertFrom(usuario), foto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void alterar(@Validated @RequestBody UsuarioDto usuario) {
        service.save(UsuarioDto.convertFrom(usuario));
    }

    @RequestMapping("{id}")
    public UsuarioDto getById(@PathVariable("id") int id) {
        Usuario aa = service.findByIdComAa(id);
        aa.forceLoad();
        return UsuarioDto.convertTo(aa);
    }

    @GetMapping
    public PageImpl<UsuarioConsultaDto> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        Page<Usuario> page = service.getAll(pageRequest, filtros);
        return new PageImpl<>(
                page
                        .getContent()
                        .stream()
                        .map(UsuarioConsultaDto::new)
                        .collect(Collectors.toList()),
                pageRequest,
                page.getTotalElements());
    }

    @RequestMapping(value = "/hierarquia/{nivelId}", method = RequestMethod.GET)
    public List<UsuarioHierarquiaResponse> getUsuariosHierarquia(@PathVariable int nivelId) {
        return service.getUsuariosHierarquia(nivelId);
    }

    @GetMapping(value = "/cargo-superior/{cargoId}")
    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperior(@PathVariable int cargoId) {
        return UsuarioHierarquiaResponse.convertTo(service.getUsuariosCargoSuperior(cargoId));
    }

    @RequestMapping(params = "email")
    public UsuarioDto getByEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    @RequestMapping(value = "/configuracao", method = RequestMethod.POST)
    public UsuarioDto saveConfiguracao(@Validated @RequestBody UsuarioConfiguracaoSaveDto dto) {
        return service.saveUsuarioConfiguracao(dto);
    }

    @RequestMapping(value = "/inativar", method = RequestMethod.POST)
    public void inativar(@Validated @RequestBody UsuarioInativacaoDto dto) {
        service.inativar(dto);
    }

    @RequestMapping(value = "/ativar", method = RequestMethod.PUT)
    public void ativar(@Validated @RequestBody UsuarioAtivacaoDto dto) {
        service.ativar(dto);
    }

    @PutMapping("limpar-cpf/{id}")
    public void limparCpf(@PathVariable Integer id) {
        service.limparCpfUsuario(id);
    }

    @RequestMapping(value = "/{idUsuario}/permissoes", method = RequestMethod.GET)
    public UsuarioPermissaoResponse getFuncionalidadeByUsuario(@PathVariable Integer idUsuario) {
        return service.findPermissoesByUsuario(idUsuario);
    }

    @RequestMapping(value = "/{idUsuario}/senha", method = RequestMethod.PUT)
    public void alterarSenhaEReenviarPorEmail(@PathVariable Integer idUsuario) {
        service.alterarSenhaEReenviarPorEmail(idUsuario);
    }

    @RequestMapping(value = "/{idUsuario}/cidades", method = RequestMethod.GET)
    public List<UsuarioCidadeDto> getCidadesByUsuario(@PathVariable Integer idUsuario) {
        return service.getCidadeByUsuario(idUsuario);
    }

    @RequestMapping(value = "/acesso/email", method = RequestMethod.PUT)
    public void alterarDadosAcessoEmail(@RequestBody UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        service.alterarDadosAcessoEmail(usuarioDadosAcessoRequest);
    }

    @RequestMapping(value = "/acesso/senha", method = RequestMethod.PUT)
    public Integer alterarDadosAcessoSenha(@RequestBody UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        return service.alterarDadosAcessoSenha(usuarioDadosAcessoRequest);
    }

    @RequestMapping(value = "{idUsuario}/supervisor", method = RequestMethod.GET)
    public UsuarioResponse getUsuarioSuperior(@PathVariable("idUsuario") Integer idUsuario) {
        return service.getUsuarioSuperior(idUsuario);
    }

    @RequestMapping(value = "{idUsuario}/supervisores", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuarioSuperiores(@PathVariable("idUsuario") Integer idUsuario) {
        return service.getUsuarioSuperiores(idUsuario);
    }

    @GetMapping("/csv")
    public void getCsv(@Validated UsuarioFiltros filtros, HttpServletResponse response) {
        service.exportUsuariosToCsv(
                service.getAllForCsv(filtros),
                response
        );
    }
}
