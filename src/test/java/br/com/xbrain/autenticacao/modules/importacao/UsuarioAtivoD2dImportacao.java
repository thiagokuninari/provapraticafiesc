package br.com.xbrain.autenticacao.modules.importacao;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Transactional
@SpringBootTest
@Rollback(false)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("importacao")
public class UsuarioAtivoD2dImportacao {

    private Logger log = Logger.getLogger("principal");
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CargoRepository cargoRepository;

    @Test
    @Ignore
    public void importarUsuariosXls() {
        log.info("Lendo arquivo no formato XLS");
        try {
            BufferedInputStream buf = new BufferedInputStream(getFileInputStream("arquivo_usuario/usarios.xlsx"));
            POIFSFileSystem fileSystem = new POIFSFileSystem(buf);
            HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
            HSSFSheet sheet = workbook.getSheetAt(0);
            log.info("Aberto arquivo XLS dos usuários.\nSerá iniciado a leitura de registro por registro");
            // Capturando as linhas
            Iterator linhas = sheet.rowIterator();
            while (linhas.hasNext()) {
                HSSFRow linha = (HSSFRow) linhas.next();
                Iterator celulas = linha.cellIterator();
                Usuario usuario = new Usuario();
                while (celulas.hasNext()) {
                    HSSFCell celula = (HSSFCell) celulas.next();
                    int celulasIndex = celula.getColumnIndex();
                    switch (celulasIndex) {
                        case 0:
                            System.out.println(celula.toString());
                            break;// nivel // celula.toString()
                        case 1:
                            System.out.println(celula.toString());
                            break;// cargo
                        case 2:
                            System.out.println(celula.toString());
                            break;// nome
                        case 3:
                            System.out.println(celula.toString());
                            break;// cpf
                        case 4:
                            System.out.println(celula.toString());
                            break;// email
                        case 5:
                            System.out.println(celula.toString());
                            break;// data nascimento
                        case 6:
                            System.out.println(celula.toString());
                            break;// telefone
                        default:
                            System.out.println("Acabou");
                    }
                }
            }
            log.info("Leitura do arquivo concluído com sucesso.");
        } catch (Exception ex) {
            log.info("Leitura do arquivo deu erro.");
        }
    }

