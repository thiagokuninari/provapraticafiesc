package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.IUsuarioHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHierarquiaAtivoService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CanalService {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private AutenticacaoService autenticacaoService;

    private Map<ECanal, Class<? extends IUsuarioHierarquia>> usuarioHierarquia = ImmutableMap.of(
        ECanal.ATIVO_PROPRIO, UsuarioHierarquiaAtivoService.class
    );

    public IUsuarioHierarquia usuarioHierarquia() {
        var service = Optional
            .ofNullable(usuarioHierarquia.get(autenticacaoService.getUsuarioCanal()))
            .orElseThrow(() -> new NotImplementedException("Funcionalidade não disponível para canal selecionado"));
        return context.getBean(service);
    }

}
