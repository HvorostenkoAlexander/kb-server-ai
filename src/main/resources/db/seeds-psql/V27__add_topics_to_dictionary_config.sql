-- Допуски по плоскостности для ДТ в ЦТС( NSD_tol_evenness_dt )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=120630055
WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-tol-evenness-dt.0',' /nsi/dict/nsd_tol_evenness_dt', true)
        RETURNING id)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
VALUES ((select id from dc), 1)
     , ((select id from dc), 955)
     , ((select id from dc), 416)
     , ((select id from dc), 587)
     , ((select id from dc), 43)

     , ((select id from dc), 598)
     , ((select id from dc), 599)
     , ((select id from dc), 138)
;

-- Допуски по толщине для ДТ в ЦТС( NSD_tol_thick_dt )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=120629985
WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-tol-thick-dt.0',' /nsi/dict/nsd_tol_thick_dt', true)
        RETURNING id)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
VALUES ((select id from dc), 955)
     , ((select id from dc), 586)
     , ((select id from dc), 587)

     , ((select id from dc), 588)
     , ((select id from dc), 589)
     , ((select id from dc), 590)
     , ((select id from dc), 1779)
     , ((select id from dc), 985)
     , ((select id from dc), 138)
;

-- Допуски по ширине для ДТ в ЦТС( NSD_tol_width_dt )
-- https://confluence.nlmk.com/pages/viewpage.action?pageId=120630028
WITH dc AS (
    INSERT INTO dictionary_config (topic, nsi_path, is_enabled)
        VALUES ('000-0.l3-pdm.cdc.sp-tol-width-dt.0',' /nsi/dict/nsd_tol_width_dt', true)
        RETURNING id)
INSERT
INTO dictionary_config_codes (dictionary_config_id, codes)
VALUES ((select id from dc), 1)
     , ((select id from dc), 3)
     , ((select id from dc), 955)
     , ((select id from dc), 586)
     , ((select id from dc), 587)
     , ((select id from dc), 591)
     , ((select id from dc), 593)

     , ((select id from dc), 592)
     , ((select id from dc), 431)
     , ((select id from dc), 234)
     , ((select id from dc), 233)
     , ((select id from dc), 594)
     , ((select id from dc), 1176)
     , ((select id from dc), 1037)
     , ((select id from dc), 138)
;