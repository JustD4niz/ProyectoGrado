-- =====================================================================
-- SISTEMA DE GESTION Y SEGUIMIENTO DE TRABAJOS DE GRADO
-- Motor: PostgreSQL (Neon)
-- Codificacion: UTF-8
-- =====================================================================

DROP TABLE IF EXISTS correos_enviados CASCADE;
DROP TABLE IF EXISTS notificaciones CASCADE;
DROP TABLE IF EXISTS historial_acciones CASCADE;
DROP TABLE IF EXISTS solicitudes CASCADE;
DROP TABLE IF EXISTS rubrica_resultado CASCADE;
DROP TABLE IF EXISTS rubrica_informe CASCADE;
DROP TABLE IF EXISTS sustentaciones CASCADE;
DROP TABLE IF EXISTS seguimientos CASCADE;
DROP TABLE IF EXISTS documentos CASCADE;
DROP TABLE IF EXISTS asignaciones_docente CASCADE;
DROP TABLE IF EXISTS proyecto_integrantes CASCADE;
DROP TABLE IF EXISTS fases_proyecto CASCADE;
DROP TABLE IF EXISTS proyectos CASCADE;
DROP TABLE IF EXISTS ideas CASCADE;
DROP TABLE IF EXISTS periodo_fases CASCADE;
DROP TABLE IF EXISTS periodos_academicos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ---------------------------------------------------------------------
-- 1. USUARIOS Y ROLES
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id                   SERIAL PRIMARY KEY,
    cedula               VARCHAR(20)  NOT NULL,
    nombre               VARCHAR(120) NOT NULL,
    email                VARCHAR(120) NOT NULL UNIQUE,
    password_hash        VARCHAR(255) NOT NULL,
    password_salt        VARCHAR(64)  NOT NULL,
    rol                  VARCHAR(20)  NOT NULL
                         CHECK (rol IN ('ADMIN','COMITE','DOCENTE','DIRECTOR','EVALUADOR','ESTUDIANTE')),
    nivel                VARCHAR(20)  NULL
                         CHECK (nivel IS NULL OR nivel IN ('TECNOLOGIA','INGENIERIA')),
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    consentimiento_datos BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_registro       TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);

-- ---------------------------------------------------------------------
-- 2. CONFIGURACION DEL SISTEMA (RNF23)
-- ---------------------------------------------------------------------
CREATE TABLE configuracion (
    clave       VARCHAR(60)  PRIMARY KEY,
    valor       VARCHAR(600) NOT NULL,
    descripcion VARCHAR(255)
);

