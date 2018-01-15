//package br.com.xbrain.autenticacao.config.tests;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.annotation.PostConstruct;
//
//
//@Configuration
//@Profile("test")
//public class RestartSequencesDatabase {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @PostConstruct
//    public void restart() throws Exception {
//        jdbcTemplate.execute(
//                "DECLARE "
//                        + " v_str VARCHAR2 (1000); "
//                        + "BEGIN "
//                        + "  FOR rec IN (SELECT * FROM   user_sequences) LOOP "
//                        + "    v_str := 'DROP SEQUENCE ' || rec.sequence_name; "
//                        + "    EXECUTE IMMEDIATE v_str; "
//                        + "    v_str := 'CREATE SEQUENCE ' || rec.sequence_name || ' "
//                        + "INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999999999999999999999 MINVALUE 0 CACHE 20'; "
//                        + "    EXECUTE IMMEDIATE v_str; "
//                        + "  END LOOP; "
//                        + "END;");
//    }
//}
