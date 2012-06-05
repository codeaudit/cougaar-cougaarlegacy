drop database if exists Aef;

create database if not exists Aef;

grant all privileges on Aef.* to Aef@localhost identified by 'Alp';
grant all privileges on Aef.* to Aef@"%" identified by 'Alp';

use aef;

CREATE TABLE Oplan (
       OplanId              VARCHAR(15) NOT NULL,
       OperationName        VARCHAR(40),
       CDate                DATE,
       Priority             VARCHAR(10)
);


ALTER TABLE Oplan
       ADD PRIMARY KEY (OplanId);


CREATE TABLE FinalizedOplanAefs (
       AefName              VARCHAR(20) NOT NULL,
       OplanId              VARCHAR(15) NOT NULL
);

CREATE INDEX XIF141FinalizedOplanAefs ON FinalizedOplanAefs
(
       OplanId
);


ALTER TABLE FinalizedOplanAefs
       ADD PRIMARY KEY (AefName, OplanId);


CREATE TABLE OplanAefs (
       AefName              VARCHAR(20) NOT NULL,
       OplanId              VARCHAR(15) NOT NULL
);

CREATE INDEX XIF139OplanAefs ON OplanAefs
(
       OplanId
);


ALTER TABLE OplanAefs
       ADD PRIMARY KEY (AefName, OplanId);


CREATE TABLE FinalizedUTCs (
       UTCId                INTEGER NOT NULL,
       OplanId              VARCHAR(15) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       AefName              VARCHAR(20) NOT NULL,
       NSN                  VARCHAR(50) NOT NULL,
       Quantity             INTEGER NOT NULL,
       StartDate            DATE,
       EndDate              DATE,
       BeddownLocation      VARCHAR(40),
       Capacity             SMALLINT,
       AssetSupportType     VARCHAR(20)
);


ALTER TABLE FinalizedUTCs
       ADD PRIMARY KEY (UTCId, OplanId, AefName, ForcePackageMixId);


CREATE TABLE FinalizedAefOrganizations (
       OrgId                VARCHAR(10) NOT NULL,
       AefName              VARCHAR(20) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       BeddownLocation      VARCHAR(40),
       OplanId              VARCHAR(15) NOT NULL,
       AircraftType         VARCHAR(20),
       MissionTypeCode      VARCHAR(7)
);

CREATE INDEX XIF143FinalizedAefOrganizations ON FinalizedAefOrganizations
(
       AefName,
       OplanId
);


ALTER TABLE FinalizedAefOrganizations
       ADD PRIMARY KEY (OrgId, ForcePackageMixId, AefName, OplanId);


CREATE TABLE FinalizedPhases (
       PhaseId              INTEGER NOT NULL,
       OrgId                VARCHAR(10) NOT NULL,
       AefName              VARCHAR(20) NOT NULL,
       ForcePackageMixId    SMALLINT NOT NULL,
       AircraftQuantity     SMALLINT,
       OplanId              VARCHAR(15) NOT NULL,
       StartDate            DATE,
       SortieRate           VARCHAR(20),
       PhaseNomenclature    VARCHAR(40),
       EndDate              DATE,
       Optempo              VARCHAR(20),
       Phase                SMALLINT
);

CREATE INDEX XIF135FinalizedPhases ON FinalizedPhases
(
       OrgId,
       ForcePackageMixId,
       AefName,
       OplanId
);


ALTER TABLE FinalizedPhases
       ADD PRIMARY KEY (PhaseId, OrgId, ForcePackageMixId, AefName, 
              OplanId);


CREATE TABLE AvailableAircraft (
       AvailableAircraftId  INTEGER NOT NULL,
       OplanId              VARCHAR(15) NOT NULL,
       AircraftType         VARCHAR(20),
       Quantity             SMALLINT
);

CREATE INDEX XIF118AvailableAircraft ON AvailableAircraft
(
       OplanId
);


ALTER TABLE AvailableAircraft
       ADD PRIMARY KEY (AvailableAircraftId, OplanId);


