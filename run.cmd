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
) else if %command% == -h (
    echo -- merge \n\t 合并./doc文件下的所有文档
    echo -- split \n\t 拆分readme文档
    echo -- compile \n\t 编译Java文件
    echo -- startJava \n\t 运行Java程序
    cd ..
)

:End
@rem ---------------------------------------------------------------------
@rem 结束
@rem ---------------------------------------------------------------------
goto :EOF