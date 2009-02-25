@ECHO OFF

SET _EXAMPLE_DIR_=%~dp0

cd %_EXAMPLE_DIR_%

ECHO.
ECHO.---------------------------------------
ECHO. BUILDING OSGi-in-Action CODE EXAMPLES 
ECHO.---------------------------------------
ECHO.

CALL mvn install

@ECHO ON
