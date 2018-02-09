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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/usuarios/gerencia")
public class UsuarioGerenciaController {

    @Autowired
    private UsuarioService service;

    @RequestMapping("{id}")
    public UsuarioDto getById(@PathVariable("id") int id) {
        return UsuarioDto.parse(service.findById(id));
    }

    @RequestMapping(method = RequestMethod.GET)
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

    @RequestMapping(params = "email")
    public UsuarioDto getByEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    @RequestMapping(method = RequestMethod.POST)
    public UsuarioDto save(@Validated @RequestBody UsuarioDto usuario) {
        return service.save(usuario);
    }

    @RequestMapping(value = "/cidades", method = RequestMethod.POST)
    public UsuarioDto saveUsuarioCidades(@Validated @RequestBody UsuarioCidadeSaveDto dto) {
        return service.saveUsuarioCidades(dto);
    }

    @RequestMapping(value = "/hierarquias", method = RequestMethod.POST)
    public UsuarioDto saveUsuarioHierarquia(@Validated @RequestBody UsuarioHierarquiaSaveDto dto) {
        return service.saveUsuarioHierarquia(dto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void alterar(@Validated @RequestBody UsuarioDto usuario) {
        service.save(usuario);
    }

    @RequestMapping(value = "/inativar", method = RequestMethod.POST)
    public void inativar(@Validated @RequestBody UsuarioInativacaoDto dto) {
        service.inativar(dto);
    }

    @RequestMapping(value = "/ativar", method = RequestMethod.PUT)
    public void ativar(@Validated @RequestBody UsuarioAtivacaoDto dto) {
        service.ativar(dto);
    }

    @RequestMapping(value = "/{idUsuario}/permissoes", method = RequestMethod.GET)
    public List<FuncionalidadeResponse> getFuncionalidadeByUsuario(@PathVariable Integer idUsuario) {
        return service.getFuncionalidadeByUsuario(idUsuario);
    }
}
