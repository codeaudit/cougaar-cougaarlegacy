gawk -F, -f gawkPrototypeFilter.txt 9rs.csv   >  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 11rs.csv  >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 54fs.csv  >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 55fs.csv  >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 77bs.csv  >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 90fs.csv  >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 355fs.csv >> PrototypeDups.ini

rem gawk -F, -f gawkPrototypeFilter.txt 522fs.csv >> PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 60fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 19fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 494fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 421fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 23fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 79fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 75fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 37bs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 96bs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 44fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 336fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 22fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 81fs.csv   >>  PrototypeDups.ini

gawk -F, -f gawkPrototypeFilter.txt 20bs.csv   >>  PrototypeDups.ini

sed s/!/,/g PrototypeDups.ini > PrototypeDuplicates.ini

pause