-- ---------------------------------------------------------------------
-- 3. PERIODOS ACADEMICOS Y CALENDARIO POR FASE (RF73, RF74)
-- ---------------------------------------------------------------------
CREATE TABLE periodos_academicos (
    id                   SERIAL PRIMARY KEY,
    nombre               VARCHAR(30) NOT NULL,
    fecha_inicio         DATE NOT NULL,
    fecha_fin            DATE NOT NULL,
    semana_pago_max      INT  NOT NULL DEFAULT 8,
    semana_propuesta_max INT  NOT NULL DEFAULT 12,
    dias_duracion        INT  NOT NULL DEFAULT 180,     -- RF36
    dias_min_informe     INT  NOT NULL DEFAULT 90,      -- RF37 (3 meses)
    max_prorrogas        INT  NOT NULL DEFAULT 2,       -- RF50
    activo               BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE periodo_fases (
    id           SERIAL PRIMARY KEY,
    periodo_id   INT NOT NULL,
    fase         VARCHAR(20) NOT NULL
                 CHECK (fase IN ('PAGO','PROPUESTA_I','PROPUESTA_II','INFORME_I','INFORME_II','SUSTENTACION','CERRADO')),
    fecha_minima DATE NULL,
    fecha_maxima DATE NULL,
    responsable  VARCHAR(20) NULL
                 CHECK (responsable IS NULL OR responsable IN ('ESTUDIANTE','DIRECTOR','EVALUADOR','COMITE')),
    CONSTRAINT uq_periodo_fase UNIQUE (periodo_id, fase),
    FOREIGN KEY (periodo_id) REFERENCES periodos_academicos(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 4. BANCO DE IDEAS (RF10 - RF19)
-- ---------------------------------------------------------------------
CREATE TABLE ideas (
    id                   SERIAL PRIMARY KEY,
    titulo               VARCHAR(200) NOT NULL,
    descripcion          TEXT NOT NULL,
    nivel                VARCHAR(20) NOT NULL
                         CHECK (nivel IN ('TECNOLOGIA','INGENIERIA')),
    linea_investigacion  VARCHAR(120) NOT NULL,
    modalidad            VARCHAR(20) NOT NULL
                         CHECK (modalidad IN ('INVESTIGACION','DESARROLLO','MONOGRAFIA')),
    proponente_id        INT NULL,
    origen               VARCHAR(30) NOT NULL DEFAULT 'BANCO'
                         CHECK (origen IN ('BANCO','PROPUESTA_ESTUDIANTE')),
    estudiante_id        INT NULL,
    estado               VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                         CHECK (estado IN ('PENDIENTE','APROBADA','RECHAZADA')),
    observaciones        TEXT NULL,
    disponible           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion       TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_revision       TIMESTAMP NULL,
    revisada_por         INT NULL,
    FOREIGN KEY (proponente_id) REFERENCES usuarios(id),
    FOREIGN KEY (estudiante_id) REFERENCES usuarios(id),
    FOREIGN KEY (revisada_por) REFERENCES usuarios(id)
);
CREATE INDEX idx_ideas_estado ON ideas(estado);
CREATE INDEX idx_ideas_nivel ON ideas(nivel);

-- ---------------------------------------------------------------------
-- 5. PROYECTOS DE GRADO (RF20 - RF54)
-- ---------------------------------------------------------------------
CREATE TABLE proyectos (
    id                       SERIAL PRIMARY KEY,
    codigo                   VARCHAR(30)  NOT NULL UNIQUE,
    titulo                   VARCHAR(250) NOT NULL,
    objetivos                TEXT NULL,
    modalidad                VARCHAR(20) NOT NULL
                             CHECK (modalidad IN ('INVESTIGACION','DESARROLLO','MONOGRAFIA')),
    nivel                    VARCHAR(20) NOT NULL
                             CHECK (nivel IN ('TECNOLOGIA','INGENIERIA')),
    linea_investigacion      VARCHAR(120) NULL,
    periodo_id               INT NOT NULL,
    idea_id                  INT NULL,
    estado                   VARCHAR(20) NOT NULL DEFAULT 'EN_FLUJO'
                             CHECK (estado IN ('EN_FLUJO','FINALIZADO','RECHAZADO','VENCIDO','CANCELADO')),
    fase_actual              VARCHAR(20) NOT NULL DEFAULT 'PAGO'
                              CHECK (fase_actual IN ('PAGO','PROPUESTA_I','PROPUESTA_II','INFORME_I','INFORME_II','SUSTENTACION','CERRADO')),
    responsable_actual       VARCHAR(20) NOT NULL DEFAULT 'ESTUDIANTE'
                             CHECK (responsable_actual IN ('ESTUDIANTE','DIRECTOR','EVALUADOR','COMITE')),
    fecha_aprobacion_propuesta DATE NULL,              -- RF36
    fecha_inicio_180         DATE NULL,
    fecha_vencimiento        DATE NULL,
    prorrogas_utilizadas     SMALLINT NOT NULL DEFAULT 0,
    calificacion_final       NUMERIC(5,2) NULL,
    concepto_evaluador       VARCHAR(30) NULL
                             CHECK (concepto_evaluador IS NULL OR concepto_evaluador IN ('APROBADO','APROBADO_CON_CORRECCIONES','NO_APROBADO')),
    concepto_comite          VARCHAR(20) NULL
                             CHECK (concepto_comite IS NULL OR concepto_comite IN ('APROBADO','RECHAZADO')),
    fecha_creacion           TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (periodo_id) REFERENCES periodos_academicos(id),
    FOREIGN KEY (idea_id) REFERENCES ideas(id)
);
CREATE INDEX idx_proyectos_estado ON proyectos(estado);
CREATE INDEX idx_proyectos_fase ON proyectos(fase_actual);

-- Estado interno de cada fase (RF27, RF44)
CREATE TABLE fases_proyecto (
    id                SERIAL PRIMARY KEY,
    proyecto_id       INT NOT NULL,
    fase              VARCHAR(20) NOT NULL
                      CHECK (fase IN ('PAGO','PROPUESTA_I','PROPUESTA_II','INFORME_I','INFORME_II','SUSTENTACION','CERRADO')),
    estado            VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                      CHECK (estado IN ('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA')),
    responsable       VARCHAR(20) NOT NULL
                      CHECK (responsable IN ('ESTUDIANTE','DIRECTOR','EVALUADOR','COMITE')),
    fecha_aprobacion  TIMESTAMP NULL,
    CONSTRAINT uq_fase_proyecto UNIQUE (proyecto_id, fase),
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE
);

CREATE TABLE proyecto_integrantes (
    id            SERIAL PRIMARY KEY,
    proyecto_id   INT NOT NULL,
    estudiante_id INT NOT NULL,
    estado        VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                  CHECK (estado IN ('ACTIVO','RETIRADO')),
    fecha_union   TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_retiro  TIMESTAMP NULL,
    CONSTRAINT uq_integrante UNIQUE (proyecto_id, estudiante_id),
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (estudiante_id) REFERENCES usuarios(id)
);

CREATE TABLE asignaciones_docente (
    id               SERIAL PRIMARY KEY,
    proyecto_id      INT NOT NULL,
    docente_id       INT NOT NULL,
    rol              VARCHAR(20) NOT NULL CHECK (rol IN ('DIRECTOR','EVALUADOR')),
    activo           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (docente_id) REFERENCES usuarios(id)
);
CREATE INDEX idx_asig_docente ON asignaciones_docente(docente_id, rol);

-- ---------------------------------------------------------------------
-- 6. DOCUMENTOS (URL) CON HISTORIAL DE VERSIONES (RF28 - RF47)
-- ---------------------------------------------------------------------
CREATE TABLE documentos (
    id                   SERIAL PRIMARY KEY,
    proyecto_id          INT NOT NULL,
    fase                 VARCHAR(20) NOT NULL
                         CHECK (fase IN ('PAGO','PROPUESTA_I','PROPUESTA_II','INFORME_I','INFORME_II','SUSTENTACION','CERRADO')),
    tipo                 VARCHAR(30) NOT NULL
                         CHECK (tipo IN ('DESPRENDIBLE_PAGO','PROPUESTA_FDC124','INFORME_FDC125',
                                         'CARTA_PRORROGA','COMPROBANTE_PRORROGA','CARTA_RETIRO',
                                         'CARTA_CAMBIO_DIRECTOR','ACTA_SUSTENTACION','OTRO')),
    url                  VARCHAR(500) NOT NULL,        -- RF47 / RNF06
    version              INT NOT NULL DEFAULT 1,       -- RF45
    autor_id             INT NOT NULL,
    estado               VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                         CHECK (estado IN ('PENDIENTE','APROBADO','RECHAZADO')),
    observaciones        TEXT NULL,                    -- RF40
    visto_bueno_director BOOLEAN NOT NULL DEFAULT FALSE,-- RF38
    fecha                TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);
CREATE INDEX idx_doc_proyecto ON documentos(proyecto_id, fase);

-- ---------------------------------------------------------------------
-- 7. SEGUIMIENTOS ACADEMICOS DEL DIRECTOR (RF42)
-- ---------------------------------------------------------------------
CREATE TABLE seguimientos (
    id            SERIAL PRIMARY KEY,
    proyecto_id   INT NOT NULL,
    director_id   INT NOT NULL,
    fecha         DATE NOT NULL,
    descripcion   TEXT NOT NULL,
    url_evidencia VARCHAR(500) NULL,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- 8. SUSTENTACION (RF43)
-- ---------------------------------------------------------------------
CREATE TABLE sustentaciones (
    id             SERIAL PRIMARY KEY,
    proyecto_id    INT NOT NULL UNIQUE,
    fecha          DATE NOT NULL,
    calificacion   NUMERIC(5,2) NOT NULL,
    url_acta       VARCHAR(500) NULL,
    registrado_por INT NOT NULL,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (registrado_por) REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- 9. RUBRICA DEL INFORME FINAL (RF39)
-- ---------------------------------------------------------------------
CREATE TABLE rubrica_informe (
    id            SERIAL PRIMARY KEY,
    proyecto_id   INT NOT NULL,
    evaluador_id  INT NOT NULL,
    criterio      VARCHAR(150) NOT NULL,
    puntaje       NUMERIC(5,2) NOT NULL,
    maximo        NUMERIC(5,2) NOT NULL DEFAULT 5,
    observaciones TEXT NULL,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluador_id) REFERENCES usuarios(id)
);

CREATE TABLE rubrica_resultado (
    id                           SERIAL PRIMARY KEY,
    proyecto_id                  INT NOT NULL UNIQUE,
    evaluador_id                 INT NOT NULL,
    cumple_objetivo_general      BOOLEAN NOT NULL DEFAULT FALSE,
    cumple_objetivos_especificos BOOLEAN NOT NULL DEFAULT FALSE,
    nota_final                   NUMERIC(5,2) NOT NULL,
    observaciones                TEXT NULL,
    fecha                        TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluador_id) REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- 10. SOLICITUDES: PRORROGA, CAMBIO DIRECTOR, RETIRO, PQRS (RF48 - RF54)
-- ---------------------------------------------------------------------
CREATE TABLE solicitudes (
    id                       SERIAL PRIMARY KEY,
    proyecto_id              INT NOT NULL,
    tipo                     VARCHAR(20) NOT NULL
                             CHECK (tipo IN ('PRORROGA','CAMBIO_DIRECTOR','RETIRO','PQRS')),
    solicitante_id           INT NOT NULL,
    justificacion            TEXT NOT NULL,
    url_carta                VARCHAR(500) NULL,
    url_comprobante          VARCHAR(500) NULL,
    docente_solicitado_id    INT NULL,
    estado                   VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                             CHECK (estado IN ('PENDIENTE','APROBADA','RECHAZADA')),
    observaciones_resolucion TEXT NULL,
    resuelta_por             INT NULL,
    fecha                    TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_resolucion         TIMESTAMP NULL,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (solicitante_id) REFERENCES usuarios(id),
    FOREIGN KEY (docente_solicitado_id) REFERENCES usuarios(id),
    FOREIGN KEY (resuelta_por) REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- 11. HISTORIAL DE ACCIONES (RNF16)
-- ---------------------------------------------------------------------
CREATE TABLE historial_acciones (
    id         SERIAL PRIMARY KEY,
    usuario_id INT NULL,
    entidad    VARCHAR(50) NOT NULL,
    entidad_id INT NULL,
    accion     VARCHAR(100) NOT NULL,
    detalle    TEXT NULL,
    fecha      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_historial_entidad ON historial_acciones(entidad, entidad_id);

-- ---------------------------------------------------------------------
-- 12. NOTIFICACIONES EN PANTALLA (RF59) Y REGISTRO DE CORREOS (RNF20)
-- ---------------------------------------------------------------------
CREATE TABLE notificaciones (
    id         SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL,
    mensaje    VARCHAR(500) NOT NULL,
    enlace     VARCHAR(255) NULL,
    leida      BOOLEAN NOT NULL DEFAULT FALSE,
    fecha      TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE INDEX idx_notif_usuario ON notificaciones(usuario_id, leida);

CREATE TABLE correos_enviados (
    id           SERIAL PRIMARY KEY,
    destinatario VARCHAR(150) NOT NULL,
    asunto       VARCHAR(250) NOT NULL,
    cuerpo       TEXT NULL,
    exitoso      BOOLEAN NOT NULL DEFAULT FALSE,
    intentos     INT NOT NULL DEFAULT 0,
    error        VARCHAR(500) NULL,
    fecha        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =====================================================================
-- DATOS DE PRUEBA
-- Contrasena para todos los usuarios demo: Clave123*
-- (PBKDF2WithHmacSHA256, 10000 iteraciones, sal aleatoria por usuario)
-- =====================================================================

INSERT INTO usuarios (cedula, nombre, email, password_hash, password_salt, rol, nivel, consentimiento_datos) VALUES
('1000000001','Administrador del Sistema','admin@uts.edu.co','5035a63df91e46a07b9fea9d3e45f108a4823ea164ce22e87d39d0cc83ea1ed24b4b82cebaa3fb6059b933366c002f038549abe79f375b1d6e1fc38d3d7385ec','fd87ed0c46664220c1d6edaeca840b99','ADMIN',NULL,TRUE),
('1000000002','Coordinador Comite de Grado','comite@uts.edu.co','d53bf1ee2b577e2f55102c008c800e3b56c92c31ba65d69d3fa6b8b232068c7427496a2eb493c1065f68883cc0038a883dc494638663fd369c52fc9dacb8e69d','c04771dd5ea546b25f08c6641815731b','COMITE',NULL,TRUE),
('1000000003','Maria Fernanda Rojas (Docente)','directora@uts.edu.co','9380f46b22b147f0378c21c2f242758333c2136d4f0b5d016719dde26779cf8bde2252dc7ef4bb969b80ac5234a8b8e112eab2a70a93dce7c1af2fd2d94b9f6e','cdafa37a28ffedc7494094125d493b54','DOCENTE',NULL,TRUE),
('1000000004','Carlos Andres Pineda (Docente)','director2@uts.edu.co','90613f4732c57f7c3d6dd1f09c8f30366b773129bc69ffa86c07da150322698fd41e1ab8b9a832a992ad31390b258e3ffb498c355b559745316e35690e0240ae','177f7ec22894c8ce20ee00222cc14953','DOCENTE',NULL,TRUE),
('1000000005','Luisa Gomez (Docente)','evaluadora@uts.edu.co','d128ac70884681a26d00bd6de73a439d679a9bd15544b69df034e048f180cbb5bfcb1108b8ccd52fddd23a606b4cf1488d201f50423f5fef59821bcf971688c7','b9dfe6b08de4f4d5c15191e132984aec','DOCENTE',NULL,TRUE),
('1000000006','Juan David Perez','juan.perez@uts.edu.co','6cf0361798e3ee9d3127cf7dd480956bcbc4f3af18a1bd43103bcf95eaa8c515ed8e6773412292e4f49d8ffe6e61b5c4ed6948cf3a9543ee5cc6d5b0b0714963','fe0b866783905169e8eb5b60a191b2f7','ESTUDIANTE','INGENIERIA',TRUE),
('1000000007','Ana Sofia Torres','ana.torres@uts.edu.co','ca2887b960f459ba596a3af3f617421ea144581f833e86ce440a874c46e8ef72ccb06b6b7311b7031108e3bf436f5fa81ac6171a280eb80ee012a451e62bd473','c9f852b8419ec7bb4c819a1261911774','ESTUDIANTE','TECNOLOGIA',TRUE),
('1000000008','Pedro Antonio Meza','pedro.meza@uts.edu.co','86b1217263401856d3388ccbce8e3db104143a71badc97e4443728a49367389daa4f718f48b6af9629c7bfac1d0678e01f8a35a5e36f6c91d1bb96c4252ab44c','18343567a6685fd14a08636f70a7c30b','ESTUDIANTE','INGENIERIA',TRUE);

-- ---------------------------------------------------------------------
-- Configuracion base (parametrizable, RNF23 / RF75 / RF76)
-- ---------------------------------------------------------------------
INSERT INTO configuracion (clave, valor, descripcion) VALUES
('institucion.nombre','Institucion Universitaria','Nombre mostrado en la interfaz'),
('email.dominio','uts.edu.co','Dominio de correo institucional permitido'),
('mail.enabled','false','Activa el envio real de correos por SMTP'),
('mail.smtp.host','','Servidor SMTP'),
('mail.smtp.port','587','Puerto SMTP'),
('mail.remitente','notificaciones@uts.edu.co','Remitente de correos'),
('documental.fdc124','https://www.institucion.edu.co/formatos/F-DC-124','Formato F-DC 124 (propuesta)'),
('documental.fdc125','https://www.institucion.edu.co/formatos/F-DC-125','Formato F-DC 125 (informe final)'),
('documental.reglamento','https://www.institucion.edu.co/reglamentos/trabajo-grado','Reglamento de trabajo de grado'),
('documental.estinculos','https://www.institucion.edu.co/reglamentos/estimulos','Reglamento de estimulos'),
('documental.rejilla','https://www.institucion.edu.co/formatos/rejilla-evaluacion','Rejilla institucional de evaluacion');

-- ---------------------------------------------------------------------
-- Periodo academico activo (RF73)
-- ---------------------------------------------------------------------
INSERT INTO periodos_academicos (nombre, fecha_inicio, fecha_fin, semana_pago_max, semana_propuesta_max, dias_duracion, dias_min_informe, max_prorrogas, activo)
VALUES ('2026-2','2026-08-03','2026-12-11',8,12,180,90,2,TRUE);

INSERT INTO periodo_fases (periodo_id, fase, fecha_minima, fecha_maxima, responsable) VALUES
(1,'PAGO','2026-08-03','2026-09-27','COMITE'),
(1,'PROPUESTA_I','2026-09-28','2026-10-08','DIRECTOR'),
(1,'PROPUESTA_II','2026-10-09','2026-10-25','COMITE'),
(1,'INFORME_I','2027-01-25','2027-03-15','ESTUDIANTE'),
(1,'INFORME_II','2027-03-16','2027-04-25','EVALUADOR'),
(1,'SUSTENTACION','2027-04-26','2027-05-15','COMITE'),
(1,'CERRADO','2027-05-16','2027-05-31','COMITE');

-- Periodo anterior (historico, RF72)
INSERT INTO periodos_academicos (nombre, fecha_inicio, fecha_fin, semana_pago_max, semana_propuesta_max, dias_duracion, dias_min_informe, max_prorrogas, activo)
VALUES ('2026-1','2026-02-02','2026-06-12',8,12,180,90,2,FALSE);

-- ---------------------------------------------------------------------
-- Banco de ideas de ejemplo (RF10)
-- ---------------------------------------------------------------------
INSERT INTO ideas (titulo, descripcion, nivel, linea_investigacion, modalidad, proponente_id, estado, disponible, fecha_creacion) VALUES
('Sistema de riego automatizado con IoT','Diseno de un sistema de riego inteligente para cultivos urbanos usando sensores de humedad y microcontroladores.','INGENIERIA','Automatizacion y control','DESARROLLO',3,'APROBADA',TRUE,NOW()),
('Analisis de sentimientos en redes sociales','Aplicacion de tecnicas de procesamiento de lenguaje natural para clasificar opiniones de usuarios.','INGENIERIA','Inteligencia artificial','INVESTIGACION',3,'APROBADA',TRUE,NOW()),
('Aplicacion movil para gestion de inventarios','Desarrollo de una app para pequenos comercios que permita controlar stock y ventas.','TECNOLOGIA','Desarrollo de software','DESARROLLO',4,'APROBADA',TRUE,NOW()),
('Impacto del teletrabajo en la productividad','Monografia sobre los efectos del teletrabajo en empresas del sector tecnologico.','TECNOLOGIA','Gestion organizacional','MONOGRAFIA',4,'APROBADA',TRUE,NOW()),
('Plataforma de aprendizaje adaptativo','Sistema web que adapta contenidos segun el desempeno del estudiante.','INGENIERIA','Informatica educativa','INVESTIGACION',3,'PENDIENTE',TRUE,NOW()),
('Sistema de alertas tempranas para desercion','Modelo predictivo para identificar estudiantes en riesgo de desercion.','INGENIERIA','Analitica de datos','INVESTIGACION',4,'APROBADA',TRUE,NOW());
