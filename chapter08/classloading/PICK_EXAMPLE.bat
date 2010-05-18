@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0

pushd %_EXAMPLE_DIR_%

SET _OPTION_=%1
SET _NOUSES_=false

IF NOT "%_OPTION_%"=="" GOTO CHOSEN

  ECHO.
  ECHO.Example classloading issues
  ECHO.---------------------------
  ECHO.
  ECHO.1) ClassNotFoundException
  ECHO.2) NoClassDefFoundException
  ECHO.3) ClassCastException
  ECHO.4) No 'uses' constraints
  ECHO.5) Mismatched 'uses'
  ECHO.6) Class.forName issues
  ECHO.7) TCCL loading issues
  ECHO.0) exit
  ECHO.

  SET _OPTION_=

  SET /P _OPTION_="Choose an example (1-7): "

  ECHO.

:CHOSEN

IF "%_OPTION_%"=="0" GOTO FIN
IF "%_OPTION_%"=="" GOTO FIN

IF NOT "%_OPTION_%"=="4" GOTO BUILD

  SET _NOUSES_=true

:BUILD

CALL ant "build_%_OPTION_%" "-Dno.uses=%_NOUSES_%"

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
