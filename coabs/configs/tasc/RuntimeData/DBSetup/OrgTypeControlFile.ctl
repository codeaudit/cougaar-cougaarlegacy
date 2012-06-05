
load data
infile 'organizationType.csv' 
into table OrganizationType
fields terminated by ',' optionally enclosed by '"'
(OrgTypeId, OrgType)
