-- Микроструктура( NSD_microstructure )
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-microstructure.0',
                    '/nsi/dict/nsd_microstructure',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 3),
       ((select id from dc), 1),
       ((select id from dc), 416),
       ((select id from dc), 417),
       ((select id from dc), 418),
       ((select id from dc), 419),
       ((select id from dc), 420),
       ((select id from dc), 421),
       ((select id from dc), 75),
       ((select id from dc), 73),
       ((select id from dc), 74),
       ((select id from dc), 422),
       ((select id from dc), 423),
       ((select id from dc), 424),
       ((select id from dc), 449),
       ((select id from dc), 425),
       ((select id from dc), 426),
       ((select id from dc), 348),
       ((select id from dc), 427),
       ((select id from dc), 138);

-- Химический состав по стандартам( NSD_asap_chemical_properties )
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-asap-chemical-properties.0',
                    '/nsi/dict/nsd_asap_chemical_propertiese',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 3),
       ((select id from dc), 1),
       ((select id from dc), 9006),
       ((select id from dc), 9014),
       ((select id from dc), 9016),
       ((select id from dc), 9015),
       ((select id from dc), 9013),
       ((select id from dc), 9024),
       ((select id from dc), 9028),
       ((select id from dc), 9029),
       ((select id from dc), 9007),
       ((select id from dc), 9022),
       ((select id from dc), 9041),
       ((select id from dc), 9023),
       ((select id from dc), 9005),
       ((select id from dc), 9042),
       ((select id from dc), 9020),
       ((select id from dc), 9074),
       ((select id from dc), 9033),
       ((select id from dc), 450),
       ((select id from dc), 451),
       ((select id from dc), 452),
       ((select id from dc), 453),
       ((select id from dc), 454),
       ((select id from dc), 455),
       ((select id from dc), 456),
       ((select id from dc), 457),
       ((select id from dc), 458),
       ((select id from dc), 459),
       ((select id from dc), 138);

--Соответствие ТК из заказа и ГМ( NSD_match_tk_num )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-match-tk-num.0',
                    '/nsi/dict/nsd_match_tk_num',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 474),
       ((select id from dc), 222),
       ((select id from dc), 138);

--Соответствие РП из заказа и ГМ( NSD_match_rabplan_num )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-match-rabplan-num.0',
                    '/nsi/dict/nsd_match_rabplan_num',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 475),
       ((select id from dc), 223);

--Коэффициент растрескивания( NSD_Pcm )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-pcm.0',
                    '/nsi/dict/nsd_pcm',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 138),
       ((select id from dc), 585),
       ((select id from dc), 477);

