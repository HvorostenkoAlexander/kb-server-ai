-- Допуски по форме слябов( NSD_tol_shape_slab )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=116498692
WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-tol-shape-slab.0', '/nsi/dict/nsd_tol_shape_slab', true)
        RETURNING id)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES   (1),
                     (138),
                     (180),
                     (588),
                     (593),
                     (595),
                     (604),
                     (955),
                     (1004),
                     (1182),
                     (1805),
                     (1180),
                     (1178),
                     (1177),
                     (1175),
                     (1176),
                     (1173),
                     (1806),
                     (1171),
                     (1172),
                     (1181),
                     (1185),
                     (1807),
                     (1808),
                     (1810),
                     (1811),
                     (1812),
                     (1813),
                     (1814),
                     (1815),
                     (1816)) v(code)
;


