
load data
infile 'GeoLoc.csv' 
into table GeoLoc
fields terminated by ',' optionally enclosed by '"'
(GeoLocId, Code, Name, Latitude, Longitude)