--Физико-механические свойства по стандартам( NSD_asap_mech_properties )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.asap-mech-properties.0',
                    '/nsi/dict/nsd_asap_mech_properties',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc), 478),
       ((select id from dc), 3),
       ((select id from dc), 1),
       ((select id from dc), 416),
       ((select id from dc), 307),
       ((select id from dc), 479),
       ((select id from dc), 480),
       ((select id from dc), 481),
       ((select id from dc), 482),
       ((select id from dc), 483),
       ((select id from dc), 484),
       ((select id from dc), 485),
       ((select id from dc), 338),
        ((select id from dc),486),
        ((select id from dc),339),
        ((select id from dc),487),
        ((select id from dc),488),
        ((select id from dc),489),
        ((select id from dc),490),
        ((select id from dc),491),
        ((select id from dc),492),
        ((select id from dc),493),
        ((select id from dc),494),
        ((select id from dc),495),
        ((select id from dc),496),
        ((select id from dc),497),
        ((select id from dc),498),
        ((select id from dc),499),
        ((select id from dc),500),
        ((select id from dc),501),
        ((select id from dc),502),
        ((select id from dc),503),
        ((select id from dc),504),
        ((select id from dc),70),
        ((select id from dc),69),
        ((select id from dc),505),
        ((select id from dc),506),
        ((select id from dc),340),
        ((select id from dc),507),
        ((select id from dc),508),
        ((select id from dc),509),
        ((select id from dc),510),
        ((select id from dc),511),
        ((select id from dc),512),
        ((select id from dc),513),
        ((select id from dc),514),
        ((select id from dc),515),
        ((select id from dc),516),
        ((select id from dc),517),
        ((select id from dc),518),
        ((select id from dc),519),
        ((select id from dc),520),
        ((select id from dc),521),
        ((select id from dc),522),
        ((select id from dc),523),
        ((select id from dc),524),
        ((select id from dc),68),
        ((select id from dc),525),
        ((select id from dc),526),
        ((select id from dc),527),
        ((select id from dc),528),
        ((select id from dc),529),
        ((select id from dc),530),
        ((select id from dc),531),
        ((select id from dc),532),
        ((select id from dc),533),
        ((select id from dc),534),
        ((select id from dc),535),
        ((select id from dc),536),
        ((select id from dc),537),
        ((select id from dc),538),
        ((select id from dc),539),
        ((select id from dc),540),
        ((select id from dc),541),
        ((select id from dc),542),
        ((select id from dc),543),
        ((select id from dc),544),
        ((select id from dc),545),
        ((select id from dc),546),
        ((select id from dc),547),
        ((select id from dc),548),
        ((select id from dc),549),
        ((select id from dc),550),
        ((select id from dc),551),
        ((select id from dc),552),
        ((select id from dc),553),
        ((select id from dc),554),
        ((select id from dc),555),
        ((select id from dc),556),
        ((select id from dc),557),
        ((select id from dc),558),
        ((select id from dc),559),
        ((select id from dc),560),
        ((select id from dc),561),
        ((select id from dc),562),
        ((select id from dc),563),
        ((select id from dc),564),
        ((select id from dc),565),
        ((select id from dc),566),
        ((select id from dc),567),
        ((select id from dc),568),
        ((select id from dc),569),
        ((select id from dc),570),
        ((select id from dc),571),
        ((select id from dc),572),
        ((select id from dc),573),
        ((select id from dc),574),
        ((select id from dc),575),
        ((select id from dc),576),
        ((select id from dc),577),
        ((select id from dc),578),
        ((select id from dc),579),
        ((select id from dc),138);

--'Таблица ссылок АСАП для допусков( NSD_asap_tol_links )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-asap-tol-links.0',
                    '/nsi/dict/nsd_asap_tol_links',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),1),
       ((select id from dc),220),
       ((select id from dc),580),
       ((select id from dc),581),
       ((select id from dc),582),
       ((select id from dc),583),
       ((select id from dc),138);

--Определение категории по марке стали ГОСТ 4041-2017( NSD_kat_steel_mark_gost4041 )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-kat-steel-mark-gost4041.0',
                    '/nsi/dict/nsd_kat_steel_mark_gost4041',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),3),
       ((select id from dc),416),
       ((select id from dc),417);

--'Эквиваленты( NSD_equivalents )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-equivalents.0',
                    '/nsi/dict/nsd_equivalents',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),1),
       ((select id from dc),3),
       ((select id from dc),416),
       ((select id from dc),491),
       ((select id from dc),584),
       ((select id from dc),464),
       ((select id from dc),585),
       ((select id from dc),465),
       ((select id from dc),138);

--Допуски по толщине( NSD_tol_thick )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-tol-thick.0',
                    '/nsi/dict/nsd_tol_thick',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),416),
       ((select id from dc),432),
       ((select id from dc),491),
       ((select id from dc),5),
       ((select id from dc),3),
       ((select id from dc),586),
       ((select id from dc),587),
       ((select id from dc),588),
       ((select id from dc),53),
       ((select id from dc),230),
       ((select id from dc),229),
       ((select id from dc),589),
       ((select id from dc),590),
       ((select id from dc),138);

