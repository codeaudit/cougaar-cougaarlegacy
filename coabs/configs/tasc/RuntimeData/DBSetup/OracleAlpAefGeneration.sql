accept sid char prompt 'Please Enter the database connect specifications, without the @ sign:'

drop user Aef cascade;

CREATE USER Aef
IDENTIFIED BY Alp
default tablespace system
temporary tablespace system
profile default account unlock;

grant connect to Aef;
grant resource to Aef;
grant unlimited tablespace to Aef;
alter user Aef default role all;

disconnect;

connect Aef/Alp@&sid;


DROP TABLE FinalizedPhases CASCADE CONSTRAINTS;

CREATE TABLE FinalizedPhases (
       PhaseId              INTEGER NOT NULL,
       OrgId                VARCHAR2(10) NOT NULL,
       AefName              VARCHAR2(20) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       AircraftQuantity     SMALLINT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       StartDate            DATE NULL,
       SortieRate           VARCHAR2(20) NULL,
       PhaseNomenclature    VARCHAR2(40) NULL,
       EndDate              DATE NULL,
       Optempo              VARCHAR2(20) NULL,
       Phase                SMALLINT NULL
);

CREATE INDEX XIF135FinalizedPhases ON FinalizedPhases
(
       OrgId,
       ForcePackageMixId,
       AefName,
       OplanId
);


ALTER TABLE FinalizedPhases
       ADD  ( PRIMARY KEY (PhaseId, OrgId, ForcePackageMixId, AefName, 
              OplanId) ) ;


DROP TABLE FinalizedAefOrganizations CASCADE CONSTRAINTS;

CREATE TABLE FinalizedAefOrganizations (
       OrgId                VARCHAR2(10) NOT NULL,
       AefName              VARCHAR2(20) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       BeddownLocation      VARCHAR2(40) NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       AircraftType         VARCHAR2(20) NULL,
       MissionTypeCode      VARCHAR2(7) NULL
);

CREATE INDEX XIF143FinalizedAefOrganization ON FinalizedAefOrganizations
(
       AefName,
       OplanId
);


ALTER TABLE FinalizedAefOrganizations
       ADD  ( PRIMARY KEY (OrgId, ForcePackageMixId, AefName, OplanId) ) ;


DROP TABLE MissionType CASCADE CONSTRAINTS;

CREATE TABLE MissionType (
       MissionTypeCode      VARCHAR2(7) NOT NULL,
       Description          VARCHAR2(40) NULL
);


ALTER TABLE MissionType
       ADD  ( PRIMARY KEY (MissionTypeCode) ) ;


DROP TABLE OplanPhases CASCADE CONSTRAINTS;

CREATE TABLE OplanPhases (
       OrgId                VARCHAR2(10) NOT NULL,
       AefName              VARCHAR2(20) NOT NULL,
       OplanPhasesId        INTEGER NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       SortieRate           VARCHAR2(20) NULL,
       AircraftQuantity     SMALLINT NULL,
       StartDate            DATE NULL,
       EndDate              DATE NULL,
       Optempo              VARCHAR2(20) NULL,
       PhaseNomenclature    VARCHAR2(40) NULL,
       Phase                SMALLINT NULL
);

CREATE INDEX XIF134OplanPhases ON OplanPhases
(
       OrgId,
       AefName,
       OplanId
);


ALTER TABLE OplanPhases
       ADD  ( PRIMARY KEY (OplanPhasesId, OrgId, AefName, OplanId) ) ;


DROP TABLE AefOrganizations CASCADE CONSTRAINTS;

CREATE TABLE AefOrganizations (
       OrgId                VARCHAR2(10) NOT NULL,
       AefName              VARCHAR2(20) NOT NULL,
       BeddownLocation      VARCHAR2(40) NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       AircraftType         VARCHAR2(20) NULL,
       MissionTypeCode      VARCHAR2(7) NULL
);

CREATE INDEX XIF140AefOrganizations ON AefOrganizations
(
       AefName,
       OplanId
);


ALTER TABLE AefOrganizations
       ADD  ( PRIMARY KEY (OrgId, AefName, OplanId) ) ;


DROP TABLE FinalizedOplanAefs CASCADE CONSTRAINTS;

CREATE TABLE FinalizedOplanAefs (
       AefName              VARCHAR2(20) NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL
);

CREATE INDEX XIF141FinalizedOplanAefs ON FinalizedOplanAefs
(
       OplanId
);


ALTER TABLE FinalizedOplanAefs
       ADD  ( PRIMARY KEY (AefName, OplanId) ) ;


DROP TABLE OplanAefs CASCADE CONSTRAINTS;

CREATE TABLE OplanAefs (
       AefName              VARCHAR2(20) NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL
);

CREATE INDEX XIF139OplanAefs ON OplanAefs
(
       OplanId
);


ALTER TABLE OplanAefs
       ADD  ( PRIMARY KEY (AefName, OplanId) ) ;


DROP TABLE AvailableLift CASCADE CONSTRAINTS;

CREATE TABLE AvailableLift (
       AvailableLiftId      INTEGER NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       DeliveryDate         DATE NULL,
       Volume               INTEGER NULL,
       Weight               FLOAT NULL
);

CREATE INDEX XIF119AvailableLift ON AvailableLift
(
       OplanId
);


ALTER TABLE AvailableLift
       ADD  ( PRIMARY KEY (AvailableLiftId, OplanId) ) ;


DROP TABLE AvailableAircraft CASCADE CONSTRAINTS;

CREATE TABLE AvailableAircraft (
       AvailableAircraftId  INTEGER NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       AircraftType         VARCHAR2(20) NULL,
       Quantity             SMALLINT NULL
);

CREATE INDEX XIF118AvailableAircraft ON AvailableAircraft
(
       OplanId
);


ALTER TABLE AvailableAircraft
       ADD  ( PRIMARY KEY (AvailableAircraftId, OplanId) ) ;


DROP TABLE Oplan CASCADE CONSTRAINTS;

CREATE TABLE Oplan (
       OplanId              VARCHAR2(15) NOT NULL,
       OperationName        VARCHAR2(40) NULL,
       CDate                DATE NULL,
       Priority             VARCHAR2(10) NULL
);


ALTER TABLE Oplan
       ADD  ( PRIMARY KEY (OplanId) ) ;


DROP TABLE AFITCompleteSemaphore CASCADE CONSTRAINTS;

CREATE TABLE AFITCompleteSemaphore (
       AFITCompleteOplanId  VARCHAR2(15) NULL
);


DROP TABLE AFITInitiateSemaphore CASCADE CONSTRAINTS;

CREATE TABLE AFITInitiateSemaphore (
       AFITInitiateOplanId  VARCHAR2(15) NULL
);


DROP TABLE UTCPersonnelRequirements CASCADE CONSTRAINTS;

CREATE TABLE UTCPersonnelRequirements (
       UTCPersonnelRequirementsId NUMBER NOT NULL,
       UTCAFSCId            NUMBER NOT NULL,
       Quantity             NUMBER NOT NULL,
       AircraftQuantity     NUMBER NOT NULL,
       Organization         VARCHAR2(25) NOT NULL,
       AircraftType         VARCHAR2(10) NULL
);

CREATE INDEX XIF114UTCPersonnelRequirements ON UTCPersonnelRequirements
(
       UTCAFSCId
);


ALTER TABLE UTCPersonnelRequirements
       ADD  ( PRIMARY KEY (UTCPersonnelRequirementsId, UTCAFSCId) ) ;


DROP TABLE DeploymentHistory CASCADE CONSTRAINTS;

CREATE TABLE DeploymentHistory (
       DeploymentHistoryId  NUMBER NOT NULL,
       LocationId           NUMBER NOT NULL,
       StartDate            DATE NULL,
       AFSCId               NUMBER NOT NULL,
       EndDate              DATE NULL,
       PersonId             NUMBER NOT NULL
);

CREATE INDEX XIF116DeploymentHistory ON DeploymentHistory
(
       AFSCId,
       PersonId
);

CREATE INDEX XIF85DeploymentHistory ON DeploymentHistory
(
       LocationId
);


ALTER TABLE DeploymentHistory
       ADD  ( PRIMARY KEY (DeploymentHistoryId, LocationId, AFSCId, 
              PersonId) ) ;


DROP TABLE Immunizations CASCADE CONSTRAINTS;

CREATE TABLE Immunizations (
       AFSCId               NUMBER NOT NULL,
       ImmunizationsId      NUMBER NOT NULL,
       PersonId             NUMBER NOT NULL,
       Description          VARCHAR2(30) NOT NULL,
       DateGiven            DATE NULL,
       NextDueDate          DATE NULL
);

CREATE INDEX XIF115Immunizations ON Immunizations
(
       AFSCId,
       PersonId
);


ALTER TABLE Immunizations
       ADD  ( PRIMARY KEY (AFSCId, ImmunizationsId, PersonId) ) ;


DROP TABLE ScheduledTraining CASCADE CONSTRAINTS;

CREATE TABLE ScheduledTraining (
       ScheduledTrainingId  SMALLINT NOT NULL,
       TrainingId           NUMBER NOT NULL,
       AFSCId               NUMBER NULL,
       PersonId             NUMBER NULL,
       StartDate            DATE NULL,
       EndDate              DATE NULL
);

CREATE INDEX XIF111ScheduledTraining ON ScheduledTraining
(
       AFSCId,
       PersonId
);

CREATE INDEX XIF112ScheduledTraining ON ScheduledTraining
(
       TrainingId
);

CREATE INDEX PersonnelIdTrainingId ON ScheduledTraining
(
       PersonId,
       TrainingId
);


ALTER TABLE ScheduledTraining
       ADD  ( PRIMARY KEY (ScheduledTrainingId, TrainingId) ) ;


DROP TABLE TrainingHistory CASCADE CONSTRAINTS;

CREATE TABLE TrainingHistory (
       PersonId             NUMBER NOT NULL,
       TrainingHistoryId    NUMBER NOT NULL,
       AFSCId               NUMBER NOT NULL,
       TrainingId           NUMBER NOT NULL,
       SuccessfullyCompleted NUMBER NOT NULL,
       StartDate            DATE NOT NULL,
       EndDate              DATE NOT NULL
);

CREATE INDEX XIF110TrainingHistory ON TrainingHistory
(
       AFSCId,
       PersonId
);

CREATE INDEX XIF113TrainingHistory ON TrainingHistory
(
       TrainingId
);


ALTER TABLE TrainingHistory
       ADD  ( PRIMARY KEY (PersonId, TrainingHistoryId, AFSCId, 
              TrainingId) ) ;


DROP TABLE Person CASCADE CONSTRAINTS;

