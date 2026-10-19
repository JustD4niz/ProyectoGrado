-- =====================================================================
--  Migracion: rol unificado DOCENTE (director y evaluador a la vez)
--  Ejecutar solo si la base de datos ya existe (si la recreas con
--  trabajo_grado.sql no es necesario).
-- =====================================================================
USE trabajo_grado;

ALTER TABLE usuarios
    MODIFY rol ENUM('ADMIN','COMITE','DOCENTE','DIRECTOR','EVALUADOR','ESTUDIANTE') NOT NULL;

-- Los antiguos directores y evaluadores pasan a ser docentes, que pueden
-- desempenar ambos roles en proyectos distintos (nunca en el mismo).
UPDATE usuarios SET rol = 'DOCENTE' WHERE rol IN ('DIRECTOR','EVALUADOR');
