@rem =========================================================================
@rem quick start script
@rem author: pengr 
@rem -------------------------------------------------------------------------
@echo off

if "%~1" EQU "" (
   echo Please input one command.
   goto End
)
set command=%~1
set projectPath = %cd%

cd script
if %command% == merge (
    python merge.py
    cd ..
) else if %command% == split (
    python split.py
    cd ..
) else if %command% == compile (
    call compileJava %~2
    cd ..
) else if %command% == start (
    call startJava %~2
    cd ..
) else if %command% == -h (
    echo    -- merge ^

        "Merge files in doc directory"
    echo    -- split ^

        "Split readme file"
    echo    -- compile xxx ^

        "Compile java code"
    echo    -- start xxx ^

        "Run java code"
    cd ..
)

:End
@rem ---------------------------------------------------------------------
@rem end command
@rem ---------------------------------------------------------------------
goto :EOF