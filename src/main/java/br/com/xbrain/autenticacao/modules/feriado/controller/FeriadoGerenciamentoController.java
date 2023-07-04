package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.*;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoImportacaoService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/feriado/gerenciar")
public class FeriadoGerenciamentoController {

    private final FeriadoService service;
    private final FeriadoImportacaoService feriadoImportacaoService;

    @GetMapping("obter-feriados")
    public Page<FeriadoResponse> obterFeriadosByFiltros(PageRequest pageRequest, FeriadoFiltros filtros) {
        return service.obterFeriadosByFiltros(pageRequest, filtros);
    }

    @GetMapping("{id}")
    public FeriadoResponse obterFeriadoPorId(@PathVariable("id") Integer id) {
        return service.getFeriadoById(id);
    }

    @PostMapping("salvar")
    @ResponseStatus(HttpStatus.CREATED)
    public FeriadoResponse salvar(@RequestBody @Validated FeriadoRequest request) {
        return service.salvarFeriado(request);
    }

    @PutMapping("editar")
    public FeriadoResponse editar(@RequestBody @Validated FeriadoRequest request) {
        return service.editarFeriado(request);
    }

    @PutMapping("excluir/{id}")
    public void excluirFeriado(@PathVariable Integer id) {
        service.excluirFeriado(id);
    }

    @PostMapping("importar")
    public List<FeriadoImportacaoResponse> importarFeriados(
        @RequestPart MultipartFile file,
        @Validated @RequestPart FeriadoImportacaoRequest feriadoImportacaoRequest) {

        return feriadoImportacaoService.importarFeriadoArquivo(file, feriadoImportacaoRequest);
    }
}
