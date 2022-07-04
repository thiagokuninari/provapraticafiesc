DELETE FROM cargo_depart_func
WHERE
    fk_cargo IN ( 20, 21, 22, 23, 24,
                  25, 52 )
    AND fk_funcionalidade IN ( 15000, 15001, 15002, 15003, 15006,
                               15007, 15008, 15009, 15010, 15011,
                               15012, 15013, 15014, 20007 );