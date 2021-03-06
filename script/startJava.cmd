@rem =========================================================================
@rem Run java program
@rem author: pengr 
@rem -------------------------------------------------------------------------
@echo off


@rem -------------------------------------------------------------------------
@rem initialize variable
@rem -------------------------------------------------------------------------
setlocal enabledelayedexpansion
set currentPath=%cd%
set javaTest=java-test

if exist "%cd%\%javaTest%" ( set projectPath=%cd% ) else (
    cd %currentPath%\..
)
set projectPath=%cd%
set srcPath=%projectPath%\%javaTest%\src
set binPath=%projectPath%\%javaTest%\bin


if "%~1" EQU "" (
   echo Please input filename to be compiled.
   goto End
)
set javaFile=%~1

@rem -------------------------------------------------------------------------
@rem Finding file and Running
@rem -------------------------------------------------------------------------
for /R %binPath% %%i in (.) do (
    if exist "%%i\%javaFile%.class" ( 
        set "str=%%i"
        set "filePath=!str:~0,-1!"
        set "filePath=!filePath:%binPath%\=!"
        set "filePath=!filePath:\=.!"
        cd %binPath%
        java !filePath!%javaFile%
        goto End
    )
)
echo run %javaFile% failure, File %javaFile%.class not found!


:End
@rem ---------------------------------------------------------------------
@rem end Running
@rem ---------------------------------------------------------------------
cd %currentPath%
goto :EOF
 

