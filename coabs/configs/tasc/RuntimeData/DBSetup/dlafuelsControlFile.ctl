load data 
infile 'fuelsdcr.csv' 
into table airforce_fuels_dcr_by_optempo
fields terminated by ','  optionally enclosed by '"'
(fuel_id , mds , 
fuel_nsn , optempo , 
gallons_per_day )
