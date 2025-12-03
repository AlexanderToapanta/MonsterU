/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/11/2025 16:49:37                          */
/*==============================================================*/


ALTER TABLE FEPAGO_PAGO 
   DROP FOREIGN KEY FK_FEPAGO_P_FR_MEEST__MEEST_ES;

ALTER TABLE MEEST_ESTUD 
   DROP FOREIGN KEY FK_MEEST_ES_MR_MECARR_MECARR_C;

ALTER TABLE MEEST_ESTUD 
   DROP FOREIGN KEY FK_MEEST_ES_XR_XEUSU__XEUSU_US;

ALTER TABLE MEGRP_GRUPO 
   DROP FOREIGN KEY FK_MEGRP_GR_MR_MEASIG_MEASIG_A;

ALTER TABLE MEGRP_GRUPO 
   DROP FOREIGN KEY FK_MEGRP_GR_MR_MEPERI_MEPERI_P;

ALTER TABLE MEGRP_GRUPO 
   DROP FOREIGN KEY FK_MEGRP_GR_MR_PEPER__PEPER_PE;

ALTER TABLE MEHOR_HORARIO 
   DROP FOREIGN KEY FK_MEHOR_HO_MR_MEGRP__MEGRP_GR;

ALTER TABLE MR_MEASIG_MEASIG 
   DROP FOREIGN KEY FK_MR_MEASI_MR_MEASIG_MEASIG_A;

ALTER TABLE MR_MEASIG_MEASIG 
   DROP FOREIGN KEY FK_MR_MEASI_MR_MEASIG_MEASIG_A;

ALTER TABLE MR_MEEST_MEGRP 
   DROP FOREIGN KEY FK_MR_MEEST_MR_MEEST__MEEST_ES;

ALTER TABLE MR_MEEST_MEGRP 
   DROP FOREIGN KEY FK_MR_MEEST_MR_MEEST__MEGRP_GR;

ALTER TABLE PEPER_PERSON 
   DROP FOREIGN KEY FK_PEPER_PE_PR_PESEX__PESEX_SE;

ALTER TABLE PEPER_PERSON 
   DROP FOREIGN KEY FK_PEPER_PE_RE_PEESC__PEESC_ES;

ALTER TABLE PEPER_PERSON 
   DROP FOREIGN KEY FK_PEPER_PE_XR_XEUSU__XEUSU_US;

ALTER TABLE XEAUD_AUDLOG 
   DROP FOREIGN KEY FK_XEAUD_AU_XR_XEUSU__XEUSU_US;

ALTER TABLE XEUSU_USUAR 
   DROP FOREIGN KEY FK_XEUSU_US_XR_XEUSU__MEEST_ES;

ALTER TABLE XEUSU_USUAR 
   DROP FOREIGN KEY FK_XEUSU_US_XR_XEUSU__PEPER_PE;

ALTER TABLE XR_XEUSU_XEROL 
   DROP FOREIGN KEY FK_XR_XEUSU_XR_XEUSU__XEUSU_US;

ALTER TABLE XR_XEUSU_XEROL 
   DROP FOREIGN KEY FK_XR_XEUSU_XR_XEUSU__XEROL_RO;


alter table FEPAGO_PAGO 
   drop foreign key FK_FEPAGO_P_FR_MEEST__MEEST_ES;

DROP TABLE IF EXISTS FEPAGO_PAGO;

DROP TABLE IF EXISTS MEASIG_ASIGNA;

DROP TABLE IF EXISTS MECARR_CARRERA;


alter table MEEST_ESTUD 
   drop foreign key FK_MEEST_ES_MR_MECARR_MECARR_C;

alter table MEEST_ESTUD 
   drop foreign key FK_MEEST_ES_XR_XEUSU__XEUSU_US;

DROP TABLE IF EXISTS MEEST_ESTUD;


alter table MEGRP_GRUPO 
   drop foreign key FK_MEGRP_GR_MR_PEPER__PEPER_PE;

