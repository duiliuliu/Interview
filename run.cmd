@rem =========================================================================
@rem С�ű�
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
) else if %command% == -h (
    echo -- merge \n\t �ϲ�./doc�ļ��µ������ĵ�
    echo -- split \n\t ���readme�ĵ�
    echo -- compile \n\t ����Java�ļ�
    echo -- startJava \n\t ����Java����
    cd ..
)

:End
@rem ---------------------------------------------------------------------
@rem ����
@rem ---------------------------------------------------------------------
goto :EOF