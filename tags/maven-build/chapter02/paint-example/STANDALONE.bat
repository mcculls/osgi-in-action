@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0

pushd %_EXAMPLE_DIR_%

IF NOT EXIST target/launch CALL mvn install

cd target/launch

START javaw -jar launcher.jar bundles

popd

@ECHO ON
