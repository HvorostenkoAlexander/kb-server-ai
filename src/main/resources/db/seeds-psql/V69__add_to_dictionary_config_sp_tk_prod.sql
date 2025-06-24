-- Вид продукции
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=75575999

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-tk-prod.0', '/nsi/dict/sp_tk_prod', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (4),
                     (916),
                     (219),
                     (917),
                     (918)) v(code)
;