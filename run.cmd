@rem =========================================================================
@rem 小脚本
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
) else if %command% == startJava (
    call startJava %~2
    cd ..
)

:End
@rem ---------------------------------------------------------------------
@rem 结束
@rem ---------------------------------------------------------------------
goto :EOF