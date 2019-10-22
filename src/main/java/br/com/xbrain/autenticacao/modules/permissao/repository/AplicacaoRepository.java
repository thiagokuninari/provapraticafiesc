package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AplicacaoRepository extends JpaRepository<Aplicacao, Integer> {
}
