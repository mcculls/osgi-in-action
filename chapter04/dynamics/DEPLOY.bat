@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0
SET _EXAMPLE_BSN_=org.foo.log.service
SET _EXAMPLE_VER_=1.0

SET _EXAMPLE_JAR_=%_EXAMPLE_BSN_%-%_EXAMPLE_VER_%.jar

SET _BUNDLES_=%1,%2,%3,%4,%5,%6,%7,%8

IF NOT "%_BUNDLES_%"==",,,,,,," GOTO CHOSEN

  ECHO.
  ECHO.Dynamic service examples"
  ECHO.------------------------"
  ECHO.
  ECHO.1) Broken lookup (dangling field)
  ECHO.2) Broken lookup (one-time lookup)
  ECHO.3) Broken lookup (race condition)
  ECHO.4) Correct lookup
  ECHO.5) Broken listener
  ECHO.6) Correct listener
  ECHO.7) Standard tracker
  ECHO.8) Customized tracker
  ECHO.

  SET _BUNDLES_=

  SET /P _BUNDLES_="Choose an example (1-8): "

  ECHO.

:CHOSEN

pushd %_EXAMPLE_DIR_%

IF ""=="%_BUNDLES_%" GOTO CLEANUP

  IF NOT EXIST %_EXAMPLE_BSN_%/target/%_EXAMPLE_JAR_% CALL mvn install

  CALL mvn pax:provision "-P%_BUNDLES_%"

:CLEANUP

popd

@ECHO ON
