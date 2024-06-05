package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETipoUsuario {
    COLAB_VENDA("COLABORADOR DE VENDAS"),
    COLAB_TECNICO("COLABORADOR TÉCNICO"),
    SOCIO("SÓCIO PRINCIPAL");

    private final String descricao;
}
