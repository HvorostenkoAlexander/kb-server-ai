-- Коррозионные свойства
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=300840310

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-corrosive-properties.0', '/nsi/dict/nsd_corrosive_properties', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (3),
                     (1),
                     (222),
                     (415),
                     (10758),
                     (10759),
                     (10760),
                     (10761)) v(code)
;