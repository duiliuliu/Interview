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
set srcPath=%projectPath%\test\src
set binPath=%projectPath%\test\bin


if "%~1" EQU "" (
   echo Please input filename to be compiled.
   goto End
)
set javaFile=%~1

@rem -------------------------------------------------------------------------
@rem 进行运行
@rem -------------------------------------------------------------------------
for /R %srcPath% %%i in (.) do (
    if exist "%%i\%javaFile%.java" ( 
        javac -d %binPath% %%i\%javaFile%.java 
    )
)


:End
@rem ---------------------------------------------------------------------
@rem 结束
@rem ---------------------------------------------------------------------
cd %currentPath%
goto :EOF
 