alter table MEGRP_GRUPO 
   drop foreign key FK_MEGRP_GR_MR_MEPERI_MEPERI_P;

alter table MEGRP_GRUPO 
   drop foreign key FK_MEGRP_GR_MR_MEASIG_MEASIG_A;

DROP TABLE IF EXISTS MEGRP_GRUPO;


alter table MEHOR_HORARIO 
   drop foreign key FK_MEHOR_HO_MR_MEGRP__MEGRP_GR;

DROP TABLE IF EXISTS MEHOR_HORARIO;

DROP TABLE IF EXISTS MEPERI_PERIOD;


alter table MR_MEASIG_MEASIG 
   drop foreign key FK_MR_MEASI_MR_MEASIG_MEASIG_A;

alter table MR_MEASIG_MEASIG 
   drop foreign key FK_MR_MEASI_MR_MEASIG_MEASIG_A;

DROP TABLE IF EXISTS MR_MEASIG_MEASIG;


alter table MR_MEEST_MEGRP 
   drop foreign key FK_MR_MEEST_MR_MEEST__MEEST_ES;

alter table MR_MEEST_MEGRP 
   drop foreign key FK_MR_MEEST_MR_MEEST__MEGRP_GR;

DROP TABLE IF EXISTS MR_MEEST_MEGRP;

DROP TABLE IF EXISTS PEESC_ESTCIV;


alter table PEPER_PERSON 
   drop foreign key FK_PEPER_PE_XR_XEUSU__XEUSU_US;

alter table PEPER_PERSON 
   drop foreign key FK_PEPER_PE_PR_PESEX__PESEX_SE;

alter table PEPER_PERSON 
   drop foreign key FK_PEPER_PE_RE_PEESC__PEESC_ES;

DROP TABLE IF EXISTS PEPER_PERSON;

DROP TABLE IF EXISTS PESEX_SEXO;


alter table XEAUD_AUDLOG 
   drop foreign key FK_XEAUD_AU_XR_XEUSU__XEUSU_US;

DROP TABLE IF EXISTS XEAUD_AUDLOG;

DROP TABLE IF EXISTS XEROL_ROL;


alter table XEUSU_USUAR 
   drop foreign key FK_XEUSU_US_XR_XEUSU__PEPER_PE;

alter table XEUSU_USUAR 
   drop foreign key FK_XEUSU_US_XR_XEUSU__MEEST_ES;

DROP TABLE IF EXISTS XEUSU_USUAR;


alter table XR_XEUSU_XEROL 
   drop foreign key FK_XR_XEUSU_XR_XEUSU__XEUSU_US;

alter table XR_XEUSU_XEROL 
   drop foreign key FK_XR_XEUSU_XR_XEUSU__XEROL_RO;

DROP TABLE IF EXISTS XR_XEUSU_XEROL;

/*==============================================================*/
/* Table: FEPAGO_PAGO                                           */
/*==============================================================*/
CREATE TABLE FEPAGO_PAGO
(
   FEPAGO_ID            CHAR(5) NOT NULL  COMMENT '',
   MECARR_ID            CHAR(5) NOT NULL  COMMENT '',
   MEEST_ID             CHAR(5) NOT NULL  COMMENT '',
   FEPAGO_MONTO         DECIMAL NOT NULL  COMMENT '',
   FEPAGO_FECHA         DATE NOT NULL  COMMENT '',
   FEPAGO_REFE          VARCHAR(50) NOT NULL  COMMENT '',
   FEPAGO_ESTADO        VARCHAR(20) NOT NULL  COMMENT '',
   PRIMARY KEY (FEPAGO_ID)
);

ALTER TABLE FEPAGO_PAGO COMMENT 'Entidad que se utiliza para alamacenar los pagos';

