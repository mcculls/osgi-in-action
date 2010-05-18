@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0

pushd %_EXAMPLE_DIR_%

SET _OPTION_=%1

IF NOT "%_OPTION_%"=="" GOTO CHOSEN

  ECHO.
  ECHO.Dynamic service examples
  ECHO.------------------------
  ECHO.
  ECHO.1) Broken lookup (dangling field)
  ECHO.2) Broken lookup (one-time lookup)
  ECHO.3) Broken lookup (race condition)
  ECHO.4) Correct lookup
  ECHO.5) Broken listener
  ECHO.6) Correct listener
  ECHO.7) Standard tracker
  ECHO.8) Customized tracker
  ECHO.0) exit
  ECHO.

  SET _OPTION_=

  SET /P _OPTION_="Choose an example (1-8): "

  ECHO.

:CHOSEN

IF "%_OPTION_%"=="0" GOTO FIN

CALL ant "build_%_OPTION_%"

IF NOT "%ERRORLEVEL%"=="0" GOTO FIN

ECHO.
ECHO.****************************
ECHO.*                          *
ECHO.* Launching OSGi container *
ECHO.*                          *
ECHO.****************************
ECHO.

java -jar launcher.jar bundles

:FIN

popd

@ECHO ON
