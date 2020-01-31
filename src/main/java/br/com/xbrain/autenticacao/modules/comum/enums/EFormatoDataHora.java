package br.com.xbrain.autenticacao.modules.comum.enums;

public enum EFormatoDataHora {

    DATA_EXTENSO("EEEE',' d 'de' MMMM 'de' yyyy"),
    DATA_HORA_EXTENSO("dd/MM/yyyy 'às' HH:mm"),
    DATA("dd/MM/yyyy"),
    HORA("HH:mm"),
    DATA_NOT_FORMAT("ddMMyyyy"),
    DATA_NOT_FORMAT_INVERSO("yyyyMMdd"),
    DATA_BANCO("yyyy-MM-dd"),
    DATA_HORA_BANCO("yyyy-MM-dd HH:mm:ss"),
    DATA_HORA("dd/MM/yyyy HH:mm"),
    DATA_HORA_SEG("dd/MM/yyyy HH:mm:ss"),
    DATA_HORA_T("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    DATA_HORA_NOME_ARQUIVO("yyyy-MM-dd_HH-mm-ss"),
    DATA_HORA_NOT_FORMAT("yyyyMMdd_HHmmss");

    private String descricao;

    EFormatoDataHora(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
