package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.ImportacaoUsuarioService;
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
    public List<UsuarioImportacaoResponse> uploadUsuario(@RequestParam MultipartFile file,
                                                    @RequestParam("senhaPadrao") String senhaPadrao) {
        Boolean isSenhaPadrao;
        try {
            isSenhaPadrao = objectMapper.readValue(senhaPadrao, Boolean.class);
        } catch (IOException ex) {
            isSenhaPadrao = false;
        }
        return importacaoUsuarioService.salvarUsuarioFile(file, isSenhaPadrao);
    }
}