CREATE TABLE Person (
       AFSCId               NUMBER NOT NULL,
       PersonId             NUMBER NOT NULL,
       SSN                  VARCHAR2(9) NOT NULL,
       DeploymentCodeId     NUMBER NULL,
       LocationId           NUMBER NULL,
       LastName             VARCHAR2(30) NOT NULL,
       FirstName            VARCHAR2(20) NOT NULL,
       MiddleInitial        VARCHAR2(1) NULL,
       Grade                VARCHAR2(10) NULL,
       Organization         VARCHAR2(20) NOT NULL
);

CREATE UNIQUE INDEX SSAN ON Person
(
       SSN
);

CREATE INDEX XIF107Person ON Person
(
       LocationId
);

CREATE INDEX XIF108Person ON Person
(
       DeploymentCodeId
);

CREATE INDEX XIF109Person ON Person
(
       AFSCId
);

CREATE INDEX OrganizationAFSCId ON Person
(
       Organization,
       AFSCId
);

CREATE INDEX LastNameFirstName ON Person
(
       LastName,
       FirstName
);

CREATE INDEX XIE3Person ON Person
(
       SSN
);

CREATE INDEX XIE4Person ON Person
(
       LastName,
       FirstName
);

CREATE INDEX XIE5Person ON Person
(
       SSN
);

CREATE INDEX XIE6Person ON Person
(
       LastName,
       FirstName
);

CREATE INDEX XIE7Person ON Person
(
       LastName,
       FirstName
);


ALTER TABLE Person
       ADD  ( PRIMARY KEY (AFSCId, PersonId) ) ;


DROP TABLE AFSCTrainingRequirements CASCADE CONSTRAINTS;

CREATE TABLE AFSCTrainingRequirements (
       AFSCTrainingRequirementsId NUMBER NOT NULL,
       AFSCId               NUMBER NOT NULL,
       TrainingId           NUMBER NOT NULL,
       Required             NUMBER NOT NULL,
       FrequencyNumeric     NUMBER NOT NULL,
       FrequencyUnits       VARCHAR2(5) NOT NULL
);

CREATE INDEX XIF82AFSCTrainingRequirements ON AFSCTrainingRequirements
(
       AFSCId
);

CREATE INDEX XIF83AFSCTrainingRequirements ON AFSCTrainingRequirements
(
       TrainingId
);


ALTER TABLE AFSCTrainingRequirements
       ADD  ( PRIMARY KEY (AFSCTrainingRequirementsId) ) ;


DROP TABLE Location CASCADE CONSTRAINTS;

CREATE TABLE Location (
       LocationId           NUMBER NOT NULL,
       Description          VARCHAR2(30) NULL,
       HazardousRating      VARCHAR2(4) NULL
);

CREATE INDEX Description ON Location
(
       Description
);


ALTER TABLE Location
       ADD  ( PRIMARY KEY (LocationId) ) ;


DROP TABLE DeploymentCode CASCADE CONSTRAINTS;

CREATE TABLE DeploymentCode (
       DeploymentCodeId     NUMBER NOT NULL,
       Description          VARCHAR2(30) NULL,
       DeploymentCode       VARCHAR2(4) NULL
);

CREATE INDEX DescriptionDeploymentCode ON DeploymentCode
(
       Description,
       DeploymentCode
);


ALTER TABLE DeploymentCode
       ADD  ( PRIMARY KEY (DeploymentCodeId) ) ;


DROP TABLE Training CASCADE CONSTRAINTS;

CREATE TABLE Training (
       TrainingId           NUMBER NOT NULL,
       Description          VARCHAR2(30) NULL,
       ExpirationNumeric    NUMBER NOT NULL,
       ExpirationUnits      VARCHAR2(5) NULL
);

CREATE INDEX Description ON Training
(
       Description
);


ALTER TABLE Training
       ADD  ( PRIMARY KEY (TrainingId) ) ;


DROP TABLE AFSC CASCADE CONSTRAINTS;

CREATE TABLE AFSC (
       AFSCId               NUMBER NOT NULL,
       JobDescription       VARCHAR2(60) NULL,
       AFSC                 VARCHAR2(7) NOT NULL
);

CREATE UNIQUE INDEX AFSC ON AFSC
(
       AFSC,
       JobDescription
);


ALTER TABLE AFSC
       ADD  ( PRIMARY KEY (AFSCId) ) ;


DROP TABLE Organization CASCADE CONSTRAINTS;

CREATE TABLE Organization (
       OrganizationId       NUMBER NOT NULL,
       OrgTypeId            NUMBER NOT NULL,
       OrgLocationId        NUMBER NOT NULL,
       OrgName              VARCHAR2(20) NOT NULL,
       ParentName           VARCHAR2(20) NULL
);

CREATE INDEX XIF75Organization ON Organization
(
       OrgTypeId
);

CREATE INDEX XIF76Organization ON Organization
(
       OrgLocationId
);


ALTER TABLE Organization
       ADD  ( PRIMARY KEY (OrganizationId, OrgTypeId, OrgLocationId) ) ;


DROP TABLE OrgLocationCode CASCADE CONSTRAINTS;

CREATE TABLE OrgLocationCode (
       OrgLocationId        NUMBER NOT NULL,
       OrgLocation          VARCHAR2(50) NULL
);


ALTER TABLE OrgLocationCode
       ADD  ( PRIMARY KEY (OrgLocationId) ) ;


DROP TABLE OrganizationType CASCADE CONSTRAINTS;

CREATE TABLE OrganizationType (
       OrgTypeId            NUMBER NOT NULL,
       OrgType              VARCHAR2(50) NULL
);


ALTER TABLE OrganizationType
       ADD  ( PRIMARY KEY (OrgTypeId) ) ;


DROP TABLE header CASCADE CONSTRAINTS;

CREATE TABLE header (
       Id                   FLOAT NOT NULL,
       NSN                  VARCHAR2(50) NULL,
       AAC                  VARCHAR2(50) NULL,
       SSC                  VARCHAR2(50) NULL,
       ICC                  VARCHAR2(50) NULL,
       Nomenclature         VARCHAR2(50) NULL
);


ALTER TABLE header
       ADD  ( PRIMARY KEY (Id) ) ;


DROP TABLE Airforce_Spares_DCR_By_Optempo CASCADE CONSTRAINTS;

CREATE TABLE Airforce_Spares_DCR_By_Optempo (
       Part_Id              FLOAT NOT NULL,
       MDS                  VARCHAR2(50) NULL,
       NSN                  VARCHAR2(50) NULL,
       Optempo              VARCHAR2(50) NULL,
       Demands_Per_Day      FLOAT NOT NULL
);

CREATE INDEX idxMdsNsn ON Airforce_Spares_DCR_By_Optempo
(
       MDS,
       NSN
);


ALTER TABLE Airforce_Spares_DCR_By_Optempo
       ADD  ( PRIMARY KEY (Part_Id) ) ;


DROP TABLE Airforce_Fuels_DCR_By_Optempo CASCADE CONSTRAINTS;

CREATE TABLE Airforce_Fuels_DCR_By_Optempo (
       Fuel_Id              FLOAT NOT NULL,
       MDS                  VARCHAR2(50) NULL,
       Fuel_NSN             VARCHAR2(50) NULL,
       Optempo              VARCHAR2(50) NULL,
       Gallons_Per_Day      FLOAT NOT NULL
);

CREATE INDEX MDS ON Airforce_Fuels_DCR_By_Optempo
(
       MDS
);


ALTER TABLE Airforce_Fuels_DCR_By_Optempo
       ADD  ( PRIMARY KEY (Fuel_Id) ) ;


DROP TABLE AircraftEngine CASCADE CONSTRAINTS;

CREATE TABLE AircraftEngine (
       SerialNumber         VARCHAR2(50) NOT NULL,
       NSN                  VARCHAR2(50) NOT NULL,
       MDS                  VARCHAR2(50) NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       EngineHours          FLOAT NOT NULL
);

CREATE INDEX XIF61AircraftEngine ON AircraftEngine
(
       MDS,
       TailNumber
);


ALTER TABLE AircraftEngine
       ADD  ( PRIMARY KEY (SerialNumber, NSN) ) ;


DROP TABLE AircraftAvailability CASCADE CONSTRAINTS;

CREATE TABLE AircraftAvailability (
       MDS                  VARCHAR2(50) NOT NULL,
       MonthEnding          DATE NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       FMCHours             INTEGER DEFAULT 0 NULL,
       TotalHours           INTEGER NULL
);


ALTER TABLE AircraftAvailability
       ADD  ( PRIMARY KEY (MDS, TailNumber, MonthEnding) ) ;


DROP TABLE Asset CASCADE CONSTRAINTS;

CREATE TABLE Asset (
       AssetID              INTEGER DEFAULT 0 NOT NULL,
       Organization         VARCHAR2(50) NULL,
       NSN                  VARCHAR2(50) NULL,
       SerialNumber         VARCHAR2(50) DEFAULT 0 NULL
);

CREATE INDEX AssetID ON Asset
(
       AssetID
);

CREATE INDEX OrgNsnSerialNum ON Asset
(
       Organization,
       NSN,
       SerialNumber
);


ALTER TABLE Asset
       ADD  ( PRIMARY KEY (AssetID) ) ;


DROP TABLE CompositeNSN CASCADE CONSTRAINTS;

CREATE TABLE CompositeNSN (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       NSN                  VARCHAR2(50) NOT NULL,
       Quantity             INTEGER DEFAULT 0 NULL
);

CREATE INDEX XIF59CompositeNSN ON CompositeNSN
(
       CompositeID
);


ALTER TABLE CompositeNSN
       ADD  ( PRIMARY KEY (CompositeID, NSN) ) ;


DROP TABLE UTCComposite CASCADE CONSTRAINTS;

CREATE TABLE UTCComposite (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       Quantity             NUMBER NOT NULL
);

CREATE INDEX XIF47UTCComposite ON UTCComposite
(
       CompositeID
);

CREATE INDEX XIF53UTCComposite ON UTCComposite
(
       UTCID
);


ALTER TABLE UTCComposite
       ADD  ( PRIMARY KEY (CompositeID, UTCID) ) ;


DROP TABLE Composite CASCADE CONSTRAINTS;

CREATE TABLE Composite (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR2(50) NULL
);

CREATE INDEX CompositeID ON Composite
(
       CompositeID
);


ALTER TABLE Composite
       ADD  ( PRIMARY KEY (CompositeID) ) ;


DROP TABLE FlightDetail CASCADE CONSTRAINTS;

