package com.thinking.machines.tmws.pojo;
import java.io.*;
import java.util.zip.*;
import java.util.*;
public class ZipUtility
{
static public void zipDirectory(File directory,ZipOutputStream zos,String prefix)
{
try
{
List<File> directories=new LinkedList<>();
File [] files=directory.listFiles();
byte[] bytes;
FileInputStream fis;
for(File file:files)
{
if(file.isDirectory())
{
directories.add(file);
}
else
{
bytes=new byte[1024];
fis=new FileInputStream(file);
zos.putNextEntry(new ZipEntry(prefix+file.getName()));
int count;
while(true)
{
count=fis.read(bytes);
if(count==-1) break;
zos.write(bytes,0,count);
}
fis.close();
zos.closeEntry();
}
}
for(File subDirectory:directories)
{
zipDirectory(subDirectory,zos,prefix+"/"+subDirectory.getName()+"/");
}
}catch(Exception exception)
{
exception.printStackTrace();
}
}
}
/*class psp
{
public static void main(String gg[])
{
try
{
String folderPath=gg[0];
String targetZipPath=gg[1];
File zipFile=new File(targetZipPath);
if(zipFile.exists()) zipFile.delete();
FileOutputStream fos=new FileOutputStream(zipFile);
ZipOutputStream zipOutputStream=new ZipOutputStream(fos);
ZipUtility.zipDirectory(new File(folderPath),zipOutputStream,"/");
zipOutputStream.close();
}catch(Exception e)
{
e.printStackTrace();
}
}
}*/