UPDATE public.sadim_pre_attestation_param p
SET lclthckng = (
    SELECT array_to_string(
        array(
            SELECT lcl_thckng FROM public.pre_attestation_param_lcl_thckng
            WHERE
            pre_attestation_param_id =p.id),
        ';' )
    )