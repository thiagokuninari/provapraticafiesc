package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public UsuarioResponse getUsuarioById(@PathVariable("id") int id) {
        return UsuarioResponse.convertFrom(
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

    @RequestMapping(value = "/{id}/subordinados", method = RequestMethod.GET)
    public List<Integer> getSubordinados(@PathVariable("id") int id,
            @RequestParam boolean incluirProprio) {
        return usuarioService.getIdDosUsuariosSubordinados(id, incluirProprio);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public List<UsuarioDto> getUsuariosFilter(UsuarioFiltrosDto usuarioFiltrosDto) {
        return usuarioService.getUsuariosFiltros(usuarioFiltrosDto);
    }

    @RequestMapping(params = "ids", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByIds(@RequestParam List<Integer> ids) {
        return usuarioService.getUsuariosByIds(ids);
    }

    @RequestMapping(params = "email", method = RequestMethod.GET)
    public UsuarioResponse getUsuarioByEmail(@RequestParam String email) {
        return usuarioService.findByEmailAa(email);
    }

    @RequestMapping(params = "cpf", method = RequestMethod.GET)
    public UsuarioResponse getUsuarioByCpf(@RequestParam String cpf) {
        return usuarioService.findByCpfAa(cpf);
    }

    @RequestMapping(value = "/{id}/empresas", method = RequestMethod.GET)
    public List<EmpresaResponse> getEmpresasDoUsuario(@PathVariable("id") int id) {
        return usuarioService.findEmpresasDoUsuario(id);
    }

    @RequestMapping(value = "/hierarquia/supervisores", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosSupervisores(UsuarioFiltrosHierarquia filtrosHierarquia) {
        return usuarioService.getUsuariosSuperiores(filtrosHierarquia);
    }

    @RequestMapping(params = "funcionalidade", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByPermissao(
            @RequestParam CodigoFuncionalidade funcionalidade) {
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

    @RequestMapping(value = "/remover-configuracao", method = RequestMethod.PUT)
    public void removerConfiguracao(@RequestBody UsuarioConfiguracaoDto dto) {
        usuarioService.removerConfiguracao(dto);
    }

    @RequestMapping(value = "/esqueci-senha", method = RequestMethod.PUT)
    public void esqueceuSenhaPorEmail(@RequestBody UsuarioDadosAcessoRequest dto) {
        usuarioService.esqueceuSenhaPorEmail(dto.getEmailAtual());
    }
}
