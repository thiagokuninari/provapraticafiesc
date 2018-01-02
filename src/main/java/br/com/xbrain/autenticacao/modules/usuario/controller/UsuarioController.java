package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioConsultaDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltros;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    //TODO alterar para dto
    @RequestMapping("{id}")
    public UsuarioDto getById(@PathVariable("id") int id) {
        return UsuarioDto.parse(service.findById(id));
    }

    //TODO verificar se precisa alterar para dto
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

    @RequestMapping(value = "/busca", method = RequestMethod.GET)
    public UsuarioDto getByCpf(@RequestParam String cpf) {
        return service.findByCpf(cpf);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void save(@Validated @RequestBody UsuarioDto usuario) {
        service.save(usuario);
    }
}
