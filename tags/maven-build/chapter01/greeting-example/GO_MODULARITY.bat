@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0
SET _EXAMPLE_BSN_=org.foo.hello.main
SET _EXAMPLE_VER_=1.0

SET _EXAMPLE_JAR_=%_EXAMPLE_BSN_%-%_EXAMPLE_VER_%.jar

pushd %_EXAMPLE_DIR_%\modularity

IF NOT EXIST %_EXAMPLE_BSN_%/target/%_EXAMPLE_JAR_% CALL mvn install

cd %_EXAMPLE_BSN_%

java -jar target/%_EXAMPLE_JAR_%

popd

@ECHO ON
