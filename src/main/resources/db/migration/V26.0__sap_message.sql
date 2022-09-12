
ALTER TABLE public.sap_message ADD COLUMN order_num VARCHAR;
COMMENT ON COLUMN public.sap_message.order_num IS E'Номер заказа (если есть в сообщении order)';

CREATE INDEX IF NOT EXISTS sap_message_order_num_idx ON public.sap_message (order_num);
