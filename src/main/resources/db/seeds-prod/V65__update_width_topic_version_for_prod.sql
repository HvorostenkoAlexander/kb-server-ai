TRUNCATE TABLE public.result_config;

INSERT INTO public.result_config (topic, avro_name, enabled)
VALUES ('000-0.l3-apcs.db.nlmk.verification-results.0', 'VerificationResults', true)
     , ('000-0.l3-apcs.db.nlmk.verification-results-pts.0', 'VerificationResultsPts', true)
     , ('000-0.l3-apcs.db.nlmk.verification-results-kc1.0', 'VerificationResultsKc1', true)
     , ('000-0.l3-apcs.db.nlmk.verification-results-kc2.0', 'VerificationResultsKc2', true)
     , ('000-0.l3-apcs.db.verification-results-phpp.0', 'VerificationResultsPhpp', true)
     , ('002-0.l3-apcs.db.verification-results-cgp.0', 'VerificationResultsCgp', true)
;