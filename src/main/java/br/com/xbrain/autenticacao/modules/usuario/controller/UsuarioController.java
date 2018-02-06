package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private Integer getUsuarioId(Principal principal) {
        return Integer.parseInt(principal.getName().split(Pattern.quote("-"))[0]);
    }

    @GetMapping
    public UsuarioAutenticado getUsuario(Principal principal) {
        return new UsuarioAutenticado(
                usuarioService.findById(getUsuarioId(principal)));
    }

    @GetMapping("/{id}")
    public UsuarioAutenticado getUsuarioById(@PathVariable("id") int id) {
        return new UsuarioAutenticado(
                usuarioService.findById(id));
    }

    @GetMapping(value = "/{id}/cidades")
    public List<CidadeResponse> getCidadesByUsuario(@PathVariable("id") int id) {
        return usuarioService.findCidadesByUsuario(id);
    }

    @RequestMapping(value = "/{id}/subordinados", method = RequestMethod.GET)
    public List<Integer> getSubordinados(@PathVariable("id") int id,
                                         @RequestParam boolean incluirProprio) {
        return usuarioService.getIdDosUsuariosSubordinados(id, incluirProprio);
    }

    @RequestMapping(value = "api/usuarios/filter", method = RequestMethod.GET)
    public List<UsuarioDto> getUsuariosFilter(UsuarioFiltrosDto usuarioFiltrosDto) {
        return usuarioService.getUsuariosFiltros(usuarioFiltrosDto);
    }

    @RequestMapping(params = "ids", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByIds(@RequestParam List<Integer> ids) {
        return usuarioService.getUsuariosByIds(ids);
    }

    @RequestMapping(value = "/{id}/cargo/{cargo}", method = RequestMethod.PUT)
    public void alterarCargoUsuario(@PathVariable("id") int id,
                                    @PathVariable("cargo") CodigoCargo codigoCargo) {
        usuarioService.alterarCargoUsuario(id, codigoCargo);
    }

    @RequestMapping(value = "/inativar", method = RequestMethod.POST)
    public void inativar(@Validated @RequestBody UsuarioInativacaoDto dto) {
        usuarioService.inativar(dto);
    }

    @RequestMapping(value = "/ativar", method = RequestMethod.PUT)
    public void ativar(@Validated @RequestBody UsuarioAtivacaoDto dto) {
        usuarioService.ativar(dto);
    }

    @RequestMapping(value = "/{id}/email/{email}", method = RequestMethod.PUT)
    public void alterarEmailUsuario(@PathVariable("id") int id,
                                    @PathVariable("email") String email) {
        usuarioService.alterarEmailUsuario(id, email);
    }

    @RequestMapping(params = "email", method = RequestMethod.GET)
    public UsuarioDto getUsuarioByEmail(@RequestParam String email) {
        return usuarioService.findByEmail(email);
    }

    @RequestMapping(value = "/{id}/empresas", method = RequestMethod.GET)
    public List<EmpresaResponse> getEmpresasDoUsuario(@PathVariable("id") int id) {
        return usuarioService.findEmpresasDoUsuario(id);
    }

}