    @Test
    @Ignore
    public void lerArqXlxs() {
        log.info("Lendo arquivo no formato XLXS");
        try {
            InputStream excelFile = new FileInputStream("src/test/resources/arquivo_usuario/plailha1.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(excelFile);
            Sheet sheet = wb.getSheetAt(0);
            Iterator linhas = sheet.rowIterator();
            log.info("Aberto arquivo.\nSerá iniciado a leitura de registro por registro");
            linhas.next();
            while (linhas.hasNext()) {
                XSSFRow linha = (XSSFRow) linhas.next();
                Iterator celulas = linha.cellIterator();
                Usuario usuario = gerarNovoUsuario();
                Nivel nivel = new Nivel();
                while (celulas.hasNext()) {
                    XSSFCell celula = (XSSFCell) celulas.next();
                    int celulasIndex = celula.getColumnIndex();
                    switch (celulasIndex) {
                        case 0:
                            nivel.setId(recuperarIdNivel(celula.toString()));
                            System.out.println(celula.toString());
                            break;// nivel
                        case 1:
                            usuario.setCargo(recuperarCargo(nivel, celula.toString()));
                            usuario.setDepartamento(recuperarDepartamento(nivel));
                            System.out.println(celula.toString());
                            break;// cargo
                        case 2:
                            usuario.setNome(celula.toString());
                            System.out.println(celula.toString());
                            break;// nome
                        case 3:
                            usuario.setCpf(celula.getRawValue());
                            System.out.println(celula.getRawValue());
                            break;// cpf
                        case 4:
                            usuario.setEmail(celula.toString());
                            System.out.println(celula.toString());
                            break;// email
                        case 5:
                            usuario.setNascimento(formatarDataNascimento(celula.toString()));
                            System.out.println(formatarDataNascimento(celula.toString()));
                            break;// data nascimento
                        case 6:
                            usuario.setTelefone(celula.getRawValue());
                            System.out.println(celula.getRawValue());
                            break;// telefone
                        default:
                            System.out.println("Acabou");
                    }
                }
                usuario = salvarUsuario(usuario);
                System.out.println("Usuario salvo com sucesso: " + usuario);
            }
            log.info("Leitura do arquivo no formato XLXS concluído com sucesso.");
        } catch (Exception ex) {
            log.info("Leitura do arquivo deu erro.");
        }
    }

    private Integer recuperarIdNivel(String codigoNivel) {
        switch (codigoNivel) {
            case "ATP":
                return 6;
            case "LOJAS":
                return 7;
            case "RECEPTIVO":
                return 8;
            case "ATIVO LOCAL PROPRIO":
                return 9;
            case "ATIVO LOCAL TERCEIRO":
                return 10;
            default:
                return 0;
        }
    }

    private Cargo recuperarCargo(Nivel nivel, String codigoCargo) {
        if (codigoCargo.equals("VENDEDOR")) {
            switch (nivel.getId()) {
                case 6:
                    return cargoRepository.findOne(59);
                case 7:
                    return cargoRepository.findOne(61);
                case 8:
                    return cargoRepository.findOne(63);
                case 9:
                    return cargoRepository.findOne(65);
                case 10:
                    return cargoRepository.findOne(67);
                default:
                    return new Cargo();
            }
        } else if (codigoCargo.equals("SUPERVISOR")) {
            switch (nivel.getId()) {
                case 6:
                    return cargoRepository.findOne(60);
                case 7:
                    return cargoRepository.findOne(62);
                case 8:
                    return cargoRepository.findOne(64);
                case 9:
                    return cargoRepository.findOne(66);
                case 10:
                    return cargoRepository.findOne(68);
                default:
                    return new Cargo();
            }
        }
        return new Cargo();
    }

    private Departamento recuperarDepartamento(Nivel nivel) {
        switch (nivel.getId()) {
            case 6:
                return new Departamento(55);
            case 7:
                return new Departamento(56);
            case 8:
                return new Departamento(57);
            case 9:
                return new Departamento(58);
            case 10:
                return new Departamento(59);
            default:
                return new Departamento();
        }
    }

    private LocalDateTime formatarDataNascimento(String dataNascimento) {
        try {
            if (StringUtils.isEmpty(dataNascimento)) {
                return null;
            }
            if (!dataNascimento.equals("DATA NASCIMENTO")) {
                DateTimeFormatter dataForm = DateTimeFormatter.ofPattern("dd-MMM-uuuu");
                LocalDate data = LocalDate.parse(dataNascimento, dataForm);
                return data.atStartOfDay();
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    private Usuario gerarNovoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUnidadesNegociosId(Arrays.asList(1, 2)); //PESSOAL - RESIDENCIAL_COMBOS
        usuario.setEmpresasId(Arrays.asList(1, 2, 3)); // CLARO_MOVEL - CLARO_TV - NET
        return usuario;
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
                Files.readAllBytes(Paths.get(
                        getClass().getClassLoader().getResource(file)
                                .getPath())));
    }

    public Usuario salvarUsuario(Usuario usuario) {
        Optional<Usuario> usuarioExistente = repository.findByCpf(usuario.getCpf());
        if (!usuarioExistente.isPresent()) {
            String senhaDescriptografada = getSenhaRandomica(6);
            configurar(usuario, senhaDescriptografada);
            usuario = repository.save(usuario);
            notificacaoService.enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            return usuario;
        }
        return new Usuario();
    }

    private void configurar(Usuario usuario, String senhaDescriptografada) {
        usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSituacao(ESituacao.A);
        if (!usuario.hasUsuarioCadastro()) {
            usuario.setUsuarioCadastro(new Usuario(104707)); // Id Usuario Larissa
        }
    }

    private String getSenhaRandomica(int size) {
        String tag = Long.toString(Math.abs(new Random().nextLong()), 36);
        return tag.substring(0, size);
    }
}