/*==============================================================*/
/* Table: MEASIG_ASIGNA                                         */
/*==============================================================*/
CREATE TABLE MEASIG_ASIGNA
(
   MEASIG_ID            VARCHAR(5) NOT NULL  COMMENT '',
   MEASIG_NOMBRE        VARCHAR(30) NOT NULL  COMMENT '',
   MEASIG_NRC           VARCHAR(6) NOT NULL  COMMENT '',
   MEASIG_CREDITOS      INT NOT NULL  COMMENT '',
   MEASIG_DESCRIPCION   VARCHAR(100)  COMMENT '',
   PRIMARY KEY (MEASIG_ID)
);

ALTER TABLE MEASIG_ASIGNA COMMENT 'Entidad que se utiliza para almacenar las asginaturas';

/*==============================================================*/
/* Table: MECARR_CARRERA                                        */
/*==============================================================*/
CREATE TABLE MECARR_CARRERA
(
   MECARR_ID            CHAR(5) NOT NULL  COMMENT '',
   MECARR_NOMBRE        VARCHAR(25) NOT NULL  COMMENT '',
   MECARR_MAX_CRED      INT NOT NULL  COMMENT '',
   MECARR_MIN_CRED      INT NOT NULL  COMMENT '',
   PRIMARY KEY (MECARR_ID)
);

ALTER TABLE MECARR_CARRERA COMMENT 'Entidad que se encarga de guardar carreras';

/*==============================================================*/
/* Table: MEEST_ESTUD                                           */
/*==============================================================*/
CREATE TABLE MEEST_ESTUD
(
   MECARR_ID            CHAR(5) NOT NULL  COMMENT '',
   MEEST_ID             CHAR(5) NOT NULL  COMMENT '',
   XEUSU_ID             CHAR(5)  COMMENT '',
   MEEST_NOMBRE         VARCHAR(25) NOT NULL  COMMENT '',
   MEEST_APELLIDO       VARCHAR(25) NOT NULL  COMMENT '',
   MEEST_CEDULA         VARCHAR(15) NOT NULL  COMMENT '',
   MEEST_EMAIL          VARCHAR(30) NOT NULL  COMMENT '',
   MEEST_FECHANA        DATE NOT NULL  COMMENT '',
   MEEST_PROMEDIO       DECIMAL NOT NULL  COMMENT '',
   PRIMARY KEY (MECARR_ID, MEEST_ID)
);

ALTER TABLE MEEST_ESTUD COMMENT 'Entidad que se utiliza para almacenar los estudiantes';

/*==============================================================*/
/* Table: MEGRP_GRUPO                                           */
/*==============================================================*/
CREATE TABLE MEGRP_GRUPO
(
   MEGRP_ID             CHAR(5) NOT NULL  COMMENT '',
   MEPERI_ID            VARCHAR(5)  COMMENT '',
   PEPER_ID             CHAR(5) NOT NULL  COMMENT '',
   MEASIG_ID            VARCHAR(5) NOT NULL  COMMENT '',
   MEGRP_CODIGO         VARCHAR(20) NOT NULL  COMMENT '',
   MEGRP_AULA           VARCHAR(35) NOT NULL  COMMENT '',
   MEGRP_CUPO_MAX       INT NOT NULL  COMMENT '',
   MEGRP_CUPO_ACT       INT NOT NULL  COMMENT '',
   PRIMARY KEY (MEGRP_ID)
);

ALTER TABLE MEGRP_GRUPO COMMENT 'Entidad que se encarga de almacenar los grupos
';

/*==============================================================*/
/* Table: MEHOR_HORARIO                                         */
/*==============================================================*/
CREATE TABLE MEHOR_HORARIO
(
   MEGRP_ID             CHAR(5) NOT NULL  COMMENT '',
   MEHOR_ID             CHAR(5) NOT NULL  COMMENT '',
   MEHOR_DIA            VARCHAR(15) NOT NULL  COMMENT '',
   MEHOR_FECHA_I        TIME NOT NULL  COMMENT '',
   MEHOR_FECHA_F        TIME NOT NULL  COMMENT '',
   PRIMARY KEY (MEGRP_ID, MEHOR_ID)
);

