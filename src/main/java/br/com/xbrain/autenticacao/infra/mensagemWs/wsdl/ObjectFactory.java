package br.com.xbrain.autenticacao.infra.mensagemWs.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the MensagemWs.wsdl package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MensagemException_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "MensagemException");
    private final static QName _EnviarEmail_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarEmail");
    private final static QName _EnviarEmailComAnexo_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarEmailComAnexo");
    private final static QName _EnviarEmailComAnexoResponse_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarEmailComAnexoResponse");
    private final static QName _EnviarEmailResponse_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarEmailResponse");
    private final static QName _EnviarSms_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarSms");
    private final static QName _EnviarSmsResponse_QNAME = new QName("http://ws.mensagem.xbrain.com.br/", "enviarSmsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: MensagemWs.wsdl
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MensagemException }
     *
     */
    public MensagemException createMensagemException() {
        return new MensagemException();
    }

    /**
     * Create an instance of {@link EnviarEmail }
     *
     */
    public EnviarEmail createEnviarEmail() {
        return new EnviarEmail();
    }

    /**
     * Create an instance of {@link EnviarEmailComAnexo }
     *
     */
    public EnviarEmailComAnexo createEnviarEmailComAnexo() {
        return new EnviarEmailComAnexo();
    }

    /**
     * Create an instance of {@link EnviarEmailComAnexoResponse }
     *
     */
    public EnviarEmailComAnexoResponse createEnviarEmailComAnexoResponse() {
        return new EnviarEmailComAnexoResponse();
    }

    /**
     * Create an instance of {@link EnviarEmailResponse }
     *
     */
    public EnviarEmailResponse createEnviarEmailResponse() {
        return new EnviarEmailResponse();
    }

    /**
     * Create an instance of {@link EnviarSms }
     *
     */
    public EnviarSms createEnviarSms() {
        return new EnviarSms();
    }

    /**
     * Create an instance of {@link EnviarSmsResponse }
     *
     */
    public EnviarSmsResponse createEnviarSmsResponse() {
        return new EnviarSmsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MensagemException }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "MensagemException")
    public JAXBElement<MensagemException> createMensagemException(MensagemException value) {
        return new JAXBElement<MensagemException>(_MensagemException_QNAME, MensagemException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarEmail }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarEmail")
    public JAXBElement<EnviarEmail> createEnviarEmail(EnviarEmail value) {
        return new JAXBElement<EnviarEmail>(_EnviarEmail_QNAME, EnviarEmail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarEmailComAnexo }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarEmailComAnexo")
    public JAXBElement<EnviarEmailComAnexo> createEnviarEmailComAnexo(EnviarEmailComAnexo value) {
        return new JAXBElement<EnviarEmailComAnexo>(_EnviarEmailComAnexo_QNAME, EnviarEmailComAnexo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarEmailComAnexoResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarEmailComAnexoResponse")
    public JAXBElement<EnviarEmailComAnexoResponse> createEnviarEmailComAnexoResponse(EnviarEmailComAnexoResponse value) {
        return new JAXBElement<EnviarEmailComAnexoResponse>(_EnviarEmailComAnexoResponse_QNAME, EnviarEmailComAnexoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarEmailResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarEmailResponse")
    public JAXBElement<EnviarEmailResponse> createEnviarEmailResponse(EnviarEmailResponse value) {
        return new JAXBElement<EnviarEmailResponse>(_EnviarEmailResponse_QNAME, EnviarEmailResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarSms }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarSms")
    public JAXBElement<EnviarSms> createEnviarSms(EnviarSms value) {
        return new JAXBElement<EnviarSms>(_EnviarSms_QNAME, EnviarSms.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnviarSmsResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.mensagem.xbrain.com.br/", name = "enviarSmsResponse")
    public JAXBElement<EnviarSmsResponse> createEnviarSmsResponse(EnviarSmsResponse value) {
        return new JAXBElement<EnviarSmsResponse>(_EnviarSmsResponse_QNAME, EnviarSmsResponse.class, null, value);
    }

}