--'Допуски по ширине( NSD_tol_width )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-tol-width.0',
                    '/nsi/dict/nsd_tol_width',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),416),
       ((select id from dc),432),
       ((select id from dc),491),
       ((select id from dc),5),
       ((select id from dc),3),
       ((select id from dc),586),
       ((select id from dc),587),
       ((select id from dc),588),
       ((select id from dc),53),
       ((select id from dc),230),
       ((select id from dc),229),
       ((select id from dc),589),
       ((select id from dc),590),
       ((select id from dc),138);

--'Допуски по длине( NSD_tol_length )
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-tol-length.0',
                    '/nsi/dict/nsd_tol_length',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),5),
       ((select id from dc),592),
       ((select id from dc),416),
       ((select id from dc),595),
       ((select id from dc),237),
       ((select id from dc),596),
       ((select id from dc),597),
       ((select id from dc),138);

--'Допуски по плоскостности( NSD_tol_evenness )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-tol-evenness.0',
                    '/nsi/dict/nsd_tol_evenness',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),5),
       ((select id from dc),587),
       ((select id from dc),416),
       ((select id from dc),43),
       ((select id from dc),53),
       ((select id from dc),598),
       ((select id from dc),599),
       ((select id from dc),490),
       ((select id from dc),138);

--'Номера документов( NSD_tk_num )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-tk-num.0',
                    '/nsi/dict/nsd_tk_num',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),222),
       ((select id from dc),353),
       ((select id from dc),630),
       ((select id from dc),631);

--'Механика( NSD_mech_properties )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-mech-properties.0',
                    '/nsi/dict/nsd_mech_properties',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),604),
       ((select id from dc),222),
       ((select id from dc),415),
       ((select id from dc),478),
       ((select id from dc),3),
       ((select id from dc),1),
       ((select id from dc),605),
       ((select id from dc),606),
       ((select id from dc),416),
       ((select id from dc),307),
       ((select id from dc),479),
       ((select id from dc),480),
       ((select id from dc),481),
       ((select id from dc),482),
       ((select id from dc),483),
       ((select id from dc),485),
       ((select id from dc),484),
       ((select id from dc),491),
       ((select id from dc),490),
       ((select id from dc),492),
       ((select id from dc),493),
       ((select id from dc),494),
       ((select id from dc),338),
       ((select id from dc),486),
       ((select id from dc),339),
       ((select id from dc),487),
       ((select id from dc),488),
       ((select id from dc),489),
       ((select id from dc),496),
       ((select id from dc),495),
       ((select id from dc),497),
       ((select id from dc),69),
       ((select id from dc),70),
       ((select id from dc),505),
       ((select id from dc),504),
       ((select id from dc),506),
       ((select id from dc),503),
       ((select id from dc),498),
       ((select id from dc),499),
       ((select id from dc),500),
       ((select id from dc),501),
       ((select id from dc),502),
       ((select id from dc),509),
       ((select id from dc),510),
       ((select id from dc),511),
       ((select id from dc),512),
       ((select id from dc),513),
       ((select id from dc),514),
       ((select id from dc),515),
       ((select id from dc),516),
       ((select id from dc),517),
       ((select id from dc),518),
       ((select id from dc),519),
       ((select id from dc),520),
       ((select id from dc),521),
       ((select id from dc),522),
       ((select id from dc),523),
       ((select id from dc),524),
       ((select id from dc),68),
       ((select id from dc),508),
       ((select id from dc),340),
       ((select id from dc),507),
       ((select id from dc),525),
       ((select id from dc),526),
       ((select id from dc),527),
       ((select id from dc),530),
       ((select id from dc),529),
       ((select id from dc),531),
       ((select id from dc),528),
       ((select id from dc),532),
       ((select id from dc),533),
       ((select id from dc),534),
       ((select id from dc),535),
       ((select id from dc),536),
       ((select id from dc),537),
       ((select id from dc),538),
       ((select id from dc),539),
       ((select id from dc),542),
       ((select id from dc),543),
       ((select id from dc),544),
       ((select id from dc),545),
       ((select id from dc),546),
       ((select id from dc),547),
       ((select id from dc),540),
       ((select id from dc),541),
       ((select id from dc),548),
       ((select id from dc),549),
       ((select id from dc),550),
       ((select id from dc),551),
       ((select id from dc),552),
       ((select id from dc),553),
       ((select id from dc),554),
       ((select id from dc),555),
       ((select id from dc),556),
       ((select id from dc),557),
       ((select id from dc),558),
       ((select id from dc),559),
       ((select id from dc),560),
       ((select id from dc),561),
       ((select id from dc),629),
       ((select id from dc),562),
       ((select id from dc),563),
       ((select id from dc),564),
       ((select id from dc),565),
       ((select id from dc),566),
       ((select id from dc),567),
       ((select id from dc),568),
       ((select id from dc),569),
       ((select id from dc),570),
       ((select id from dc),571),
       ((select id from dc),572),
       ((select id from dc),573),
       ((select id from dc),574),
       ((select id from dc),575),
       ((select id from dc),576),
       ((select id from dc),577),
       ((select id from dc),578),
       ((select id from dc),579),
       ((select id from dc),138);

