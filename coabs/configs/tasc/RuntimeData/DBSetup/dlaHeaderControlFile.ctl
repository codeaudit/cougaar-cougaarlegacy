
load data
infile 'header.csv' 
into table header
fields terminated by ','  optionally enclosed by '"'
(id, nsn, aac, ssc, icc, nomenclature)