CREATE TABLE AvailableLift (
       AvailableLiftId      INTEGER NOT NULL,
       OplanId              VARCHAR(15) NOT NULL,
       DeliveryDate         DATE,
       Volume               INTEGER,
       Weight               FLOAT
);

CREATE INDEX XIF119AvailableLift ON AvailableLift
(
       OplanId
);


ALTER TABLE AvailableLift
       ADD PRIMARY KEY (AvailableLiftId, OplanId);


CREATE TABLE AFSC (
       AFSCId               INTEGER NOT NULL,
       JobDescription       VARCHAR(60),
       AFSC                 VARCHAR(7) NOT NULL
);

CREATE UNIQUE INDEX AFSC ON AFSC
(
       AFSC,
       JobDescription
);


ALTER TABLE AFSC
       ADD PRIMARY KEY (AFSCId);


CREATE TABLE DeploymentCode (
       DeploymentCodeId     INTEGER NOT NULL,
       Description          VARCHAR(30),
       DeploymentCode       VARCHAR(4)
);

CREATE INDEX DescriptionDeploymentCode ON DeploymentCode
(
       Description,
       DeploymentCode
);


ALTER TABLE DeploymentCode
       ADD PRIMARY KEY (DeploymentCodeId);


CREATE TABLE Location (
       LocationId           INTEGER NOT NULL,
       Description          VARCHAR(30),
       HazardousRating      VARCHAR(4)
);

CREATE INDEX Description ON Location
(
       Description
);


ALTER TABLE Location
       ADD PRIMARY KEY (LocationId);


