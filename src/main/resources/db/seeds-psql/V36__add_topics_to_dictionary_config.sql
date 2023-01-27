-- Схемы зачистки слябов( NSD_scheme_stripping_slab )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=118453626

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-scheme-stripping-slab.0', '/nsi/dict/nsd_scheme_stripping_slab', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (1),
                     (3),
                     (138),
                     (248),
                     (250),
                     (272),
                     (278),
                     (416),
                     (433),
                     (434),
                     (572),
                     (604),
                     (909),
                     (955),
                     (1815),
                     (1816),
                     (1877),
                     (1899),
                     (3690),
                     (3691),
                     (3692),
                     (3693),
                     (3694),
                     (3695),
                     (3696),
                     (3697)) v(code)
;


