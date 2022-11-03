
UPDATE dictionary_config
  SET nsi_path = '/nsi/dict/nsd_tol_evenness_dt'
  WHERE topic = '000-0.l3-pdm.cdc.sp-tol-evenness-dt.0';

UPDATE dictionary_config
  SET nsi_path = '/nsi/dict/nsd_tol_thick_dt'
  WHERE topic = '000-0.l3-pdm.cdc.sp-tol-thick-dt.0';

UPDATE dictionary_config
  SET nsi_path = '/nsi/dict/nsd_tol_width_dt'
  WHERE topic = '000-0.l3-pdm.cdc.sp-tol-width-dt.0';
