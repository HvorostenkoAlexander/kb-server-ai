-- Вид образца для контроля макроструктуры( NSD_type_sample_macrostructure )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=162610700

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-type-sample-macrostructure.0', '/nsi/dict/nsd_type_sample_macrostructure', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (1),
                     (138),
                     (248),
                     (604),
                     (3737),
                     (9016)) v(code)
;


