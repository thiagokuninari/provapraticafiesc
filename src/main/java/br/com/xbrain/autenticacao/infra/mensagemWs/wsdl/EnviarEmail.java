package br.com.xbrain.autenticacao.infra.mensagemWs.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for enviarEmail complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="enviarEmail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="senha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="emailLista" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="assunto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="conteudo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="prioridade" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enviarEmail", propOrder = {
        "usuario",
        "senha",
        "emailLista",
        "assunto",
        "conteudo",
        "prioridade"
})
public class EnviarEmail {

    protected String usuario;
    protected String senha;
    protected String emailLista;
    protected String assunto;
    protected String conteudo;
    protected Short prioridade;

    /**
     * Gets the value of the usuario property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Sets the value of the usuario property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Gets the value of the senha property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Sets the value of the senha property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSenha(String value) {
        this.senha = value;
    }

    /**
     * Gets the value of the emailLista property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmailLista() {
        return emailLista;
    }

    /**
     * Sets the value of the emailLista property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmailLista(String value) {
        this.emailLista = value;
    }

    /**
     * Gets the value of the assunto property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAssunto() {
        return assunto;
    }

    /**
     * Sets the value of the assunto property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAssunto(String value) {
        this.assunto = value;
    }

    /**
     * Gets the value of the conteudo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Sets the value of the conteudo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setConteudo(String value) {
        this.conteudo = value;
    }

    /**
     * Gets the value of the prioridade property.
     *
     * @return
     *     possible object is
     *     {@link Short }
     *
     */
    public Short getPrioridade() {
        return prioridade;
    }

    /**
     * Sets the value of the prioridade property.
     *
     * @param value
     *     allowed object is
     *     {@link Short }
     *
     */
    public void setPrioridade(Short value) {
        this.prioridade = value;
    }

}