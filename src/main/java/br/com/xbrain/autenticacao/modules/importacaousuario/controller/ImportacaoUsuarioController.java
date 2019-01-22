package br.com.xbrain.autenticacao.modules.importacaousuario.controller;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.importacaousuario.service.ImportacaoUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "api/importacao-usuarios")
public class ImportacaoUsuarioController {

    @Autowired
    ImportacaoUsuarioService importacaoUsuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public List<UsuarioImportacaoResponse> uploadUsuario(
            @RequestParam MultipartFile file,
            @RequestParam("usuarioImportacaoJson") String usuarioImportacaoJson) throws IOException {

        UsuarioImportacaoRequest request = objectMapper.readValue(
                usuarioImportacaoJson, UsuarioImportacaoRequest.class);

        return importacaoUsuarioService.salvarUsuarioFile(file, request);
    }
}
