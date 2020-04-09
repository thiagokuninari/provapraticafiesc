declare
  v_count number;
  sql_table_creation varchar2(500);
begin
  select count(tname) into v_count from tab where tname = 'USUARIOS_PARA_DESLOGAR';

  if v_count = 0 then
    sql_table_creation := 'CREATE TABLE USUARIOS_PARA_DESLOGAR (USUARIO_ID NUMBER)';
    execute immediate sql_table_creation;
  end if;
end;