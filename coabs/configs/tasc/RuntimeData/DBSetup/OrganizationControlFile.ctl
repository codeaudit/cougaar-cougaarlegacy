
load data
infile 'organization.csv' 
into table organization
fields terminated by ',' optionally enclosed by '"'
(OrganizationId, OrgName, ParentName, GeoLocId, OrgTypeId, OrgLocationId)
