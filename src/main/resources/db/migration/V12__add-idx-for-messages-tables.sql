CREATE INDEX IF NOT EXISTS ccm_message__idx ON public.ccm_message (topic, partition,msg_offset);
CREATE INDEX IF NOT EXISTS sadim_message__idx ON public.sadim_message (partition, msg_offset);
CREATE INDEX IF NOT EXISTS pdm_message__idx ON public.pdm_message (topic, partition,msg_offset);