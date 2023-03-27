-- Проверка кодов (ушек) слябов( NSD_coding_slab )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=118428442

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-coding-slab.0', '/nsi/dict/nsd_coding_slab', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (1),
                     (3),
                     (138),
                     (604),
                     (1815),
                     (1816),
                     (3738),
                     (3739),
                     (3792)) v(code)
;
