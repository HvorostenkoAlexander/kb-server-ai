
DELETE
    FROM dictionary_config_codes
    WHERE dictionary_config_id in (
        SELECT id
             FROM dictionary_config
             WHERE topic in ('000-0.l3-pdm.cdc.sp-pcm.0', '000-0.l3-pdm.cdc.sp-ceq.0')
    );

DELETE FROM dictionary_config
    WHERE topic in ('000-0.l3-pdm.cdc.sp-pcm.0', '000-0.l3-pdm.cdc.sp-ceq.0');
