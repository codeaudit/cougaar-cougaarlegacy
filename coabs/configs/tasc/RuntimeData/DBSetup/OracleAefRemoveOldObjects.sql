--
--  The old users.
--
drop user aircraftassets cascade;
drop user assets cascade;
drop user dla cascade;
drop user forcepackage cascade;
drop user maintenance cascade;
drop user orgchart cascade;
drop user personnel cascade;
drop user prototype cascade;
drop user utc cascade;
drop user utcpersonnel cascade;
--
--  The rollback segment.
--
alter rollback segment aef_rs online;
drop rollback segment aef_rs;
--
--  The T_maintenance tablespace.
--
drop tablespace t_maintenance including contents cascade constraints;

