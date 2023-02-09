-- Допуски по форме слябов( NSD_tol_shape_slab )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=144109644

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-register-equivalents.0', '/nsi/dict/nsd_register_equivalents', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (138),
                     (476),
                     (609),
                     (1908),
                     (3537),
                     (9005),
                     (9006)) v(code)
;


