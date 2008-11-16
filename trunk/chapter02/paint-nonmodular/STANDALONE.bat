@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0
SET _EXAMPLE_BSN_=org.foo.paint
SET _EXAMPLE_VER_=1.0

SET _EXAMPLE_JAR_=%_EXAMPLE_BSN_%-%_EXAMPLE_VER_%.jar

pushd %_EXAMPLE_DIR_%

IF NOT EXIST target/%_EXAMPLE_JAR_% CALL mvn install

START javaw -jar target/%_EXAMPLE_JAR_%

popd

@ECHO ON
