
load data
infile 'OrgLocationCode.csv' 
into table OrgLocationCode
fields terminated by ',' optionally enclosed by '"'
(OrgLocationId, OrgLocation)
