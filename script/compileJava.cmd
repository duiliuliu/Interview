@rem =========================================================================
@rem Compile java programe code
@rem author: pengr 
@rem -------------------------------------------------------------------------
@echo off


@rem -------------------------------------------------------------------------
@rem initialize variable
@rem -------------------------------------------------------------------------
set currentPath=%cd%
if exist "%cd%\test" ( set projectPath=%cd% ) else (
    cd %currentPath%\..
)
set projectPath=%cd%
set srcPath=%projectPath%\test\src\com
set binPath=%projectPath%\test\bin


if "%~1" EQU "" (
   echo Please input filename to be compiled.
   goto End
)
set javaFile=%~1

@rem -------------------------------------------------------------------------
@rem Finding file and Compiling
@rem -------------------------------------------------------------------------
for /R %srcPath% %%i in (.) do (
    if exist "%%i\%javaFile%.java" ( 
        set str=%%i
        set filepath=%str:~0,-1%
        javac -d %binPath% -cp %binPath% %%i\%javaFile%.java 
        echo compile %filepath%%javaFile%.java successfully
        goto End
    )
)
echo compile %javaFile%.java failure, javaFile not found!


:End
@rem ---------------------------------------------------------------------
@rem End compiling
@rem ---------------------------------------------------------------------
cd %currentPath%
goto :EOF
