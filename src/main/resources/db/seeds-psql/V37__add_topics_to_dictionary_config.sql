-- Макроструктура( NSD_macrosructure )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=118428454

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-macrosructure.0', '/nsi/dict/nsd_macrosructure', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (1),
                     (3),
                     (138),
                     (415),
                     (416),
                     (474),
                     (573),
                     (574),
                     (575),
                     (576),
                     (577),
                     (578),
                     (604),
                     (955),
                     (1815),
                     (1816),
                     (1830),
                     (1835),
                     (1836),
                     (1837)) v(code)
;


