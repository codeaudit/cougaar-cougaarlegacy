
load data
infile 'sparesdcr.csv' 
into table airforce_spares_dcr_by_optempo
fields terminated by ',' optionally enclosed by '"'
(part_id, mds, nsn, optempo, demands_per_day)
