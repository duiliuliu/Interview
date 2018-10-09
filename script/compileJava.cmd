@rem =========================================================================
@rem 编译java程序，如果编译成功则运行
@rem author: pengr 
@rem -------------------------------------------------------------------------
@echo off


@rem -------------------------------------------------------------------------
@rem 初始化变量
@rem -------------------------------------------------------------------------
set currentPath=%cd%
if exist "%cd%\test" ( set projectPath=%cd% ) else (
    cd %currentPath%\..
)
set projectPath=%cd%
set javaPath=%projectPath%\test


if "%~1" EQU "" (
   echo Please input filename to be compiled.
   goto End
)
set javaFile=%~1


for %%a in (%javaPath%) do (
    echo %%a
)


:End
@rem ---------------------------------------------------------------------
@rem 结束
@rem ---------------------------------------------------------------------
cd %currentPath%
goto :EOF
 