CREATE TABLE FlightDetail (
       FlightID             INTEGER DEFAULT 0 NOT NULL,
       FlightReturnCodeID   INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX XIF57FlightDetail ON FlightDetail
(
       FlightID
);

CREATE INDEX XIF58FlightDetail ON FlightDetail
(
       FlightReturnCodeID
);


ALTER TABLE FlightDetail
       ADD  ( PRIMARY KEY (FlightID, FlightReturnCodeID) ) ;


DROP TABLE Flight CASCADE CONSTRAINTS;

CREATE TABLE Flight (
       FlightID             INTEGER DEFAULT 0 NOT NULL,
       MissionID            INTEGER DEFAULT 0 NOT NULL,
       TakeOffTime          DATE NOT NULL,
       MDS                  VARCHAR2(50) NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       LandingTime          DATE NOT NULL
);

CREATE INDEX XIF55Flight ON Flight
(
       MissionID
);

CREATE INDEX FlightID ON Flight
(
       FlightID
);

CREATE INDEX MdsTailNumLandTimeMissionId ON Flight
(
       MDS,
       TailNumber,
       LandingTime,
       MissionID
);


ALTER TABLE Flight
       ADD  ( PRIMARY KEY (FlightID) ) ;


DROP TABLE FlightReturnCode CASCADE CONSTRAINTS;

CREATE TABLE FlightReturnCode (
       FlightReturnCodeID   INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR2(50) NOT NULL
);

CREATE INDEX ReturnCodeID ON FlightReturnCode
(
       FlightReturnCodeID
);


ALTER TABLE FlightReturnCode
       ADD  ( PRIMARY KEY (FlightReturnCodeID) ) ;


DROP TABLE ScheduledMaintenance CASCADE CONSTRAINTS;

CREATE TABLE ScheduledMaintenance (
       ScheduledMaintenanceID INTEGER DEFAULT 0 NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       MDS                  VARCHAR2(50) NOT NULL,
       MaintenanceTypeID    INTEGER DEFAULT 0 NOT NULL,
       ScheduledDate        DATE NULL
);

CREATE INDEX MaintenanceID ON ScheduledMaintenance
(
       ScheduledMaintenanceID
);

CREATE INDEX MaintenanceTypeID ON ScheduledMaintenance
(
       MaintenanceTypeID,
       TailNumber,
       MDS,
       ScheduledDate
);


ALTER TABLE ScheduledMaintenance
       ADD  ( PRIMARY KEY (ScheduledMaintenanceID) ) ;


DROP TABLE MaintenanceType CASCADE CONSTRAINTS;

CREATE TABLE MaintenanceType (
       MaintenanceTypeID    INTEGER DEFAULT 0 NOT NULL,
       MDS                  VARCHAR2(50) NOT NULL,
       Nomenclature         VARCHAR2(50) NOT NULL,
       Frequency            INTEGER DEFAULT 0 NULL,
       FrequencyItemUnitOfMeasure VARCHAR2(20) DEFAULT 0 NULL,
       Duration             INTEGER DEFAULT 0 NULL
);

CREATE INDEX MaintenanceTypeID ON MaintenanceType
(
       MaintenanceTypeID,
       MDS,
       Nomenclature
);


ALTER TABLE MaintenanceType
       ADD  ( PRIMARY KEY (MaintenanceTypeID) ) ;


DROP TABLE Mission CASCADE CONSTRAINTS;

CREATE TABLE Mission (
       MissionID            INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR2(50) NOT NULL
);

CREATE INDEX MissionID ON Mission
(
       MissionID
);


ALTER TABLE Mission
       ADD  ( PRIMARY KEY (MissionID) ) ;


DROP TABLE PrototypeDetail CASCADE CONSTRAINTS;

CREATE TABLE PrototypeDetail (
       PrototypeID          INTEGER DEFAULT 0 NOT NULL,
       KeyName              VARCHAR2(50) NOT NULL,
       KeyValue             VARCHAR2(50) NOT NULL
);

CREATE INDEX XIF56PrototypeDetail ON PrototypeDetail
(
       PrototypeID
);

CREATE INDEX IdxPrototypeIdKeyname ON PrototypeDetail
(
       PrototypeID,
       KeyName
);


ALTER TABLE PrototypeDetail
       ADD  ( PRIMARY KEY (PrototypeID, KeyName) ) ;


DROP TABLE Prototype CASCADE CONSTRAINTS;

CREATE TABLE Prototype (
       PrototypeID          INTEGER DEFAULT 0 NOT NULL,
       TypeID               VARCHAR2(50) NOT NULL,
       Class                VARCHAR2(80) NOT NULL
);

CREATE INDEX IdxTypeid ON Prototype
(
       TypeID
);


ALTER TABLE Prototype
       ADD  ( PRIMARY KEY (PrototypeID) ) ;


DROP TABLE UTCNSN CASCADE CONSTRAINTS;

CREATE TABLE UTCNSN (
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       NSN                  VARCHAR2(50) NOT NULL,
       Quantity             INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX XIF60UTCNSN ON UTCNSN
(
       UTCID
);

CREATE INDEX IdxUtcidNsn ON UTCNSN
(
       UTCID,
       NSN
);


DROP TABLE UTC CASCADE CONSTRAINTS;

CREATE TABLE UTC (
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       Organization         VARCHAR2(50) NULL
);

CREATE INDEX UTCID ON UTC
(
       UTCID
);

CREATE INDEX IdxOrgUtcid ON UTC
(
       Organization,
       UTCID
);


ALTER TABLE UTC
       ADD  ( PRIMARY KEY (UTCID) ) ;


DROP TABLE Supplements CASCADE CONSTRAINTS;

CREATE TABLE Supplements (
       MDS                  VARCHAR2(50) NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       SupplementCnt        INTEGER NOT NULL
);


ALTER TABLE Supplements
       ADD  ( PRIMARY KEY (MDS, TailNumber) ) ;


DROP TABLE Aircraft CASCADE CONSTRAINTS;

CREATE TABLE Aircraft (
       MDS                  VARCHAR2(50) NOT NULL,
       TailNumber           VARCHAR2(50) NOT NULL,
       Organization         VARCHAR2(20) NULL,
       FlightHours          FLOAT NOT NULL
);

CREATE INDEX Organization ON Aircraft
(
       Organization
);


ALTER TABLE Aircraft
       ADD  ( PRIMARY KEY (MDS, TailNumber) ) ;


DROP TABLE DeletedAsset CASCADE CONSTRAINTS;

CREATE TABLE DeletedAsset (
       Organization         VARCHAR2(50) NOT NULL,
       NSN                  VARCHAR2(50) NULL,
       SerialNumber         VARCHAR2(50) NULL
);

CREATE INDEX organization ON DeletedAsset
(
       Organization
);


DROP TABLE UTCAFSC CASCADE CONSTRAINTS;

CREATE TABLE UTCAFSC (
       UTCAFSCId            NUMBER NOT NULL,
       JobDescription       VARCHAR2(60) NULL,
       AFSC                 VARCHAR2(7) NOT NULL
);


ALTER TABLE UTCAFSC
       ADD  ( PRIMARY KEY (UTCAFSCId) ) ;


DROP TABLE FinalizedUTCs CASCADE CONSTRAINTS;

CREATE TABLE FinalizedUTCs (
       UTCId                INTEGER NOT NULL,
       OplanId              VARCHAR2(15) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       AefName              VARCHAR2(20) NOT NULL,
       NSN                  VARCHAR2(50) NOT NULL,
       Quantity             INTEGER NOT NULL,
       StartDate            DATE NULL,
       EndDate              DATE NULL,
       BeddownLocation      VARCHAR2(40) NULL,
       Capacity             SMALLINT NULL,
       AssetSupportType     VARCHAR2(20) NULL
);


ALTER TABLE FinalizedUTCs
       ADD  ( PRIMARY KEY (UTCId, OplanId, AefName, ForcePackageMixId) ) ;


ALTER TABLE FinalizedPhases
       ADD  ( FOREIGN KEY (OrgId, ForcePackageMixId, AefName, OplanId)
                             REFERENCES FinalizedAefOrganizations
                             ON DELETE CASCADE ) ;


ALTER TABLE FinalizedAefOrganizations
       ADD  ( FOREIGN KEY (AefName, OplanId)
                             REFERENCES FinalizedOplanAefs
                             ON DELETE CASCADE ) ;


ALTER TABLE OplanPhases
       ADD  ( FOREIGN KEY (OrgId, AefName, OplanId)
                             REFERENCES AefOrganizations
                             ON DELETE CASCADE ) ;


ALTER TABLE AefOrganizations
       ADD  ( FOREIGN KEY (AefName, OplanId)
                             REFERENCES OplanAefs
                             ON DELETE CASCADE ) ;


ALTER TABLE FinalizedOplanAefs
       ADD  ( FOREIGN KEY (OplanId)
                             REFERENCES Oplan ) ;


ALTER TABLE OplanAefs
       ADD  ( FOREIGN KEY (OplanId)
                             REFERENCES Oplan
                             ON DELETE CASCADE ) ;


ALTER TABLE AvailableLift
       ADD  ( FOREIGN KEY (OplanId)
                             REFERENCES Oplan
                             ON DELETE CASCADE ) ;


ALTER TABLE AvailableAircraft
       ADD  ( FOREIGN KEY (OplanId)
                             REFERENCES Oplan
                             ON DELETE CASCADE ) ;


ALTER TABLE UTCPersonnelRequirements
       ADD  ( FOREIGN KEY (UTCAFSCId)
                             REFERENCES UTCAFSC ) ;


ALTER TABLE DeploymentHistory
       ADD  ( FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person ) ;


ALTER TABLE DeploymentHistory
       ADD  ( FOREIGN KEY (LocationId)
                             REFERENCES Location ) ;


ALTER TABLE Immunizations
       ADD  ( FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person ) ;


ALTER TABLE ScheduledTraining
       ADD  ( FOREIGN KEY (TrainingId)
                             REFERENCES Training ) ;


ALTER TABLE ScheduledTraining
       ADD  ( FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person
                             ON DELETE CASCADE ) ;


ALTER TABLE TrainingHistory
       ADD  ( FOREIGN KEY (TrainingId)
                             REFERENCES Training ) ;


ALTER TABLE TrainingHistory
       ADD  ( FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person
                             ON DELETE CASCADE ) ;


ALTER TABLE Person
       ADD  ( FOREIGN KEY (AFSCId)
                             REFERENCES AFSC ) ;


ALTER TABLE Person
       ADD  ( FOREIGN KEY (DeploymentCodeId)
                             REFERENCES DeploymentCode ) ;


ALTER TABLE Person
       ADD  ( FOREIGN KEY (LocationId)
                             REFERENCES Location ) ;


ALTER TABLE AFSCTrainingRequirements
       ADD  ( FOREIGN KEY (TrainingId)
                             REFERENCES Training ) ;


ALTER TABLE AFSCTrainingRequirements
       ADD  ( FOREIGN KEY (AFSCId)
                             REFERENCES AFSC ) ;


ALTER TABLE Organization
       ADD  ( FOREIGN KEY (OrgLocationId)
                             REFERENCES OrgLocationCode ) ;


ALTER TABLE Organization
       ADD  ( FOREIGN KEY (OrgTypeId)
                             REFERENCES OrganizationType ) ;


ALTER TABLE AircraftEngine
       ADD  ( FOREIGN KEY (MDS, TailNumber)
                             REFERENCES Aircraft
                             ON DELETE CASCADE ) ;


ALTER TABLE CompositeNSN
       ADD  ( FOREIGN KEY (CompositeID)
                             REFERENCES Composite
                             ON DELETE CASCADE ) ;


ALTER TABLE UTCComposite
       ADD  ( FOREIGN KEY (UTCID)
                             REFERENCES UTC
                             ON DELETE CASCADE ) ;


ALTER TABLE UTCComposite
       ADD  ( FOREIGN KEY (CompositeID)
                             REFERENCES Composite
                             ON DELETE CASCADE ) ;


ALTER TABLE FlightDetail
       ADD  ( FOREIGN KEY (FlightReturnCodeID)
                             REFERENCES FlightReturnCode
                             ON DELETE CASCADE ) ;


ALTER TABLE FlightDetail
       ADD  ( FOREIGN KEY (FlightID)
                             REFERENCES Flight
                             ON DELETE CASCADE ) ;


ALTER TABLE Flight
       ADD  ( FOREIGN KEY (MissionID)
                             REFERENCES Mission
                             ON DELETE CASCADE ) ;


ALTER TABLE ScheduledMaintenance
       ADD  ( FOREIGN KEY (MaintenanceTypeID)
                             REFERENCES MaintenanceType ) ;


ALTER TABLE PrototypeDetail
       ADD  ( FOREIGN KEY (PrototypeID)
                             REFERENCES Prototype
                             ON DELETE CASCADE ) ;


ALTER TABLE UTCNSN
       ADD  ( FOREIGN KEY (UTCID)
                             REFERENCES UTC
                             ON DELETE CASCADE ) ;


ALTER TABLE Supplements
       ADD  ( FOREIGN KEY (MDS, TailNumber)
                             REFERENCES Aircraft
                             ON DELETE CASCADE ) ;


CREATE OR REPLACE PROCEDURE disableAlpTableConstraints IS
cid INTEGER;
   CURSOR c1 IS
     SELECT OWNER, TABLE_NAME, CONSTRAINT_NAME
     FROM ALL_CONSTRAINTS
     WHERE owner in ('AEF')
     ORDER BY constraint_type desc;
     owner            all_constraints.OWNER%TYPE;
	 table_name       all_constraints.TABLE_NAME%TYPE;
	 constraint_name  all_constraints.CONSTRAINT_NAME%TYPE;
BEGIN
   /* cursor c1 contains the info needed from the all_constraints sytem
      table, to obtain all the constraints for the ALP schemas.  Get this data,
	  and disable all of these contraints, in order to allow the ALP database code
	  to perform truncate tables to load new data. */
  OPEN c1;
  cid := SYS.DBMS_SQL.OPEN_CURSOR;
  LOOP
    FETCH c1 INTO owner, table_name, constraint_name;
	EXIT WHEN c1%NOTFOUND;
	--
    -- Parse and immediately execute dynamic SQL statement built by
    --   concatenating table name to DROP TABLE command.
    --
    SYS.DBMS_SQL.PARSE(cid, 'ALTER TABLE ' || owner || '.' || table_name
	               || ' DISABLE CONSTRAINT ' || constraint_name,
         	       sys.dbms_sql.native);
	COMMIT WORK;
  END LOOP;
  SYS.DBMS_SQL.CLOSE_CURSOR(cid);
  CLOSE c1;
  EXCEPTION
    -- If an exception is raised, close cursor before exiting.
    WHEN OTHERS THEN
      DBMS_SQL.CLOSE_CURSOR(cid);
	  CLOSE c1;
      RAISE;  -- reraise the exception
END;
/

CREATE OR REPLACE PROCEDURE enableAlpTableConstraints IS
cid INTEGER;
   CURSOR c2 IS
     SELECT OWNER, TABLE_NAME, CONSTRAINT_NAME
     FROM ALL_CONSTRAINTS
     WHERE owner in ('AEF')
     ORDER BY constraint_type;
     owner            all_constraints.OWNER%TYPE;
	 table_name       all_constraints.TABLE_NAME%TYPE;
	 constraint_name  all_constraints.CONSTRAINT_NAME%TYPE;
BEGIN
   /* cursor c1 contains the info needed from the all_constraints sytem
      table, to obtain all the constraints for the ALP schemas.  Get this data,
	  and disable all of these contraints, in order to allow the ALP database code
	  to perform truncate tables to load new data. */
  OPEN c2;
  cid := SYS.DBMS_SQL.OPEN_CURSOR;
  LOOP
    FETCH c2 INTO owner, table_name, constraint_name;
	EXIT WHEN c2%NOTFOUND;
	--
    -- Parse and immediately execute dynamic SQL statement built by
    --   concatenating table name to DROP TABLE command.
    --
    SYS.DBMS_SQL.PARSE(cid, 'ALTER TABLE ' || owner || '.' || table_name
	               || ' ENABLE CONSTRAINT ' || constraint_name,
         	       sys.dbms_sql.native);
  END LOOP;
  SYS.DBMS_SQL.CLOSE_CURSOR(cid);
  CLOSE c2;
  EXCEPTION
    -- If an exception is raised, close cursor before exiting.
    WHEN OTHERS THEN
      DBMS_SQL.CLOSE_CURSOR(cid);
	  CLOSE c2;
      RAISE;  -- reraise the exception
END;
/



create trigger tI_FinalizedPhases after INSERT on FinalizedPhases for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on FinalizedPhases 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* FinalizedAefOrganizations R/135 FinalizedPhases ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from FinalizedAefOrganizations
      where
        /* %JoinFKPK(:%New,FinalizedAefOrganizations," = "," and") */
        :new.OrgId = FinalizedAefOrganizations.OrgId and
        :new.ForcePackageMixId = FinalizedAefOrganizations.ForcePackageMixId and
        :new.AefName = FinalizedAefOrganizations.AefName and
        :new.OplanId = FinalizedAefOrganizations.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "FinalizedPhases" because "FinalizedAefOrganizations" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_FinalizedPhases after UPDATE on FinalizedPhases for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on FinalizedPhases 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* FinalizedAefOrganizations R/135 FinalizedPhases ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from FinalizedAefOrganizations
    where
      /* %JoinFKPK(:%New,FinalizedAefOrganizations," = "," and") */
      :new.OrgId = FinalizedAefOrganizations.OrgId and
      :new.ForcePackageMixId = FinalizedAefOrganizations.ForcePackageMixId and
      :new.AefName = FinalizedAefOrganizations.AefName and
      :new.OplanId = FinalizedAefOrganizations.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "FinalizedPhases" because "FinalizedAefOrganizations" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_FinalizedAefOrganizations after DELETE on FinalizedAefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on FinalizedAefOrganizations 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* FinalizedAefOrganizations R/135 FinalizedPhases ON PARENT DELETE CASCADE */
    delete from FinalizedPhases
      where
        /*  %JoinFKPK(FinalizedPhases,:%Old," = "," and") */
        FinalizedPhases.OrgId = :old.OrgId and
        FinalizedPhases.ForcePackageMixId = :old.ForcePackageMixId and
        FinalizedPhases.AefName = :old.AefName and
        FinalizedPhases.OplanId = :old.OplanId;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_FinalizedAefOrganizations after INSERT on FinalizedAefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on FinalizedAefOrganizations 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* FinalizedOplanAefs R/143 FinalizedAefOrganizations ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from FinalizedOplanAefs
      where
        /* %JoinFKPK(:%New,FinalizedOplanAefs," = "," and") */
        :new.AefName = FinalizedOplanAefs.AefName and
        :new.OplanId = FinalizedOplanAefs.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "FinalizedAefOrganizations" because "FinalizedOplanAefs" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_FinalizedAefOrganizations after UPDATE on FinalizedAefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on FinalizedAefOrganizations 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* FinalizedOplanAefs R/143 FinalizedAefOrganizations ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from FinalizedOplanAefs
    where
      /* %JoinFKPK(:%New,FinalizedOplanAefs," = "," and") */
      :new.AefName = FinalizedOplanAefs.AefName and
      :new.OplanId = FinalizedOplanAefs.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "FinalizedAefOrganizations" because "FinalizedOplanAefs" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_OplanPhases after INSERT on OplanPhases for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on OplanPhases 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* AefOrganizations R/134 OplanPhases ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from AefOrganizations
      where
        /* %JoinFKPK(:%New,AefOrganizations," = "," and") */
        :new.OrgId = AefOrganizations.OrgId and
        :new.AefName = AefOrganizations.AefName and
        :new.OplanId = AefOrganizations.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "OplanPhases" because "AefOrganizations" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_OplanPhases after UPDATE on OplanPhases for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on OplanPhases 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* AefOrganizations R/134 OplanPhases ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from AefOrganizations
    where
      /* %JoinFKPK(:%New,AefOrganizations," = "," and") */
      :new.OrgId = AefOrganizations.OrgId and
      :new.AefName = AefOrganizations.AefName and
      :new.OplanId = AefOrganizations.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "OplanPhases" because "AefOrganizations" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_AefOrganizations after DELETE on AefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on AefOrganizations 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* AefOrganizations R/134 OplanPhases ON PARENT DELETE CASCADE */
    delete from OplanPhases
      where
        /*  %JoinFKPK(OplanPhases,:%Old," = "," and") */
        OplanPhases.OrgId = :old.OrgId and
        OplanPhases.AefName = :old.AefName and
        OplanPhases.OplanId = :old.OplanId;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_AefOrganizations after INSERT on AefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on AefOrganizations 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* OplanAefs R/140 AefOrganizations ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from OplanAefs
      where
        /* %JoinFKPK(:%New,OplanAefs," = "," and") */
        :new.AefName = OplanAefs.AefName and
        :new.OplanId = OplanAefs.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "AefOrganizations" because "OplanAefs" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_AefOrganizations after UPDATE on AefOrganizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on AefOrganizations 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* OplanAefs R/140 AefOrganizations ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from OplanAefs
    where
      /* %JoinFKPK(:%New,OplanAefs," = "," and") */
      :new.AefName = OplanAefs.AefName and
      :new.OplanId = OplanAefs.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "AefOrganizations" because "OplanAefs" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_FinalizedOplanAefs after DELETE on FinalizedOplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on FinalizedOplanAefs 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* FinalizedOplanAefs R/143 FinalizedAefOrganizations ON PARENT DELETE CASCADE */
    delete from FinalizedAefOrganizations
      where
        /*  %JoinFKPK(FinalizedAefOrganizations,:%Old," = "," and") */
        FinalizedAefOrganizations.AefName = :old.AefName and
        FinalizedAefOrganizations.OplanId = :old.OplanId;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_FinalizedOplanAefs after INSERT on FinalizedOplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on FinalizedOplanAefs 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/141 FinalizedOplanAefs ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Oplan
      where
        /* %JoinFKPK(:%New,Oplan," = "," and") */
        :new.OplanId = Oplan.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "FinalizedOplanAefs" because "Oplan" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_FinalizedOplanAefs after UPDATE on FinalizedOplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on FinalizedOplanAefs 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Oplan R/141 FinalizedOplanAefs ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Oplan
    where
      /* %JoinFKPK(:%New,Oplan," = "," and") */
      :new.OplanId = Oplan.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "FinalizedOplanAefs" because "Oplan" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_OplanAefs after DELETE on OplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on OplanAefs 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* OplanAefs R/140 AefOrganizations ON PARENT DELETE CASCADE */
    delete from AefOrganizations
      where
        /*  %JoinFKPK(AefOrganizations,:%Old," = "," and") */
        AefOrganizations.AefName = :old.AefName and
        AefOrganizations.OplanId = :old.OplanId;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_OplanAefs after INSERT on OplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on OplanAefs 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/139 OplanAefs ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Oplan
      where
        /* %JoinFKPK(:%New,Oplan," = "," and") */
        :new.OplanId = Oplan.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "OplanAefs" because "Oplan" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_OplanAefs after UPDATE on OplanAefs for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on OplanAefs 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Oplan R/139 OplanAefs ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Oplan
    where
      /* %JoinFKPK(:%New,Oplan," = "," and") */
      :new.OplanId = Oplan.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "OplanAefs" because "Oplan" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_AvailableLift after INSERT on AvailableLift for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on AvailableLift 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/119 AvailableLift ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Oplan
      where
        /* %JoinFKPK(:%New,Oplan," = "," and") */
        :new.OplanId = Oplan.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "AvailableLift" because "Oplan" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_AvailableLift after UPDATE on AvailableLift for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on AvailableLift 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Oplan R/119 AvailableLift ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Oplan
    where
      /* %JoinFKPK(:%New,Oplan," = "," and") */
      :new.OplanId = Oplan.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "AvailableLift" because "Oplan" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_AvailableAircraft after INSERT on AvailableAircraft for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on AvailableAircraft 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/118 AvailableAircraft ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Oplan
      where
        /* %JoinFKPK(:%New,Oplan," = "," and") */
        :new.OplanId = Oplan.OplanId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "AvailableAircraft" because "Oplan" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_AvailableAircraft after UPDATE on AvailableAircraft for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on AvailableAircraft 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Oplan R/118 AvailableAircraft ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Oplan
    where
      /* %JoinFKPK(:%New,Oplan," = "," and") */
      :new.OplanId = Oplan.OplanId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "AvailableAircraft" because "Oplan" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_Oplan after DELETE on Oplan for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on Oplan 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/141 FinalizedOplanAefs ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from FinalizedOplanAefs
      where
        /*  %JoinFKPK(FinalizedOplanAefs,:%Old," = "," and") */
        FinalizedOplanAefs.OplanId = :old.OplanId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Oplan" because "FinalizedOplanAefs" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/139 OplanAefs ON PARENT DELETE CASCADE */
    delete from OplanAefs
      where
        /*  %JoinFKPK(OplanAefs,:%Old," = "," and") */
        OplanAefs.OplanId = :old.OplanId;

    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/119 AvailableLift ON PARENT DELETE CASCADE */
    delete from AvailableLift
      where
        /*  %JoinFKPK(AvailableLift,:%Old," = "," and") */
        AvailableLift.OplanId = :old.OplanId;

    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Oplan R/118 AvailableAircraft ON PARENT DELETE CASCADE */
    delete from AvailableAircraft
      where
        /*  %JoinFKPK(AvailableAircraft,:%Old," = "," and") */
        AvailableAircraft.OplanId = :old.OplanId;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_Oplan after UPDATE on Oplan for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on Oplan 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Oplan R/141 FinalizedOplanAefs ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.OplanId <> :new.OplanId
  then
    select count(*) into numrows
      from FinalizedOplanAefs
      where
        /*  %JoinFKPK(FinalizedOplanAefs,:%Old," = "," and") */
        FinalizedOplanAefs.OplanId = :old.OplanId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Oplan" because "FinalizedOplanAefs" exists.'
      );
    end if;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_UTCPersonnelRequirements after INSERT on UTCPersonnelRequirements for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on UTCPersonnelRequirements 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* UTCAFSC R/114 UTCPersonnelRequirements ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from UTCAFSC
      where
        /* %JoinFKPK(:%New,UTCAFSC," = "," and") */
        :new.UTCAFSCId = UTCAFSC.UTCAFSCId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "UTCPersonnelRequirements" because "UTCAFSC" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_UTCPersonnelRequirements after UPDATE on UTCPersonnelRequirements for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on UTCPersonnelRequirements 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* UTCAFSC R/114 UTCPersonnelRequirements ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from UTCAFSC
    where
      /* %JoinFKPK(:%New,UTCAFSC," = "," and") */
      :new.UTCAFSCId = UTCAFSC.UTCAFSCId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "UTCPersonnelRequirements" because "UTCAFSC" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_DeploymentHistory after INSERT on DeploymentHistory for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on DeploymentHistory 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Person R/116 DeploymentHistory ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Person
      where
        /* %JoinFKPK(:%New,Person," = "," and") */
        :new.AFSCId = Person.AFSCId and
        :new.PersonId = Person.PersonId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "DeploymentHistory" because "Person" does not exist.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Location R/85 DeploymentHistory ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Location
      where
        /* %JoinFKPK(:%New,Location," = "," and") */
        :new.LocationId = Location.LocationId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "DeploymentHistory" because "Location" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_DeploymentHistory after UPDATE on DeploymentHistory for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on DeploymentHistory 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Person R/116 DeploymentHistory ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Person
    where
      /* %JoinFKPK(:%New,Person," = "," and") */
      :new.AFSCId = Person.AFSCId and
      :new.PersonId = Person.PersonId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "DeploymentHistory" because "Person" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Location R/85 DeploymentHistory ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Location
    where
      /* %JoinFKPK(:%New,Location," = "," and") */
      :new.LocationId = Location.LocationId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "DeploymentHistory" because "Location" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_Immunizations after INSERT on Immunizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on Immunizations 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Person R/115 Immunizations ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Person
      where
        /* %JoinFKPK(:%New,Person," = "," and") */
        :new.AFSCId = Person.AFSCId and
        :new.PersonId = Person.PersonId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Immunizations" because "Person" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_Immunizations after UPDATE on Immunizations for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- UPDATE trigger on Immunizations 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Person R/115 Immunizations ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Person
    where
      /* %JoinFKPK(:%New,Person," = "," and") */
      :new.AFSCId = Person.AFSCId and
      :new.PersonId = Person.PersonId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Immunizations" because "Person" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tD_ScheduledTraining after DELETE on ScheduledTraining for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- DELETE trigger on ScheduledTraining 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Person R/111 ScheduledTraining ON CHILD DELETE RESTRICT */
    select count(*) into numrows from Person
      where
        /* %JoinFKPK(:%Old,Person," = "," and") */
        :old.AFSCId = Person.AFSCId and
        :old.PersonId = Person.PersonId;
    if (numrows > 0)
    then
      raise_application_error(
        -20010,
        'Cannot DELETE "ScheduledTraining" because "Person" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tI_ScheduledTraining after INSERT on ScheduledTraining for each row
-- ERwin Builtin Tue Oct 03 20:17:53 2000
-- INSERT trigger on ScheduledTraining 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
    /* Training R/112 ScheduledTraining ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Training
      where
        /* %JoinFKPK(:%New,Training," = "," and") */
        :new.TrainingId = Training.TrainingId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "ScheduledTraining" because "Training" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:53 2000
end;
/

create trigger tU_ScheduledTraining after UPDATE on ScheduledTraining for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on ScheduledTraining 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:53 2000 */
  /* Training R/112 ScheduledTraining ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Training
    where
      /* %JoinFKPK(:%New,Training," = "," and") */
      :new.TrainingId = Training.TrainingId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "ScheduledTraining" because "Training" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/111 ScheduledTraining ON CHILD UPDATE CASCADE */
    insert into Person (AFSCId,
                        PersonId)
      select AFSCId,
             PersonId
        from ScheduledTraining
        where
          /* %NotnullFK(:%New," is not null and") */
          :new.AFSCId is not null and
          :new.PersonId is not null and
          not exists (
            select * from Person
              where
                /* %JoinFKPK(:%New,Person," = "," and") */
                :new.AFSCId = Person.AFSCId and
                :new.PersonId = Person.PersonId
          );


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_TrainingHistory after DELETE on TrainingHistory for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on TrainingHistory 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Person R/110 TrainingHistory ON CHILD DELETE RESTRICT */
    select count(*) into numrows from Person
      where
        /* %JoinFKPK(:%Old,Person," = "," and") */
        :old.AFSCId = Person.AFSCId and
        :old.PersonId = Person.PersonId;
    if (numrows > 0)
    then
      raise_application_error(
        -20010,
        'Cannot DELETE "TrainingHistory" because "Person" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_TrainingHistory after INSERT on TrainingHistory for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on TrainingHistory 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/113 TrainingHistory ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Training
      where
        /* %JoinFKPK(:%New,Training," = "," and") */
        :new.TrainingId = Training.TrainingId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "TrainingHistory" because "Training" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_TrainingHistory after UPDATE on TrainingHistory for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on TrainingHistory 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Training R/113 TrainingHistory ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Training
    where
      /* %JoinFKPK(:%New,Training," = "," and") */
      :new.TrainingId = Training.TrainingId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "TrainingHistory" because "Training" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/110 TrainingHistory ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Person
    where
      /* %JoinFKPK(:%New,Person," = "," and") */
      :new.AFSCId = Person.AFSCId and
      :new.PersonId = Person.PersonId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "TrainingHistory" because "Person" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Person after DELETE on Person for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Person 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Person R/116 DeploymentHistory ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from DeploymentHistory
      where
        /*  %JoinFKPK(DeploymentHistory,:%Old," = "," and") */
        DeploymentHistory.AFSCId = :old.AFSCId and
        DeploymentHistory.PersonId = :old.PersonId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Person" because "DeploymentHistory" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Person R/115 Immunizations ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from Immunizations
      where
        /*  %JoinFKPK(Immunizations,:%Old," = "," and") */
        Immunizations.AFSCId = :old.AFSCId and
        Immunizations.PersonId = :old.PersonId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Person" because "Immunizations" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Person R/111 ScheduledTraining ON PARENT DELETE CASCADE */
    delete from ScheduledTraining
      where
        /*  %JoinFKPK(ScheduledTraining,:%Old," = "," and") */
        ScheduledTraining.AFSCId = :old.AFSCId and
        ScheduledTraining.PersonId = :old.PersonId;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Person R/110 TrainingHistory ON PARENT DELETE CASCADE */
    delete from TrainingHistory
      where
        /*  %JoinFKPK(TrainingHistory,:%Old," = "," and") */
        TrainingHistory.AFSCId = :old.AFSCId and
        TrainingHistory.PersonId = :old.PersonId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_Person after INSERT on Person for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on Person 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* AFSC R/109 Person ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from AFSC
      where
        /* %JoinFKPK(:%New,AFSC," = "," and") */
        :new.AFSCId = AFSC.AFSCId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Person" because "AFSC" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Person after UPDATE on Person for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Person 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/116 DeploymentHistory ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.AFSCId <> :new.AFSCId or 
    :old.PersonId <> :new.PersonId
  then
    select count(*) into numrows
      from DeploymentHistory
      where
        /*  %JoinFKPK(DeploymentHistory,:%Old," = "," and") */
        DeploymentHistory.AFSCId = :old.AFSCId and
        DeploymentHistory.PersonId = :old.PersonId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Person" because "DeploymentHistory" exists.'
      );
    end if;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/115 Immunizations ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.AFSCId <> :new.AFSCId or 
    :old.PersonId <> :new.PersonId
  then
    select count(*) into numrows
      from Immunizations
      where
        /*  %JoinFKPK(Immunizations,:%Old," = "," and") */
        Immunizations.AFSCId = :old.AFSCId and
        Immunizations.PersonId = :old.PersonId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Person" because "Immunizations" exists.'
      );
    end if;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/111 ScheduledTraining ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.AFSCId <> :new.AFSCId or 
    :old.PersonId <> :new.PersonId
  then
    update ScheduledTraining
      set
        /*  %JoinFKPK(ScheduledTraining,:%New," = ",",") */
        ScheduledTraining.AFSCId = :new.AFSCId,
        ScheduledTraining.PersonId = :new.PersonId
      where
        /*  %JoinFKPK(ScheduledTraining,:%Old," = "," and") */
        ScheduledTraining.AFSCId = :old.AFSCId and
        ScheduledTraining.PersonId = :old.PersonId;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Person R/110 TrainingHistory ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.AFSCId <> :new.AFSCId or 
    :old.PersonId <> :new.PersonId
  then
    update TrainingHistory
      set
        /*  %JoinFKPK(TrainingHistory,:%New," = ",",") */
        TrainingHistory.AFSCId = :new.AFSCId,
        TrainingHistory.PersonId = :new.PersonId
      where
        /*  %JoinFKPK(TrainingHistory,:%Old," = "," and") */
        TrainingHistory.AFSCId = :old.AFSCId and
        TrainingHistory.PersonId = :old.PersonId;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* AFSC R/109 Person ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from AFSC
    where
      /* %JoinFKPK(:%New,AFSC," = "," and") */
      :new.AFSCId = AFSC.AFSCId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Person" because "AFSC" does not exist.'
    );
  end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* DeploymentCode R/108 Person ON CHILD UPDATE SET NULL */
    update Person
      set
        /* %SetFK(Person,NULL) */
        Person.DeploymentCodeId = NULL
      where
        not exists (
          select * from DeploymentCode
            where
              /* %JoinFKPK(:%New,DeploymentCode," = "," and") */
              :new.DeploymentCodeId = DeploymentCode.DeploymentCodeId
        ) and
        /* %JoinPKPK(Person,:%New," = "," and") */
        Person.AFSCId = :new.AFSCId and
        Person.PersonId = :new.PersonId;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Location R/107 Person ON CHILD UPDATE SET NULL */
    update Person
      set
        /* %SetFK(Person,NULL) */
        Person.LocationId = NULL
      where
        not exists (
          select * from Location
            where
              /* %JoinFKPK(:%New,Location," = "," and") */
              :new.LocationId = Location.LocationId
        ) and
        /* %JoinPKPK(Person,:%New," = "," and") */
        Person.AFSCId = :new.AFSCId and
        Person.PersonId = :new.PersonId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_AFSCTrainingRequirements after INSERT on AFSCTrainingRequirements for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on AFSCTrainingRequirements 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/83 AFSCTrainingRequirements ON CHILD INSERT SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.TrainingId = NULL
      where
        not exists (
          select * from Training
            where
              /* %JoinFKPK(:%New,Training," = "," and") */
              :new.TrainingId = Training.TrainingId
        ) and
        /* %JoinPKPK(AFSCTrainingRequirements,:%New," = "," and") */
        AFSCTrainingRequirements.AFSCTrainingRequirementsId = :new.AFSCTrainingRequirementsId;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* AFSC R/82 AFSCTrainingRequirements ON CHILD INSERT SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.AFSCId = NULL
      where
        not exists (
          select * from AFSC
            where
              /* %JoinFKPK(:%New,AFSC," = "," and") */
              :new.AFSCId = AFSC.AFSCId
        ) and
        /* %JoinPKPK(AFSCTrainingRequirements,:%New," = "," and") */
        AFSCTrainingRequirements.AFSCTrainingRequirementsId = :new.AFSCTrainingRequirementsId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_AFSCTrainingRequirements after UPDATE on AFSCTrainingRequirements for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on AFSCTrainingRequirements 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/83 AFSCTrainingRequirements ON CHILD UPDATE SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.TrainingId = NULL
      where
        not exists (
          select * from Training
            where
              /* %JoinFKPK(:%New,Training," = "," and") */
              :new.TrainingId = Training.TrainingId
        ) and
        /* %JoinPKPK(AFSCTrainingRequirements,:%New," = "," and") */
        AFSCTrainingRequirements.AFSCTrainingRequirementsId = :new.AFSCTrainingRequirementsId;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* AFSC R/82 AFSCTrainingRequirements ON CHILD UPDATE SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.AFSCId = NULL
      where
        not exists (
          select * from AFSC
            where
              /* %JoinFKPK(:%New,AFSC," = "," and") */
              :new.AFSCId = AFSC.AFSCId
        ) and
        /* %JoinPKPK(AFSCTrainingRequirements,:%New," = "," and") */
        AFSCTrainingRequirements.AFSCTrainingRequirementsId = :new.AFSCTrainingRequirementsId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Location after DELETE on Location for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Location 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Location R/107 Person ON PARENT DELETE SET NULL */
    update Person
      set
        /* %SetFK(Person,NULL) */
        Person.LocationId = NULL
      where
        /* %JoinFKPK(Person,:%Old," = "," and") */
        Person.LocationId = :old.LocationId;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Location R/85 DeploymentHistory ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from DeploymentHistory
      where
        /*  %JoinFKPK(DeploymentHistory,:%Old," = "," and") */
        DeploymentHistory.LocationId = :old.LocationId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Location" because "DeploymentHistory" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Location after UPDATE on Location for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Location 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Location R/107 Person ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.LocationId <> :new.LocationId
  then
    update Person
      set
        /*  %JoinFKPK(Person,:%New," = ",",") */
        Person.LocationId = :new.LocationId
      where
        /*  %JoinFKPK(Person,:%Old," = "," and") */
        Person.LocationId = :old.LocationId;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Location R/85 DeploymentHistory ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.LocationId <> :new.LocationId
  then
    select count(*) into numrows
      from DeploymentHistory
      where
        /*  %JoinFKPK(DeploymentHistory,:%Old," = "," and") */
        DeploymentHistory.LocationId = :old.LocationId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Location" because "DeploymentHistory" exists.'
      );
    end if;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_DeploymentCode after DELETE on DeploymentCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on DeploymentCode 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* DeploymentCode R/108 Person ON PARENT DELETE SET NULL */
    update Person
      set
        /* %SetFK(Person,NULL) */
        Person.DeploymentCodeId = NULL
      where
        /* %JoinFKPK(Person,:%Old," = "," and") */
        Person.DeploymentCodeId = :old.DeploymentCodeId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_DeploymentCode after UPDATE on DeploymentCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on DeploymentCode 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* DeploymentCode R/108 Person ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.DeploymentCodeId <> :new.DeploymentCodeId
  then
    update Person
      set
        /*  %JoinFKPK(Person,:%New," = ",",") */
        Person.DeploymentCodeId = :new.DeploymentCodeId
      where
        /*  %JoinFKPK(Person,:%Old," = "," and") */
        Person.DeploymentCodeId = :old.DeploymentCodeId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Training after DELETE on Training for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Training 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/113 TrainingHistory ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from TrainingHistory
      where
        /*  %JoinFKPK(TrainingHistory,:%Old," = "," and") */
        TrainingHistory.TrainingId = :old.TrainingId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Training" because "TrainingHistory" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/112 ScheduledTraining ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from ScheduledTraining
      where
        /*  %JoinFKPK(ScheduledTraining,:%Old," = "," and") */
        ScheduledTraining.TrainingId = :old.TrainingId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "Training" because "ScheduledTraining" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Training R/83 AFSCTrainingRequirements ON PARENT DELETE SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.TrainingId = NULL
      where
        /* %JoinFKPK(AFSCTrainingRequirements,:%Old," = "," and") */
        AFSCTrainingRequirements.TrainingId = :old.TrainingId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Training after UPDATE on Training for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Training 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Training R/113 TrainingHistory ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.TrainingId <> :new.TrainingId
  then
    select count(*) into numrows
      from TrainingHistory
      where
        /*  %JoinFKPK(TrainingHistory,:%Old," = "," and") */
        TrainingHistory.TrainingId = :old.TrainingId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Training" because "TrainingHistory" exists.'
      );
    end if;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Training R/112 ScheduledTraining ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.TrainingId <> :new.TrainingId
  then
    select count(*) into numrows
      from ScheduledTraining
      where
        /*  %JoinFKPK(ScheduledTraining,:%Old," = "," and") */
        ScheduledTraining.TrainingId = :old.TrainingId;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "Training" because "ScheduledTraining" exists.'
      );
    end if;
  end if;

  /* Training R/83 AFSCTrainingRequirements ON PARENT UPDATE SET NULL */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or " */
    :old.TrainingId <> :new.TrainingId
  then
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.TrainingId = NULL
      where
        /* %JoinFKPK(AFSCTrainingRequirements,:%Old," = ",",") */
        AFSCTrainingRequirements.TrainingId = :old.TrainingId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_AFSC after DELETE on AFSC for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on AFSC 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* AFSC R/109 Person ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from Person
      where
        /*  %JoinFKPK(Person,:%Old," = "," and") */
        Person.AFSCId = :old.AFSCId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "AFSC" because "Person" exists.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* AFSC R/82 AFSCTrainingRequirements ON PARENT DELETE SET NULL */
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.AFSCId = NULL
      where
        /* %JoinFKPK(AFSCTrainingRequirements,:%Old," = "," and") */
        AFSCTrainingRequirements.AFSCId = :old.AFSCId;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_AFSC after UPDATE on AFSC for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on AFSC 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* AFSC R/109 Person ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.AFSCId <> :new.AFSCId
  then
    update Person
      set
        /*  %JoinFKPK(Person,:%New," = ",",") */
        Person.AFSCId = :new.AFSCId
      where
        /*  %JoinFKPK(Person,:%Old," = "," and") */
        Person.AFSCId = :old.AFSCId;
  end if;

  /* AFSC R/82 AFSCTrainingRequirements ON PARENT UPDATE SET NULL */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or " */
    :old.AFSCId <> :new.AFSCId
  then
    update AFSCTrainingRequirements
      set
        /* %SetFK(AFSCTrainingRequirements,NULL) */
        AFSCTrainingRequirements.AFSCId = NULL
      where
        /* %JoinFKPK(AFSCTrainingRequirements,:%Old," = ",",") */
        AFSCTrainingRequirements.AFSCId = :old.AFSCId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_Organization after INSERT on Organization for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on Organization 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* OrgLocationCode R/76 Organization ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from OrgLocationCode
      where
        /* %JoinFKPK(:%New,OrgLocationCode," = "," and") */
        :new.OrgLocationId = OrgLocationCode.OrgLocationId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Organization" because "OrgLocationCode" does not exist.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* OrganizationType R/75 Organization ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from OrganizationType
      where
        /* %JoinFKPK(:%New,OrganizationType," = "," and") */
        :new.OrgTypeId = OrganizationType.OrgTypeId;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Organization" because "OrganizationType" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Organization after UPDATE on Organization for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Organization 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* OrgLocationCode R/76 Organization ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from OrgLocationCode
    where
      /* %JoinFKPK(:%New,OrgLocationCode," = "," and") */
      :new.OrgLocationId = OrgLocationCode.OrgLocationId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Organization" because "OrgLocationCode" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* OrganizationType R/75 Organization ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from OrganizationType
    where
      /* %JoinFKPK(:%New,OrganizationType," = "," and") */
      :new.OrgTypeId = OrganizationType.OrgTypeId;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Organization" because "OrganizationType" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_OrgLocationCode after DELETE on OrgLocationCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on OrgLocationCode 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* OrgLocationCode R/76 Organization ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from Organization
      where
        /*  %JoinFKPK(Organization,:%Old," = "," and") */
        Organization.OrgLocationId = :old.OrgLocationId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "OrgLocationCode" because "Organization" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_OrgLocationCode after UPDATE on OrgLocationCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on OrgLocationCode 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* OrgLocationCode R/76 Organization ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.OrgLocationId <> :new.OrgLocationId
  then
    update Organization
      set
        /*  %JoinFKPK(Organization,:%New," = ",",") */
        Organization.OrgLocationId = :new.OrgLocationId
      where
        /*  %JoinFKPK(Organization,:%Old," = "," and") */
        Organization.OrgLocationId = :old.OrgLocationId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_OrganizationType after DELETE on OrganizationType for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on OrganizationType 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* OrganizationType R/75 Organization ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from Organization
      where
        /*  %JoinFKPK(Organization,:%Old," = "," and") */
        Organization.OrgTypeId = :old.OrgTypeId;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "OrganizationType" because "Organization" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_OrganizationType after UPDATE on OrganizationType for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on OrganizationType 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* OrganizationType R/75 Organization ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.OrgTypeId <> :new.OrgTypeId
  then
    update Organization
      set
        /*  %JoinFKPK(Organization,:%New," = ",",") */
        Organization.OrgTypeId = :new.OrgTypeId
      where
        /*  %JoinFKPK(Organization,:%Old," = "," and") */
        Organization.OrgTypeId = :old.OrgTypeId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_AircraftEngine after INSERT on AircraftEngine for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on AircraftEngine 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Aircraft R/61 AircraftEngine ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Aircraft
      where
        /* %JoinFKPK(:%New,Aircraft," = "," and") */
        :new.MDS = Aircraft.MDS and
        :new.TailNumber = Aircraft.TailNumber;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "AircraftEngine" because "Aircraft" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_AircraftEngine after UPDATE on AircraftEngine for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on AircraftEngine 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Aircraft R/61 AircraftEngine ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Aircraft
    where
      /* %JoinFKPK(:%New,Aircraft," = "," and") */
      :new.MDS = Aircraft.MDS and
      :new.TailNumber = Aircraft.TailNumber;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "AircraftEngine" because "Aircraft" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_CompositeNSN after INSERT on CompositeNSN for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on CompositeNSN 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Composite R/59 CompositeNSN ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Composite
      where
        /* %JoinFKPK(:%New,Composite," = "," and") */
        :new.CompositeID = Composite.CompositeID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "CompositeNSN" because "Composite" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_CompositeNSN after UPDATE on CompositeNSN for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on CompositeNSN 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Composite R/59 CompositeNSN ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Composite
    where
      /* %JoinFKPK(:%New,Composite," = "," and") */
      :new.CompositeID = Composite.CompositeID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "CompositeNSN" because "Composite" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_UTCComposite after INSERT on UTCComposite for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on UTCComposite 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* UTC R/53 UTCComposite ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from UTC
      where
        /* %JoinFKPK(:%New,UTC," = "," and") */
        :new.UTCID = UTC.UTCID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "UTCComposite" because "UTC" does not exist.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Composite R/47 UTCComposite ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Composite
      where
        /* %JoinFKPK(:%New,Composite," = "," and") */
        :new.CompositeID = Composite.CompositeID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "UTCComposite" because "Composite" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_UTCComposite after UPDATE on UTCComposite for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on UTCComposite 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* UTC R/53 UTCComposite ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from UTC
    where
      /* %JoinFKPK(:%New,UTC," = "," and") */
      :new.UTCID = UTC.UTCID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "UTCComposite" because "UTC" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Composite R/47 UTCComposite ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Composite
    where
      /* %JoinFKPK(:%New,Composite," = "," and") */
      :new.CompositeID = Composite.CompositeID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "UTCComposite" because "Composite" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Composite after DELETE on Composite for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Composite 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Composite R/59 CompositeNSN ON PARENT DELETE CASCADE */
    delete from CompositeNSN
      where
        /*  %JoinFKPK(CompositeNSN,:%Old," = "," and") */
        CompositeNSN.CompositeID = :old.CompositeID;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Composite R/47 UTCComposite ON PARENT DELETE CASCADE */
    delete from UTCComposite
      where
        /*  %JoinFKPK(UTCComposite,:%Old," = "," and") */
        UTCComposite.CompositeID = :old.CompositeID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Composite after UPDATE on Composite for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Composite 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Composite R/59 CompositeNSN ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.CompositeID <> :new.CompositeID
  then
    update CompositeNSN
      set
        /*  %JoinFKPK(CompositeNSN,:%New," = ",",") */
        CompositeNSN.CompositeID = :new.CompositeID
      where
        /*  %JoinFKPK(CompositeNSN,:%Old," = "," and") */
        CompositeNSN.CompositeID = :old.CompositeID;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Composite R/47 UTCComposite ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.CompositeID <> :new.CompositeID
  then
    update UTCComposite
      set
        /*  %JoinFKPK(UTCComposite,:%New," = ",",") */
        UTCComposite.CompositeID = :new.CompositeID
      where
        /*  %JoinFKPK(UTCComposite,:%Old," = "," and") */
        UTCComposite.CompositeID = :old.CompositeID;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_FlightDetail after INSERT on FlightDetail for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on FlightDetail 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* FlightReturnCode R/58 FlightDetail ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from FlightReturnCode
      where
        /* %JoinFKPK(:%New,FlightReturnCode," = "," and") */
        :new.FlightReturnCodeID = FlightReturnCode.FlightReturnCodeID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "FlightDetail" because "FlightReturnCode" does not exist.'
      );
    end if;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Flight R/57 FlightDetail ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Flight
      where
        /* %JoinFKPK(:%New,Flight," = "," and") */
        :new.FlightID = Flight.FlightID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "FlightDetail" because "Flight" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_FlightDetail after UPDATE on FlightDetail for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on FlightDetail 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* FlightReturnCode R/58 FlightDetail ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from FlightReturnCode
    where
      /* %JoinFKPK(:%New,FlightReturnCode," = "," and") */
      :new.FlightReturnCodeID = FlightReturnCode.FlightReturnCodeID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "FlightDetail" because "FlightReturnCode" does not exist.'
    );
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Flight R/57 FlightDetail ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Flight
    where
      /* %JoinFKPK(:%New,Flight," = "," and") */
      :new.FlightID = Flight.FlightID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "FlightDetail" because "Flight" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Flight after DELETE on Flight for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Flight 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Flight R/57 FlightDetail ON PARENT DELETE CASCADE */
    delete from FlightDetail
      where
        /*  %JoinFKPK(FlightDetail,:%Old," = "," and") */
        FlightDetail.FlightID = :old.FlightID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_Flight after INSERT on Flight for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on Flight 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Mission R/55 Flight ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Mission
      where
        /* %JoinFKPK(:%New,Mission," = "," and") */
        :new.MissionID = Mission.MissionID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Flight" because "Mission" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Flight after UPDATE on Flight for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Flight 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Flight R/57 FlightDetail ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.FlightID <> :new.FlightID
  then
    update FlightDetail
      set
        /*  %JoinFKPK(FlightDetail,:%New," = ",",") */
        FlightDetail.FlightID = :new.FlightID
      where
        /*  %JoinFKPK(FlightDetail,:%Old," = "," and") */
        FlightDetail.FlightID = :old.FlightID;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Mission R/55 Flight ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Mission
    where
      /* %JoinFKPK(:%New,Mission," = "," and") */
      :new.MissionID = Mission.MissionID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Flight" because "Mission" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_FlightReturnCode after DELETE on FlightReturnCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on FlightReturnCode 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* FlightReturnCode R/58 FlightDetail ON PARENT DELETE CASCADE */
    delete from FlightDetail
      where
        /*  %JoinFKPK(FlightDetail,:%Old," = "," and") */
        FlightDetail.FlightReturnCodeID = :old.FlightReturnCodeID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_FlightReturnCode after UPDATE on FlightReturnCode for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on FlightReturnCode 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* FlightReturnCode R/58 FlightDetail ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.FlightReturnCodeID <> :new.FlightReturnCodeID
  then
    update FlightDetail
      set
        /*  %JoinFKPK(FlightDetail,:%New," = ",",") */
        FlightDetail.FlightReturnCodeID = :new.FlightReturnCodeID
      where
        /*  %JoinFKPK(FlightDetail,:%Old," = "," and") */
        FlightDetail.FlightReturnCodeID = :old.FlightReturnCodeID;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_ScheduledMaintenance after INSERT on ScheduledMaintenance for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on ScheduledMaintenance 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* MaintenanceType MaintenanceTypeMaintenance ScheduledMaintenance ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from MaintenanceType
      where
        /* %JoinFKPK(:%New,MaintenanceType," = "," and") */
        :new.MaintenanceTypeID = MaintenanceType.MaintenanceTypeID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "ScheduledMaintenance" because "MaintenanceType" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_ScheduledMaintenance after UPDATE on ScheduledMaintenance for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on ScheduledMaintenance 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* MaintenanceType MaintenanceTypeMaintenance ScheduledMaintenance ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from MaintenanceType
    where
      /* %JoinFKPK(:%New,MaintenanceType," = "," and") */
      :new.MaintenanceTypeID = MaintenanceType.MaintenanceTypeID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "ScheduledMaintenance" because "MaintenanceType" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_MaintenanceType after DELETE on MaintenanceType for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on MaintenanceType 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* MaintenanceType MaintenanceTypeMaintenance ScheduledMaintenance ON PARENT DELETE RESTRICT */
    select count(*) into numrows
      from ScheduledMaintenance
      where
        /*  %JoinFKPK(ScheduledMaintenance,:%Old," = "," and") */
        ScheduledMaintenance.MaintenanceTypeID = :old.MaintenanceTypeID;
    if (numrows > 0)
    then
      raise_application_error(
        -20001,
        'Cannot DELETE "MaintenanceType" because "ScheduledMaintenance" exists.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_MaintenanceType after UPDATE on MaintenanceType for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on MaintenanceType 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* MaintenanceType MaintenanceTypeMaintenance ScheduledMaintenance ON PARENT UPDATE RESTRICT */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.MaintenanceTypeID <> :new.MaintenanceTypeID
  then
    select count(*) into numrows
      from ScheduledMaintenance
      where
        /*  %JoinFKPK(ScheduledMaintenance,:%Old," = "," and") */
        ScheduledMaintenance.MaintenanceTypeID = :old.MaintenanceTypeID;
    if (numrows > 0)
    then 
      raise_application_error(
        -20005,
        'Cannot UPDATE "MaintenanceType" because "ScheduledMaintenance" exists.'
      );
    end if;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Mission after DELETE on Mission for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Mission 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Mission R/55 Flight ON PARENT DELETE CASCADE */
    delete from Flight
      where
        /*  %JoinFKPK(Flight,:%Old," = "," and") */
        Flight.MissionID = :old.MissionID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Mission after UPDATE on Mission for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Mission 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Mission R/55 Flight ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.MissionID <> :new.MissionID
  then
    update Flight
      set
        /*  %JoinFKPK(Flight,:%New," = ",",") */
        Flight.MissionID = :new.MissionID
      where
        /*  %JoinFKPK(Flight,:%Old," = "," and") */
        Flight.MissionID = :old.MissionID;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_PrototypeDetail after INSERT on PrototypeDetail for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on PrototypeDetail 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Prototype R/56 PrototypeDetail ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Prototype
      where
        /* %JoinFKPK(:%New,Prototype," = "," and") */
        :new.PrototypeID = Prototype.PrototypeID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "PrototypeDetail" because "Prototype" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_PrototypeDetail after UPDATE on PrototypeDetail for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on PrototypeDetail 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Prototype R/56 PrototypeDetail ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Prototype
    where
      /* %JoinFKPK(:%New,Prototype," = "," and") */
      :new.PrototypeID = Prototype.PrototypeID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "PrototypeDetail" because "Prototype" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Prototype after DELETE on Prototype for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Prototype 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Prototype R/56 PrototypeDetail ON PARENT DELETE CASCADE */
    delete from PrototypeDetail
      where
        /*  %JoinFKPK(PrototypeDetail,:%Old," = "," and") */
        PrototypeDetail.PrototypeID = :old.PrototypeID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Prototype after UPDATE on Prototype for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Prototype 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Prototype R/56 PrototypeDetail ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.PrototypeID <> :new.PrototypeID
  then
    update PrototypeDetail
      set
        /*  %JoinFKPK(PrototypeDetail,:%New," = ",",") */
        PrototypeDetail.PrototypeID = :new.PrototypeID
      where
        /*  %JoinFKPK(PrototypeDetail,:%Old," = "," and") */
        PrototypeDetail.PrototypeID = :old.PrototypeID;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_UTCNSN after INSERT on UTCNSN for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on UTCNSN 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* UTC R/60 UTCNSN ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from UTC
      where
        /* %JoinFKPK(:%New,UTC," = "," and") */
        :new.UTCID = UTC.UTCID;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "UTCNSN" because "UTC" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_UTCNSN after UPDATE on UTCNSN for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on UTCNSN 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* UTC R/60 UTCNSN ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from UTC
    where
      /* %JoinFKPK(:%New,UTC," = "," and") */
      :new.UTCID = UTC.UTCID;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "UTCNSN" because "UTC" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_UTC after DELETE on UTC for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on UTC 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* UTC R/60 UTCNSN ON PARENT DELETE CASCADE */
    delete from UTCNSN
      where
        /*  %JoinFKPK(UTCNSN,:%Old," = "," and") */
        UTCNSN.UTCID = :old.UTCID;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* UTC R/53 UTCComposite ON PARENT DELETE CASCADE */
    delete from UTCComposite
      where
        /*  %JoinFKPK(UTCComposite,:%Old," = "," and") */
        UTCComposite.UTCID = :old.UTCID;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_UTC after UPDATE on UTC for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on UTC 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* UTC R/60 UTCNSN ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.UTCID <> :new.UTCID
  then
    update UTCNSN
      set
        /*  %JoinFKPK(UTCNSN,:%New," = ",",") */
        UTCNSN.UTCID = :new.UTCID
      where
        /*  %JoinFKPK(UTCNSN,:%Old," = "," and") */
        UTCNSN.UTCID = :old.UTCID;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* UTC R/53 UTCComposite ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.UTCID <> :new.UTCID
  then
    update UTCComposite
      set
        /*  %JoinFKPK(UTCComposite,:%New," = ",",") */
        UTCComposite.UTCID = :new.UTCID
      where
        /*  %JoinFKPK(UTCComposite,:%Old," = "," and") */
        UTCComposite.UTCID = :old.UTCID;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tI_Supplements after INSERT on Supplements for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- INSERT trigger on Supplements 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Aircraft R/64 Supplements ON CHILD INSERT RESTRICT */
    select count(*) into numrows
      from Aircraft
      where
        /* %JoinFKPK(:%New,Aircraft," = "," and") */
        :new.MDS = Aircraft.MDS and
        :new.TailNumber = Aircraft.TailNumber;
    if (
      /* %NotnullFK(:%New," is not null and") */
      
      numrows = 0
    )
    then
      raise_application_error(
        -20002,
        'Cannot INSERT "Supplements" because "Aircraft" does not exist.'
      );
    end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Supplements after UPDATE on Supplements for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Supplements 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Aircraft R/64 Supplements ON CHILD UPDATE RESTRICT */
  select count(*) into numrows
    from Aircraft
    where
      /* %JoinFKPK(:%New,Aircraft," = "," and") */
      :new.MDS = Aircraft.MDS and
      :new.TailNumber = Aircraft.TailNumber;
  if (
    /* %NotnullFK(:%New," is not null and") */
    
    numrows = 0
  )
  then
    raise_application_error(
      -20007,
      'Cannot UPDATE "Supplements" because "Aircraft" does not exist.'
    );
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tD_Aircraft after DELETE on Aircraft for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- DELETE trigger on Aircraft 
declare numrows INTEGER;
begin
    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Aircraft R/64 Supplements ON PARENT DELETE CASCADE */
    delete from Supplements
      where
        /*  %JoinFKPK(Supplements,:%Old," = "," and") */
        Supplements.MDS = :old.MDS and
        Supplements.TailNumber = :old.TailNumber;

    /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
    /* Aircraft R/61 AircraftEngine ON PARENT DELETE CASCADE */
    delete from AircraftEngine
      where
        /*  %JoinFKPK(AircraftEngine,:%Old," = "," and") */
        AircraftEngine.MDS = :old.MDS and
        AircraftEngine.TailNumber = :old.TailNumber;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_Aircraft after UPDATE on Aircraft for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on Aircraft 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Aircraft R/64 Supplements ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.MDS <> :new.MDS or 
    :old.TailNumber <> :new.TailNumber
  then
    update Supplements
      set
        /*  %JoinFKPK(Supplements,:%New," = ",",") */
        Supplements.MDS = :new.MDS,
        Supplements.TailNumber = :new.TailNumber
      where
        /*  %JoinFKPK(Supplements,:%Old," = "," and") */
        Supplements.MDS = :old.MDS and
        Supplements.TailNumber = :old.TailNumber;
  end if;

  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* Aircraft R/61 AircraftEngine ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.MDS <> :new.MDS or 
    :old.TailNumber <> :new.TailNumber
  then
    update AircraftEngine
      set
        /*  %JoinFKPK(AircraftEngine,:%New," = ",",") */
        AircraftEngine.MDS = :new.MDS,
        AircraftEngine.TailNumber = :new.TailNumber
      where
        /*  %JoinFKPK(AircraftEngine,:%Old," = "," and") */
        AircraftEngine.MDS = :old.MDS and
        AircraftEngine.TailNumber = :old.TailNumber;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

create trigger tU_UTCAFSC after UPDATE on UTCAFSC for each row
-- ERwin Builtin Tue Oct 03 20:17:54 2000
-- UPDATE trigger on UTCAFSC 
declare numrows INTEGER;
begin
  /* ERwin Builtin Tue Oct 03 20:17:54 2000 */
  /* UTCAFSC R/114 UTCPersonnelRequirements ON PARENT UPDATE CASCADE */
  if
    /* %JoinPKPK(:%Old,:%New," <> "," or ") */
    :old.UTCAFSCId <> :new.UTCAFSCId
  then
    update UTCPersonnelRequirements
      set
        /*  %JoinFKPK(UTCPersonnelRequirements,:%New," = ",",") */
        UTCPersonnelRequirements.UTCAFSCId = :new.UTCAFSCId
      where
        /*  %JoinFKPK(UTCPersonnelRequirements,:%Old," = "," and") */
        UTCPersonnelRequirements.UTCAFSCId = :old.UTCAFSCId;
  end if;


-- ERwin Builtin Tue Oct 03 20:17:54 2000
end;
/