ALTER TABLE MEHOR_HORARIO COMMENT 'Entidad que se utiliza para almacenar los horarios';

/*==============================================================*/
/* Table: MEPERI_PERIOD                                         */
/*==============================================================*/
CREATE TABLE MEPERI_PERIOD
(
   MEPERI_ID            VARCHAR(5) NOT NULL  COMMENT '',
   MEPERI_NOMBRE        VARCHAR(30) NOT NULL  COMMENT '',
   MEPERI_FECHAINI      DATE NOT NULL  COMMENT '',
   MEPERI_FECHAFIN      DATE NOT NULL  COMMENT '',
   MEPERI_ACTIVO        BOOL NOT NULL  COMMENT '',
   PRIMARY KEY (MEPERI_ID)
);

ALTER TABLE MEPERI_PERIOD COMMENT 'Entidad para almacenar los periodos';

/*==============================================================*/
/* Table: MR_MEASIG_MEASIG                                      */
/*==============================================================*/
CREATE TABLE MR_MEASIG_MEASIG
(
   MEASIG_ID            VARCHAR(5) NOT NULL  COMMENT '',
   MEA_MEASIG_ID        VARCHAR(5) NOT NULL  COMMENT '',
   PRIMARY KEY (MEASIG_ID, MEA_MEASIG_ID)
);

ALTER TABLE MR_MEASIG_MEASIG COMMENT 'Relación de la entidad MEASIG_ASIGNA';

/*==============================================================*/
/* Table: MR_MEEST_MEGRP                                        */
/*==============================================================*/
CREATE TABLE MR_MEEST_MEGRP
(
   MECARR_ID            CHAR(5) NOT NULL  COMMENT '',
   MEEST_ID             CHAR(5) NOT NULL  COMMENT '',
   MEGRP_ID             CHAR(5) NOT NULL  COMMENT '',
   PRIMARY KEY (MECARR_ID, MEEST_ID, MEGRP_ID)
);

ALTER TABLE MR_MEEST_MEGRP COMMENT 'Relación entre la entidad MEEST_ESTUD y la entidad MEGRP_GRU';

/*==============================================================*/
/* Table: PEESC_ESTCIV                                          */
/*==============================================================*/
CREATE TABLE PEESC_ESTCIV
(
   PEESC_ID             CHAR(1) NOT NULL  COMMENT '',
   PEESC_DESCRI         VARCHAR(50) NOT NULL  COMMENT '',
   PRIMARY KEY (PEESC_ID)
);

ALTER TABLE PEESC_ESTCIV COMMENT 'Entidad utilizada para realizar el CRUD del ESTADO CIVIL de ';

/*==============================================================*/
/* Table: PEPER_PERSON                                          */
/*==============================================================*/
CREATE TABLE PEPER_PERSON
(
   PEPER_ID             CHAR(5) NOT NULL  COMMENT '',
   PEESC_ID             CHAR(1)  COMMENT '',
   XEUSU_ID             CHAR(5)  COMMENT '',
   PESEX_ID             VARCHAR(1) NOT NULL  COMMENT '',
   PEPER_NOMBRE         VARCHAR(25) NOT NULL  COMMENT '',
   PEPER_APELLIDO       VARCHAR(25) NOT NULL  COMMENT '',
   PEPER_EMAIL          VARCHAR(30) NOT NULL  COMMENT '',
   PEPER_CEDULA         VARCHAR(15) NOT NULL  COMMENT '',
   PEPER_CELULAR        VARCHAR(15)  COMMENT '',
   PEPER_TIPO           VARCHAR(30) NOT NULL  COMMENT '',
   PEPEPER_FECH_INGR    DATE NOT NULL  COMMENT '',
   PRIMARY KEY (PEPER_ID)
);

ALTER TABLE PEPER_PERSON COMMENT 'Entidad que se utiliza para almacenar el personal
';