--'Химия( NSD_chemical_properties )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-chemical-properties.0',
                    '/nsi/dict/nsd_chemical_properties',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),604),
       ((select id from dc),222),
       ((select id from dc),415),
       ((select id from dc),605),
       ((select id from dc),606),
       ((select id from dc),3),
       ((select id from dc),1),
       ((select id from dc),416),
       ((select id from dc),307),
       ((select id from dc),9006),
       ((select id from dc),9014),
       ((select id from dc),9025),
       ((select id from dc),9016),
       ((select id from dc),9015),
       ((select id from dc),9013),
       ((select id from dc),9024),
       ((select id from dc),9028),
       ((select id from dc),9029),
       ((select id from dc),9022),
       ((select id from dc),9007),
       ((select id from dc),9023),
       ((select id from dc),9041),
       ((select id from dc),9050),
       ((select id from dc),9042),
       ((select id from dc),9005),
       ((select id from dc),9033),
       ((select id from dc),9020),
       ((select id from dc),9001),
       ((select id from dc),9051),
       ((select id from dc),9082),
       ((select id from dc),607),
       ((select id from dc),608),
       ((select id from dc),453),
       ((select id from dc),609),
       ((select id from dc),452),
       ((select id from dc),610),
       ((select id from dc),611),
       ((select id from dc),612),
       ((select id from dc),613),
       ((select id from dc),457),
       ((select id from dc),614),
       ((select id from dc),615),
       ((select id from dc),616),
       ((select id from dc),617),
       ((select id from dc),618),
       ((select id from dc),619),
       ((select id from dc),584),
       ((select id from dc),464),
       ((select id from dc),620),
       ((select id from dc),138),
       ((select id from dc),9083),
       ((select id from dc),9027),
       ((select id from dc),9026),
       ((select id from dc),9012),
       ((select id from dc),9008),
       ((select id from dc),9074),
       ((select id from dc),9030),
       ((select id from dc),9040),
       ((select id from dc),621),
       ((select id from dc),622),
       ((select id from dc),623),
       ((select id from dc),624),
       ((select id from dc),625),
       ((select id from dc),626),
       ((select id from dc),627);

--'Углеродный эквивалент( NSD_CEQ )'
WITH dc
         AS (
        insert into public.dictionary_config (topic, nsi_path, is_enabled)
            values ('000-0.l3-pdm.cdc.sp-ceq.0',
                    '/nsi/dict/nsd_ceq',
                    true)
            RETURNING id
    )
insert
into dictionary_config_codes (dictionary_config_id, codes)
values ((select id from dc),138),
       ((select id from dc),628),
       ((select id from dc),584);