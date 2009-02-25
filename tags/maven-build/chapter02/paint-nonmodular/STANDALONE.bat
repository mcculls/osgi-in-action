@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0

pushd %_EXAMPLE_DIR_%

IF NOT EXIST target/paint.jar CALL mvn install

START javaw -jar target/paint.jar

popd

@ECHO ON