/*==============================================================*/
/* Table: PESEX_SEXO                                            */
/*==============================================================*/
CREATE TABLE PESEX_SEXO
(
   PESEX_ID             VARCHAR(1) NOT NULL  COMMENT '',
   PESEX_DESCRI         VARCHAR(50) NOT NULL  COMMENT '',
   PRIMARY KEY (PESEX_ID)
);

ALTER TABLE PESEX_SEXO COMMENT 'Entidad utilizada para realizar el CRUD del sexo o genero de';

/*==============================================================*/
/* Table: XEAUD_AUDLOG                                          */
/*==============================================================*/
CREATE TABLE XEAUD_AUDLOG
(
   IDAUDITORIA          CHAR(5) NOT NULL  COMMENT '',
   XEUSU_ID             CHAR(5) NOT NULL  COMMENT '',
   ACCION               VARCHAR(100) NOT NULL  COMMENT '',
   TABLAAFEC            VARCHAR(100) NOT NULL  COMMENT '',
   FILAID               INT NOT NULL  COMMENT '',
   FECHAHORA            DATETIME NOT NULL  COMMENT '',
   DETALLE              TEXT NOT NULL  COMMENT '',
   PRIMARY KEY (IDAUDITORIA)
);

ALTER TABLE XEAUD_AUDLOG COMMENT 'Entidad que se encarga de registrar acciones críticas';

/*==============================================================*/
/* Table: XEROL_ROL                                             */
/*==============================================================*/
CREATE TABLE XEROL_ROL
(
   XEROL_ID             CHAR(5) NOT NULL  COMMENT '',
   XEROL_NOMBRE         VARCHAR(30)  COMMENT '',
   XEROL_DESCRI         VARCHAR(100)  COMMENT '',
   PRIMARY KEY (XEROL_ID)
);

ALTER TABLE XEROL_ROL COMMENT 'Entidad que se encarga de almenar los roles';

/*==============================================================*/
/* Table: XEUSU_USUAR                                           */
/*==============================================================*/
CREATE TABLE XEUSU_USUAR
(
   XEUSU_ID             CHAR(5) NOT NULL  COMMENT '',
   PEPER_ID             CHAR(5)  COMMENT '',
   MECARR_ID            CHAR(5)  COMMENT '',
   MEEST_ID             CHAR(5)  COMMENT '',
   XEUSU_NOMBRE         VARCHAR(100) NOT NULL  COMMENT '',
   XEUSU_CONTRA         VARCHAR(100) NOT NULL  COMMENT '',
   XEUSU_ESTADO         VARCHAR(30) NOT NULL  COMMENT '',
   PRIMARY KEY (XEUSU_ID)
);

ALTER TABLE XEUSU_USUAR COMMENT 'Entidad que almacena los usuarios que ingresan al sistema';

/*==============================================================*/
/* Table: XR_XEUSU_XEROL                                        */
/*==============================================================*/
CREATE TABLE XR_XEUSU_XEROL
(
   XEUSU_ID             CHAR(5) NOT NULL  COMMENT '',
   XEROL_ID             CHAR(5) NOT NULL  COMMENT '',
   PRIMARY KEY (XEUSU_ID, XEROL_ID)
);

ALTER TABLE XR_XEUSU_XEROL COMMENT 'Relación entre las entidades XEUSU_USUAR y la entidad XEROL_';

