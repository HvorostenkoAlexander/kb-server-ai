INSERT INTO dictionary_config
 (id, topic, nsi_path, is_enabled)
VALUES
 (10, 'topic.pdm.dict-1.0', '/nsi/dict/nsd_dict_one', true)
,(20, 'topic.pdm.dict-2.0', '/nsi/dict/nsd_dict_two', false)
;

INSERT INTO dictionary_config_codes
 (dictionary_config_id, codes)
VALUES
 (10,2)
,(10,3)
,(10,138)
,(10,333)
,(20,1)
,(20,3)
 ;
