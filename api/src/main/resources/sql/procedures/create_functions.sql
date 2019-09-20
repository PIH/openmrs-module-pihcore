/*
  This file contains common functions that are useful within the stored procedures defined in this folder
*/

/*
 get concept_id from report_mapping table
*/
DROP FUNCTION IF EXISTS concept_from_mapping;
#
CREATE FUNCTION concept_from_mapping(
	_source varchar(50),
    _code varchar(255)
)
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE mappedConcept INT;

	SELECT concept_id INTO mappedConcept FROM report_mapping WHERE source = _source and code = _code;

    RETURN mappedConcept;

END
#

/*
get names from the concept_name table
*/
DROP FUNCTION IF EXISTS concept_name;
#
CREATE FUNCTION concept_name(
    _conceptID INT,
    _locale varchar(50)
)
	RETURNS VARCHAR(255)
    DETERMINISTIC

BEGIN
    DECLARE conceptName varchar(255);

	SELECT name INTO conceptName FROM concept_name WHERE voided = 0 AND concept_id = _conceptID AND locale = _locale AND concept_name_type
    = "FULLY_SPECIFIED";

    RETURN conceptName;
END
#

/*
get names from the concept_name table
*/
DROP FUNCTION IF EXISTS encounter_type;
#
CREATE FUNCTION encounter_type(
    _encounter_type_name varchar(255)
)
	RETURNS INT
    DETERMINISTIC

BEGIN
    DECLARE enconterType varchar(255);

	SELECT encounter_type_id INTO enconterType FROM encounter_type WHERE retired = 0 and
    name = _encounter_type_name;

    RETURN enconterType;

END
#

/*
 get patient age at encounter
*/
DROP FUNCTION IF EXISTS age_at_enc;
#
CREATE FUNCTION age_at_enc(
    _person_id int
)
	RETURNS DOUBLE
    DETERMINISTIC

BEGIN
    DECLARE ageAtEnc DOUBLE;

	select round(datediff(encounter_datetime, birthdate)/365.25,1) into ageAtENC from encounter e join person p on patient_id = person_id and e.voided = 0
	and p.voided = 0 and person_id = _person_id group by encounter_id LIMIT 1;

    RETURN ageAtEnc;

END
#

/*
get patient EMR ZL
*/
DROP FUNCTION IF EXISTS zlemr;
#
CREATE FUNCTION zlemr(
    _patient_id int
)
	RETURNS VARCHAR(255)
    DETERMINISTIC

BEGIN
    DECLARE  zlEMR VARCHAR(255);

    SELECT pid.identifier into zlEMR from patient_identifier pid where voided = 0 and pid.identifier_type = (select pid2.patient_identifier_type_id from patient_identifier_type pid2 where
pid2.name = 'ZL EMR ID') and patient_id = _patient_id order by preferred desc limit 1;

    RETURN zlEMR;

END
#

/*
unknown patient
*/
DROP FUNCTION IF EXISTS unknown_patient;
#
CREATE FUNCTION unknown_patient(
    _patient_id int
)
	RETURNS VARCHAR(50)
    DETERMINISTIC

BEGIN
    DECLARE  unknownPatient VARCHAR(50);

	select person_id into unknownPatient from person_attribute where person_attribute_type_id = (select person_attribute_type_id from
person_attribute_type where name = 'Unknown patient') and voided = 0 and person_id = _patient_id;

    RETURN unknownPatient;

END
#

/*
gender
*/
DROP FUNCTION IF EXISTS gender;
#
CREATE FUNCTION gender(
    _patient_id int
)
	RETURNS VARCHAR(50)
    DETERMINISTIC

BEGIN
    DECLARE  patientGender VARCHAR(50);

	select gender into patientGender from person where person_id = _patient_id and voided =0;

    RETURN patientGender;

END
#

/*
 patient address
*/
DROP FUNCTION IF EXISTS person_address;
#
CREATE FUNCTION person_address(
    _patient_id int
)
	RETURNS TEXT
    DETERMINISTIC

BEGIN
    DECLARE patientAddress TEXT;

	select concat(IFNULL(state_province,''), ',' ,IFNULL(city_village,''), ',', IFNULL(address3,''), ',', IFNULL(address1,''), ',',IFNULL(address2,'')) into patientAddress
    from person_address where voided = 0 and person_id = _patient_id order by preferred desc, date_created desc limit 1;

    RETURN patientAddress;

END
#

/*
  ZL EMR ID location
*/

DROP FUNCTION IF EXISTS loc_registered;
#
CREATE FUNCTION loc_registered(
    _patient_id int
)
	RETURNS VARCHAR(255)
    DETERMINISTIC

BEGIN
    DECLARE locRegistered varchar(255);

select name into locRegistered from location l join patient_identifier pi on pi.location_id = l.location_id and pi.voided = 0 and pi.patient_id = _patient_id
and identifier_type = (select pid2.patient_identifier_type_id from patient_identifier_type pid2 where
 pid2.name = 'ZL EMR ID');

    RETURN locRegistered;

END
#