package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioServiceEsqueciSenha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioServiceEsqueciSenha usuarioServiceEsqueciSenha;

    private Integer getUsuarioId(Principal principal) {
        return Integer.parseInt(principal.getName().split(Pattern.quote("-"))[0]);
    }

    @GetMapping
    public UsuarioAutenticado getUsuario(Principal principal) {
        return new UsuarioAutenticado(
                usuarioService.findById(getUsuarioId(principal)));
    }

    @PutMapping("ativar-socio")
    public void ativarSocioPrincipal(@RequestParam String email) {
        usuarioService.ativarSocioPrincipal(email);
    }

    @PutMapping("inativar-socio")
    public void inativarSocioPrincipal(@RequestParam String email) {
        usuarioService.inativarSocioPrincipal(email);
    }

    @GetMapping("/autenticado/{id}")
    public UsuarioAutenticado getUsuarioAutenticadoById(@PathVariable("id") int id) {
        return new UsuarioAutenticado(
                usuarioService.findCompleteById(id));
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

    @RequestMapping(value = "/{id}/subordinados/vendas", method = RequestMethod.GET)
    public List<Integer> getSubordinadosVendas(@PathVariable("id") int id) {
        return usuarioService.getIdDosUsuariosSubordinados(id, true);
    }

    @GetMapping("/hierarquia/subordinados/{id}")
    public List<UsuarioSubordinadoDto> getSubordinadosByUsuario(@PathVariable Integer id) {
        return usuarioService.getSubordinadosDoUsuario(id);
    }

    @PostMapping("/vincula/hierarquia")
    public void vincularUsuariosComSuperior(@RequestParam List<Integer> idsUsuarios, @RequestParam Integer idUsuarioSuperior) {
        usuarioService.vincularUsuario(idsUsuarios, idUsuarioSuperior);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public List<UsuarioDto> getUsuariosFilter(UsuarioFiltrosDto usuarioFiltrosDto) {
        return usuarioService.getUsuariosFiltros(usuarioFiltrosDto);
    }

    @RequestMapping(params = "ids", method = RequestMethod.GET)
    public List<UsuarioResponse> getUsuariosByIds(@RequestParam List<Integer> ids) {
        return usuarioService.getUsuariosByIds(ids);
    }

    @GetMapping(params = "email")
    public UsuarioResponse getUsuarioByEmail(@RequestParam String email) {
        Optional<UsuarioResponse> emailAaOptional = usuarioService.findByEmailAa(email);
        return emailAaOptional.orElse(null);

    }

    @GetMapping(params = "cpf")
    public UsuarioResponse getUsuarioByCpf(@RequestParam String cpf) {
        Optional<UsuarioResponse> cpfAaOpt = usuarioService.findByCpfAa(cpf);
        return cpfAaOpt.orElse(null);
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
                .map(item -> SelectResponse.convertFrom(item.name(), item.getDescricao()))
                .sorted(Comparator.comparing(SelectResponse::getLabel))
                .collect(Collectors.toList());
    }
}