ALTER TABLE FEPAGO_PAGO ADD CONSTRAINT FK_FEPAGO_P_FR_MEEST__MEEST_ES FOREIGN KEY (MECARR_ID, MEEST_ID)
      REFERENCES MEEST_ESTUD (MECARR_ID, MEEST_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEEST_ESTUD ADD CONSTRAINT FK_MEEST_ES_MR_MECARR_MECARR_C FOREIGN KEY (MECARR_ID)
      REFERENCES MECARR_CARRERA (MECARR_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEEST_ESTUD ADD CONSTRAINT FK_MEEST_ES_XR_XEUSU__XEUSU_US FOREIGN KEY (XEUSU_ID)
      REFERENCES XEUSU_USUAR (XEUSU_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEGRP_GRUPO ADD CONSTRAINT FK_MEGRP_GR_MR_MEASIG_MEASIG_A FOREIGN KEY (MEASIG_ID)
      REFERENCES MEASIG_ASIGNA (MEASIG_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEGRP_GRUPO ADD CONSTRAINT FK_MEGRP_GR_MR_MEPERI_MEPERI_P FOREIGN KEY (MEPERI_ID)
      REFERENCES MEPERI_PERIOD (MEPERI_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEGRP_GRUPO ADD CONSTRAINT FK_MEGRP_GR_MR_PEPER__PEPER_PE FOREIGN KEY (PEPER_ID)
      REFERENCES PEPER_PERSON (PEPER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEHOR_HORARIO ADD CONSTRAINT FK_MEHOR_HO_MR_MEGRP__MEGRP_GR FOREIGN KEY (MEGRP_ID)
      REFERENCES MEGRP_GRUPO (MEGRP_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MR_MEASIG_MEASIG ADD CONSTRAINT FK_MR_MEASI_MR_MEASIG_MEASIG_A FOREIGN KEY (MEASIG_ID)
      REFERENCES MEASIG_ASIGNA (MEASIG_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MR_MEASIG_MEASIG ADD CONSTRAINT FK_MR_MEASI_MR_MEASIG_MEASIG_A FOREIGN KEY (MEA_MEASIG_ID)
      REFERENCES MEASIG_ASIGNA (MEASIG_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MR_MEEST_MEGRP ADD CONSTRAINT FK_MR_MEEST_MR_MEEST__MEEST_ES FOREIGN KEY (MECARR_ID, MEEST_ID)
      REFERENCES MEEST_ESTUD (MECARR_ID, MEEST_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MR_MEEST_MEGRP ADD CONSTRAINT FK_MR_MEEST_MR_MEEST__MEGRP_GR FOREIGN KEY (MEGRP_ID)
      REFERENCES MEGRP_GRUPO (MEGRP_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PE_PR_PESEX__PESEX_SE FOREIGN KEY (PESEX_ID)
      REFERENCES PESEX_SEXO (PESEX_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PE_RE_PEESC__PEESC_ES FOREIGN KEY (PEESC_ID)
      REFERENCES PEESC_ESTCIV (PEESC_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PEPER_PERSON ADD CONSTRAINT FK_PEPER_PE_XR_XEUSU__XEUSU_US FOREIGN KEY (XEUSU_ID)
      REFERENCES XEUSU_USUAR (XEUSU_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE XEAUD_AUDLOG ADD CONSTRAINT FK_XEAUD_AU_XR_XEUSU__XEUSU_US FOREIGN KEY (XEUSU_ID)
      REFERENCES XEUSU_USUAR (XEUSU_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE XEUSU_USUAR ADD CONSTRAINT FK_XEUSU_US_XR_XEUSU__MEEST_ES FOREIGN KEY (MECARR_ID, MEEST_ID)
      REFERENCES MEEST_ESTUD (MECARR_ID, MEEST_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE XEUSU_USUAR ADD CONSTRAINT FK_XEUSU_US_XR_XEUSU__PEPER_PE FOREIGN KEY (PEPER_ID)
      REFERENCES PEPER_PERSON (PEPER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE XR_XEUSU_XEROL ADD CONSTRAINT FK_XR_XEUSU_XR_XEUSU__XEUSU_US FOREIGN KEY (XEUSU_ID)
      REFERENCES XEUSU_USUAR (XEUSU_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE XR_XEUSU_XEROL ADD CONSTRAINT FK_XR_XEUSU_XR_XEUSU__XEROL_RO FOREIGN KEY (XEROL_ID)
      REFERENCES XEROL_ROL (XEROL_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

