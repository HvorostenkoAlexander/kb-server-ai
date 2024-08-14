-- Выбор проверяемой продукции АСАП ЦТС
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=118428454

WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-choice-tested-products.0', '/nsi/dict/nsd_choice_tested_products', true)
        RETURNING id
)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
SELECT dc.id, v.code
  FROM dc
  CROSS JOIN (VALUES (3),
                     (605),
                     (474),
                     (330),
                     (415),
                     (1),
                     (138)) v(code)
;