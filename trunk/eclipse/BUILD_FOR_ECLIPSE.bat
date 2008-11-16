@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0/..

cd %_EXAMPLE_DIR_%

ECHO.
ECHO.---------------------------------------------------
ECHO. BUILDING OSGi-in-Action CODE EXAMPLES FOR ECLIPSE 
ECHO.---------------------------------------------------
ECHO.

SET /P _WORKSPACE_="Path to existing Eclipse workspace? "
ECHO.

IF EXIST %_WORKSPACE_% GOTO SETUPWORKSPACE
  ECHO.No such directory: %_WORKSPACE_%
  GOTO DONEWORKSPACE
:SETUPWORKSPACE
  ECHO.Adding M2_REPO classpath variable to: %_WORKSPACE_%
  ECHO.
  CALL mvn -N eclipse:configure-workspace "-Declipse.workspace=%_WORKSPACE_%"
:DONEWORKSPACE

ECHO.
ECHO.Generating Eclipse project files
ECHO.

CALL mvn install pax:eclipse

ECHO.
ECHO.----------------------------------------------------
ECHO. TO IMPORT THE EXAMPLES INTO YOUR ECLIPSE WORKSPACE 
ECHO.----------------------------------------------------
ECHO.
ECHO. File... Import... Existing Projects into Workspace 
ECHO.

@ECHO ON
