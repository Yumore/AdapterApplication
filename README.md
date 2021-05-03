# AdapterApplication
Adapter for RecyclerView

### idea 集成 cmder
"cmd.exe" /k ""%CMDER_ROOT%\vendor\init.bat""

### Android 10/11 AsyncTask 过时

### Android Studio 4.0创建类出现两次弹窗
File ---> Setting ---> Editor ---> File and Code Templates下选中Class
   
    #if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
    
    #if (${IMPORT_BLOCK} != "")${IMPORT_BLOCK}
    #end
    #parse("File Header.java")
    #if (${VISIBILITY} == "PUBLIC")public #end #if (${ABSTRACT} == "TRUE")abstract #end #if (${FINAL} == "TRUE")final #end class ${NAME} #if (${SUPERCLASS} != "")extends ${SUPERCLASS} #end #if (${INTERFACES} != "")implements ${INTERFACES} #end {
    }

    修改为
    
    #if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
     
    #parse("File Header.java")
    public  class ${NAME} {
    }