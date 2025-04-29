TRUNCATE TABLE public.result_config
;
INSERT INTO public.result_config
 (topic,avro_name,enabled)
VALUES
 ('000-1.l3-apcs.db.nlmk.verification-results.0','VerificationResults',true)
,('000-1.l3-apcs.db.nlmk.verification-results-pts.0','VerificationResultsPts',true)
;
