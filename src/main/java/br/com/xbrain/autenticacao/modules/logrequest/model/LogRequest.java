package br.com.xbrain.autenticacao.modules.logrequest.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "LOG_REQUEST")
public class LogRequest {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_LOG_REQUEST", sequenceName = "SEQ_LOG_REQUEST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_LOG_REQUEST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_LOG_REQUEST_USUARIO"),
            referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Column(name = "USUARIO_EMULADOR")
    private Integer usuarioEmulador;
    private String url;
    private String method;
    private String ip;

    @Column(name = "URL_PARAMETROS")
    private String urlParam;

    public static LogRequest build(Integer usuarioId,
                                   String url,
                                   String method,
                                   String urlParam,
                                   Integer usuarioEmulador,
                                   String ip) {
        LogRequest logRequest = new LogRequest();
        logRequest.setUsuario(new Usuario(usuarioId));
        logRequest.setUrl(url);
        logRequest.setMethod(method);
        logRequest.setUsuarioEmulador(usuarioEmulador);
        logRequest.setIp(ip);
        logRequest.setUrlParam(url);
        return logRequest;
    }
}