CREATE TABLE Person (
       AFSCId               INTEGER NOT NULL,
       PersonId             INTEGER NOT NULL,
       SSN                  VARCHAR(9) NOT NULL,
       DeploymentCodeId     INTEGER,
       LocationId           INTEGER,
       LastName             VARCHAR(30) NOT NULL,
       FirstName            VARCHAR(20) NOT NULL,
       MiddleInitial        VARCHAR(1),
       Grade                VARCHAR(10),
       Organization         VARCHAR(20) NOT NULL
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
       ADD PRIMARY KEY (AFSCId, PersonId);


CREATE TABLE Immunizations (
       AFSCId               INTEGER NOT NULL,
       ImmunizationsId      INTEGER NOT NULL,
       PersonId             INTEGER NOT NULL,
       Description          VARCHAR(30) NOT NULL,
       DateGiven            DATE,
       NextDueDate          DATE
);

CREATE INDEX XIF115Immunizations ON Immunizations
(
       AFSCId,
       PersonId
);


ALTER TABLE Immunizations
       ADD PRIMARY KEY (AFSCId, ImmunizationsId, PersonId);


CREATE TABLE UTCAFSC (
       UTCAFSCId            INTEGER NOT NULL,
       JobDescription       VARCHAR(60),
       AFSC                 VARCHAR(7) NOT NULL
);


ALTER TABLE UTCAFSC
       ADD PRIMARY KEY (UTCAFSCId);


CREATE TABLE OrgLocationCode (
       OrgLocationId        INTEGER NOT NULL,
       OrgLocation          VARCHAR(50)
);


ALTER TABLE OrgLocationCode
       ADD PRIMARY KEY (OrgLocationId);


CREATE TABLE OrganizationType (
       OrgTypeId            INTEGER NOT NULL,
       OrgType              VARCHAR(50)
);


ALTER TABLE OrganizationType
       ADD PRIMARY KEY (OrgTypeId);


CREATE TABLE Organization (
       OrganizationId       INTEGER NOT NULL,
       OrgTypeId            INTEGER NOT NULL,
       OrgLocationId        INTEGER NOT NULL,
       OrgName              VARCHAR(20) NOT NULL,
       ParentName           VARCHAR(20)
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
       ADD PRIMARY KEY (OrganizationId, OrgTypeId, OrgLocationId);


CREATE TABLE Aircraft (
       MDS                  VARCHAR(50) NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
       Organization         VARCHAR(20),
       FlightHours          FLOAT NOT NULL
);

CREATE INDEX Organization ON Aircraft
(
       Organization
);


ALTER TABLE Aircraft
       ADD PRIMARY KEY (MDS, TailNumber);


CREATE TABLE Supplements (
       MDS                  VARCHAR(50) NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
       SupplementCnt        INTEGER NOT NULL
);


ALTER TABLE Supplements
       ADD PRIMARY KEY (MDS, TailNumber);


CREATE TABLE DeletedAsset (
       Organization         VARCHAR(50) NOT NULL,
       NSN                  VARCHAR(50),
       SerialNumber         VARCHAR(50)
);

CREATE INDEX organization ON DeletedAsset
(
       Organization
);


CREATE TABLE UTC (
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       Organization         VARCHAR(50)
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
       ADD PRIMARY KEY (UTCID);


CREATE TABLE Composite (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR(50)
);

CREATE INDEX CompositeID ON Composite
(
       CompositeID
);


ALTER TABLE Composite
       ADD PRIMARY KEY (CompositeID);


CREATE TABLE UTCComposite (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       Quantity             INTEGER NOT NULL
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
       ADD PRIMARY KEY (CompositeID, UTCID);


CREATE TABLE UTCNSN (
       UTCID                INTEGER DEFAULT 0 NOT NULL,
       NSN                  VARCHAR(50) NOT NULL,
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


CREATE TABLE Prototype (
       PrototypeID          INTEGER DEFAULT 0 NOT NULL,
       TypeID               VARCHAR(50) NOT NULL,
       Class                VARCHAR(80) NOT NULL
);

CREATE INDEX IdxTypeid ON Prototype
(
       TypeID
);


ALTER TABLE Prototype
       ADD PRIMARY KEY (PrototypeID);


CREATE TABLE PrototypeDetail (
       PrototypeID          INTEGER DEFAULT 0 NOT NULL,
       KeyName              VARCHAR(50) NOT NULL,
       KeyValue             VARCHAR(50) NOT NULL
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
       ADD PRIMARY KEY (PrototypeID, KeyName);


CREATE TABLE Mission (
       MissionID            INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR(50) NOT NULL
);

CREATE INDEX MissionID ON Mission
(
       MissionID
);


ALTER TABLE Mission
       ADD PRIMARY KEY (MissionID);


CREATE TABLE MaintenanceType (
       MaintenanceTypeID    INTEGER DEFAULT 0 NOT NULL,
       MDS                  VARCHAR(50) NOT NULL,
       Nomenclature         VARCHAR(50) NOT NULL,
       Frequency            INTEGER DEFAULT 0,
       FrequencyItemUnitOfMeasure VARCHAR(20) DEFAULT 0,
       Duration             INTEGER DEFAULT 0
);

CREATE INDEX MaintenanceTypeID ON MaintenanceType
(
       MaintenanceTypeID,
       MDS,
       Nomenclature
);


ALTER TABLE MaintenanceType
       ADD PRIMARY KEY (MaintenanceTypeID);


CREATE TABLE ScheduledMaintenance (
       ScheduledMaintenanceID INTEGER DEFAULT 0 NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
       MDS                  VARCHAR(50) NOT NULL,
       MaintenanceTypeID    INTEGER DEFAULT 0 NOT NULL,
       ScheduledDate        DATE
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
       ADD PRIMARY KEY (ScheduledMaintenanceID);


CREATE TABLE FlightReturnCode (
       FlightReturnCodeID   INTEGER DEFAULT 0 NOT NULL,
       Nomenclature         VARCHAR(50) NOT NULL
);

CREATE INDEX ReturnCodeID ON FlightReturnCode
(
       FlightReturnCodeID
);


ALTER TABLE FlightReturnCode
       ADD PRIMARY KEY (FlightReturnCodeID);


CREATE TABLE Flight (
       FlightID             INTEGER DEFAULT 0 NOT NULL,
       MissionID            INTEGER DEFAULT 0 NOT NULL,
       TakeOffTime          DATE NOT NULL,
       MDS                  VARCHAR(50) NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
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
       ADD PRIMARY KEY (FlightID);


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
       ADD PRIMARY KEY (FlightID, FlightReturnCodeID);


CREATE TABLE CompositeNSN (
       CompositeID          INTEGER DEFAULT 0 NOT NULL,
       NSN                  VARCHAR(50) NOT NULL,
       Quantity             INTEGER DEFAULT 0
);

CREATE INDEX XIF59CompositeNSN ON CompositeNSN
(
       CompositeID
);


ALTER TABLE CompositeNSN
       ADD PRIMARY KEY (CompositeID, NSN);


CREATE TABLE Asset (
       AssetID              INTEGER DEFAULT 0 NOT NULL,
       Organization         VARCHAR(50),
       NSN                  VARCHAR(50),
       SerialNumber         VARCHAR(50) DEFAULT 0
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
       ADD PRIMARY KEY (AssetID);


CREATE TABLE AircraftAvailability (
       MDS                  VARCHAR(50) NOT NULL,
       MonthEnding          DATE NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
       FMCHours             INTEGER DEFAULT 0,
       TotalHours           INTEGER
);


ALTER TABLE AircraftAvailability
       ADD PRIMARY KEY (MDS, TailNumber, MonthEnding);


CREATE TABLE AircraftEngine (
       SerialNumber         VARCHAR(50) NOT NULL,
       NSN                  VARCHAR(50) NOT NULL,
       MDS                  VARCHAR(50) NOT NULL,
       TailNumber           VARCHAR(50) NOT NULL,
       EngineHours          FLOAT NOT NULL
);

CREATE INDEX XIF61AircraftEngine ON AircraftEngine
(
       MDS,
       TailNumber
);


ALTER TABLE AircraftEngine
       ADD PRIMARY KEY (SerialNumber, NSN);


CREATE TABLE Airforce_Fuels_DCR_By_Optempo (
       Fuel_Id              FLOAT NOT NULL,
       MDS                  VARCHAR(50),
       Fuel_NSN             VARCHAR(50),
       Optempo              VARCHAR(50),
       Gallons_Per_Day      FLOAT NOT NULL
);

CREATE INDEX MDS ON Airforce_Fuels_DCR_By_Optempo
(
       MDS
);


ALTER TABLE Airforce_Fuels_DCR_By_Optempo
       ADD PRIMARY KEY (Fuel_Id);


CREATE TABLE Airforce_Spares_DCR_By_Optempo (
       Part_Id              FLOAT NOT NULL,
       MDS                  VARCHAR(50),
       NSN                  VARCHAR(50),
       Optempo              VARCHAR(50),
       Demands_Per_Day      FLOAT NOT NULL
);

CREATE INDEX idxMdsNsn ON Airforce_Spares_DCR_By_Optempo
(
       MDS,
       NSN
);


ALTER TABLE Airforce_Spares_DCR_By_Optempo
       ADD PRIMARY KEY (Part_Id);


CREATE TABLE header (
       Id                   FLOAT NOT NULL,
       NSN                  VARCHAR(50),
       AAC                  VARCHAR(50),
       SSC                  VARCHAR(50),
       ICC                  VARCHAR(50),
       Nomenclature         VARCHAR(50)
);


ALTER TABLE header
       ADD PRIMARY KEY (Id);


CREATE TABLE Training (
       TrainingId           INTEGER NOT NULL,
       Description          VARCHAR(30),
       ExpirationNumeric    INTEGER NOT NULL,
       ExpirationUnits      VARCHAR(5)
);

CREATE INDEX Description ON Training
(
       Description
);


ALTER TABLE Training
       ADD PRIMARY KEY (TrainingId);


CREATE TABLE DeploymentHistory (
       DeploymentHistoryId  INTEGER NOT NULL,
       LocationId           INTEGER NOT NULL,
       StartDate            DATE,
       AFSCId               INTEGER NOT NULL,
       EndDate              DATE,
       PersonId             INTEGER NOT NULL
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
       ADD PRIMARY KEY (DeploymentHistoryId, LocationId, AFSCId, 
              PersonId);


CREATE TABLE TrainingHistory (
       PersonId             INTEGER NOT NULL,
       TrainingHistoryId    INTEGER NOT NULL,
       AFSCId               INTEGER NOT NULL,
       TrainingId           INTEGER NOT NULL,
       SuccessfullyCompleted INTEGER NOT NULL,
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
       ADD PRIMARY KEY (PersonId, TrainingHistoryId, AFSCId, 
              TrainingId);


CREATE TABLE AFSCTrainingRequirements (
       AFSCTrainingRequirementsId INTEGER NOT NULL,
       AFSCId               INTEGER NOT NULL,
       TrainingId           INTEGER NOT NULL,
       Required             INTEGER NOT NULL,
       FrequencyNumeric     INTEGER NOT NULL,
       FrequencyUnits       VARCHAR(5) NOT NULL
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
       ADD PRIMARY KEY (AFSCTrainingRequirementsId);


CREATE TABLE ScheduledTraining (
       ScheduledTrainingId  SMALLINT NOT NULL,
       TrainingId           INTEGER NOT NULL,
       AFSCId               INTEGER,
       PersonId             INTEGER,
       StartDate            DATE,
       EndDate              DATE
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
       ADD PRIMARY KEY (ScheduledTrainingId, TrainingId);


CREATE TABLE UTCPersonnelRequirements (
       UTCPersonnelRequirementsId INTEGER NOT NULL,
       UTCAFSCId            INTEGER NOT NULL,
       Quantity             INTEGER NOT NULL,
       AircraftQuantity     INTEGER NOT NULL,
       Organization         VARCHAR(25) NOT NULL,
       AircraftType         VARCHAR(10)
);

CREATE INDEX XIF114UTCPersonnelRequirements ON UTCPersonnelRequirements
(
       UTCAFSCId
);


ALTER TABLE UTCPersonnelRequirements
       ADD PRIMARY KEY (UTCPersonnelRequirementsId, UTCAFSCId);


CREATE TABLE AFITInitiateSemaphore (
       AFITInitiateOplanId  VARCHAR(15)
);


CREATE TABLE AFITCompleteSemaphore (
       AFITCompleteOplanId  VARCHAR(15)
);


CREATE TABLE AefOrganizations (
       OrgId                VARCHAR(10) NOT NULL,
       AefName              VARCHAR(20) NOT NULL,
       BeddownLocation      VARCHAR(40) NOT NULL,
       OplanId              VARCHAR(15) NOT NULL,
       AircraftType         VARCHAR(20),
       MissionTypeCode      VARCHAR(7)
);

CREATE INDEX XIF140AefOrganizations ON AefOrganizations
(
       AefName,
       OplanId
);


ALTER TABLE AefOrganizations
       ADD PRIMARY KEY (OrgId, AefName, OplanId);


CREATE TABLE OplanPhases (
       OrgId                VARCHAR(10) NOT NULL,
       AefName              VARCHAR(20) NOT NULL,
       OplanPhasesId        INTEGER NOT NULL,
       OplanId              VARCHAR(15) NOT NULL,
       SortieRate           VARCHAR(20),
       AircraftQuantity     SMALLINT,
       StartDate            DATE,
       EndDate              DATE,
       Optempo              VARCHAR(20),
       PhaseNomenclature    VARCHAR(40),
       Phase                SMALLINT
);

CREATE INDEX XIF134OplanPhases ON OplanPhases
(
       OrgId,
       AefName,
       OplanId
);


ALTER TABLE OplanPhases
       ADD PRIMARY KEY (OplanPhasesId, OrgId, AefName, OplanId);


CREATE TABLE MissionType (
       MissionTypeCode      VARCHAR(7) NOT NULL,
       Description          VARCHAR(40)
);


ALTER TABLE MissionType
       ADD PRIMARY KEY (MissionTypeCode);


ALTER TABLE FinalizedOplanAefs
       ADD FOREIGN KEY (OplanId)
                             REFERENCES Oplan;


ALTER TABLE OplanAefs
       ADD FOREIGN KEY (OplanId)
                             REFERENCES Oplan;


ALTER TABLE FinalizedAefOrganizations
       ADD FOREIGN KEY (AefName, OplanId)
                             REFERENCES FinalizedOplanAefs;


ALTER TABLE FinalizedPhases
       ADD FOREIGN KEY (OrgId, ForcePackageMixId, AefName, OplanId)
                             REFERENCES FinalizedAefOrganizations;


ALTER TABLE AvailableAircraft
       ADD FOREIGN KEY (OplanId)
                             REFERENCES Oplan;


ALTER TABLE AvailableLift
       ADD FOREIGN KEY (OplanId)
                             REFERENCES Oplan;


ALTER TABLE Person
       ADD FOREIGN KEY (AFSCId)
                             REFERENCES AFSC;


ALTER TABLE Person
       ADD FOREIGN KEY (DeploymentCodeId)
                             REFERENCES DeploymentCode;


ALTER TABLE Person
       ADD FOREIGN KEY (LocationId)
                             REFERENCES Location;


ALTER TABLE Immunizations
       ADD FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person;


ALTER TABLE Organization
       ADD FOREIGN KEY (OrgLocationId)
                             REFERENCES OrgLocationCode;


ALTER TABLE Organization
       ADD FOREIGN KEY (OrgTypeId)
                             REFERENCES OrganizationType;


ALTER TABLE Supplements
       ADD FOREIGN KEY (MDS, TailNumber)
                             REFERENCES Aircraft;


ALTER TABLE UTCComposite
       ADD FOREIGN KEY (UTCID)
                             REFERENCES UTC;


ALTER TABLE UTCComposite
       ADD FOREIGN KEY (CompositeID)
                             REFERENCES Composite;


ALTER TABLE UTCNSN
       ADD FOREIGN KEY (UTCID)
                             REFERENCES UTC;


ALTER TABLE PrototypeDetail
       ADD FOREIGN KEY (PrototypeID)
                             REFERENCES Prototype;


ALTER TABLE ScheduledMaintenance
       ADD FOREIGN KEY (MaintenanceTypeID)
                             REFERENCES MaintenanceType;


ALTER TABLE Flight
       ADD FOREIGN KEY (MissionID)
                             REFERENCES Mission;


ALTER TABLE FlightDetail
       ADD FOREIGN KEY (FlightReturnCodeID)
                             REFERENCES FlightReturnCode;


ALTER TABLE FlightDetail
       ADD FOREIGN KEY (FlightID)
                             REFERENCES Flight;


ALTER TABLE CompositeNSN
       ADD FOREIGN KEY (CompositeID)
                             REFERENCES Composite;


ALTER TABLE AircraftEngine
       ADD FOREIGN KEY (MDS, TailNumber)
                             REFERENCES Aircraft;


ALTER TABLE DeploymentHistory
       ADD FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person;


ALTER TABLE DeploymentHistory
       ADD FOREIGN KEY (LocationId)
                             REFERENCES Location;


ALTER TABLE TrainingHistory
       ADD FOREIGN KEY (TrainingId)
                             REFERENCES Training;


ALTER TABLE TrainingHistory
       ADD FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person;


ALTER TABLE AFSCTrainingRequirements
       ADD FOREIGN KEY (TrainingId)
                             REFERENCES Training;


ALTER TABLE AFSCTrainingRequirements
       ADD FOREIGN KEY (AFSCId)
                             REFERENCES AFSC;


ALTER TABLE ScheduledTraining
       ADD FOREIGN KEY (TrainingId)
                             REFERENCES Training;


ALTER TABLE ScheduledTraining
       ADD FOREIGN KEY (AFSCId, PersonId)
                             REFERENCES Person;


ALTER TABLE UTCPersonnelRequirements
       ADD FOREIGN KEY (UTCAFSCId)
                             REFERENCES UTCAFSC;


ALTER TABLE AefOrganizations
       ADD FOREIGN KEY (AefName, OplanId)
                             REFERENCES OplanAefs;


ALTER TABLE OplanPhases
       ADD FOREIGN KEY (OrgId, AefName, OplanId)
                             REFERENCES AefOrganizations;

