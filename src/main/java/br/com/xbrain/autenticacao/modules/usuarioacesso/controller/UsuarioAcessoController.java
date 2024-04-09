package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/usuario-acesso")
public class UsuarioAcessoController {

    private static final String ORIGEM = "Usuário inativado pelo processo manual [endpoint]";
    private final UsuarioAcessoService usuarioAcessoService;

    @GetMapping("inativar")
    @ResponseStatus(HttpStatus.OK)
    public void inativarUsuariosSemAcesso() {
        var usuariosInativados = usuarioAcessoService.inativarUsuariosSemAcesso(ORIGEM);
        log.info("Usuários inativados: {}", usuariosInativados);
    }

    @DeleteMapping("historico")
    public void deletarHistoricoUsuarioAcesso() {
        usuarioAcessoService.deletarHistoricoUsuarioAcesso();
    }

    @GetMapping
    public Page<UsuarioAcessoResponse> filtrar(PageRequest pageRequest, @Validated UsuarioAcessoFiltros usuarioAcessoFiltros) {
        usuarioAcessoFiltros.validarPeriodoMaiorQue30Dias();
        return usuarioAcessoService.getAll(pageRequest, usuarioAcessoFiltros);
    }

    @GetMapping("relatorio")
    public void exportRegistrosToCsv(@Validated UsuarioAcessoFiltros usuarioAcessoFiltros, HttpServletResponse response) {
        this.usuarioAcessoService.exportRegistrosToCsv(response, usuarioAcessoFiltros);
    }

    @PostMapping("usuarios-logados/por-periodo")
    public List<PaLogadoDto> getTotalUsuariosLogadosPorPeriodo(@RequestBody UsuarioLogadoRequest usuarioLogadoRequest) {
        return usuarioAcessoService.getTotalUsuariosLogadosPorPeriodoByFiltros(usuarioLogadoRequest);
    }

    @GetMapping("usuarios-logados")
    public List<Integer> getUsuariosLogadosAtual(UsuarioLogadoRequest request) {
        return usuarioAcessoService.getUsuariosLogadosAtualPorIds(request);
    }

    @GetMapping("usuarios-logados-completos")
    public List<UsuarioLogadoResponse> getUsuariosLogadosCompletos(UsuarioLogadoRequest request) {
        return usuarioAcessoService.getUsuariosLogadosCompletos(request);
    }
}
