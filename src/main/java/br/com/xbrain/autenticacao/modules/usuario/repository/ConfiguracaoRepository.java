package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoRepository extends PagingAndSortingRepository<Configuracao, Integer> {

    Optional<Configuracao> findByUsuario(Usuario usuario);
    
    List<Configuracao> findByRamal(Integer ramal);
}
