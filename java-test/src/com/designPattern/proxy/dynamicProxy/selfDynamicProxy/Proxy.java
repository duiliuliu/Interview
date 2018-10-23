package com.designPattern.proxy.dynamicProxy.selfDynamicProxy;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Proxy
 * 
 * Realizing the key part of dynamic proxy, we dynamically generate our concrete
 * proxy class through Proxy.
 * http://wiki.jikexueyuan.com/project/ssh-noob-learning/dynamic-proxy.html
 */
public class Proxy {
    public static Object newProxyInstance(Class infce, InvocationHandler handler) throws Exception {
        String methodStr = "";
        String rt = "\n\t";

        // Use reflection to get all the infce methods and reassemble them.
        Method[] methods = infce.getMethods();
        for (Method m : methods) {
            methodStr += "    @Override" + rt
                + "    public  " + m.getReturnType() + " " + m.getName() + "() {" + rt
                + "        try {" + rt 
                + "            Method md = " + infce.getName() + ".class.getMethod(\"" + m.getName() + "\");" + rt 
                + "            h.invoke(this, md);" + rt
                + "        }catch(Exception e) {e.printStackTrace();}" + rt 
                + "    }" + rt;
        }

        // Generating Java source files
        String srcCode =   
            "package com.designPattern.proxy.dynamicProxy.selfDynamicProxy;" +  rt +  
            "import java.lang.reflect.Method;" + rt +  
            "public class $Proxy1 implements " + infce.getName() + "{" + rt +  
            "    public $Proxy1(InvocationHandler h) {" + rt +  
            "        this.h = h;" + rt +  
            "    }" + rt +            
            "    com.tgb.proxy.InvocationHandler h;" + rt +                           
            methodStr + rt +  
            "}";  

        String fileName =   
            "./$Proxy1.java"; 

        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(srcCode);
        fileWriter.flush();
        fileWriter.close();

        // Compiling Java files into class files
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable compilationUnits = fileManager.getJavaFileObjects(fileName);
        CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        task.call();
        fileManager.close();

        // loading into memory and instantiation

        return null;
    }
}