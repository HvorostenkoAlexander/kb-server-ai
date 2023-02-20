-- Допуски по форме слябов( NSD_tol_shape_slab )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=147134079

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-min-number-samp-chem.0', '/nsi/dict/nsd_min_number_samp_chem', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (3711)) v(code)
;


