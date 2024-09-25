package com.thinking.machines.tmws;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.io.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.google.gson.*;
import com.thinking.machines.tmws.pojo.*;
import com.thinking.machines.tmws.annotations.*;
import java.util.zip.*;
import java.util.*;
public class TMRESTAPIHandler extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
process(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
process(request,response);
}
// for another methods
public void process(HttpServletRequest request,HttpServletResponse response)
{
System.out.println("Process starts........................");
try
{
String path=request.getPathInfo();
System.out.println("Process starts........................"+path);
ServletContext servletContext=getServletContext();
System.out.println("Process starts......................1..");
Model dataModel=(Model)servletContext.getAttribute(Model.id);
System.out.println("Process starts....................2....");
ServiceModule serviceModule=dataModel.getServiceModule(path);
System.out.println("Process starts....................3....");
if(serviceModule==null)
{
System.out.println("Service Module==null me gyaaaaaaaaaaaaaaaaaaaaaaa");
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
Service service=serviceModule.getService(path);
System.out.println("Path is : "+service.getPath());
String requestMethodType=request.getMethod();
if(requestMethodType.equalsIgnoreCase("GET"))
{
if(!service.allowGet())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}else if(requestMethodType.equalsIgnoreCase("POST"))
{
if(!service.allowPost())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}
Object serviceObject=serviceModule.getServiceObject(servletContext,request,service);
if(serviceObject==null)
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
if(serviceModule.isSecured() || service.isSecured())
{
// some code
}
System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
Object arguments[]=service.getArguments(servletContext,request);
for(Object o:arguments) System.out.println("@@@@@@@@@@@@@@@@@@@@@@"+o);
try
{
Method method=service.getMethod();
Object result=method.invoke(serviceObject,arguments);
System.out.println("Result : "+result);
/*if(service.isForwarding()) // forwarding logic starts here
{
String forwardTo=service.getForwardTo();
if(forwardTo==null)
{
if(service.getReturnType().equals(void.class))
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
if(result==null)
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
forwardTo=result.toString();
if(forwardTo.trim().length()==0)
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}
if(dataModel.containsPath(forwardTo)) // can we change this to pick info from service object and decide
{
String prefix=request.getServletPath();
RequestDispatcher requestDispatcher=request.getRequestDispatcher(prefix+forwardTo);
requestDispatcher.forward(request,response);
return;
}
else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
return;
}
}
*/
// forwarding logic ends here
// if not forwarding part starts here
PrintWriter pw=response.getWriter();
if(service.getResponseType()==ResponseType.JSON)
{
response.setContentType("application/json");
response.getWriter().print(new Gson().toJson(result));
return;
}
if(service.getResponseType()==ResponseType.XML)
{
response.setContentType("application/xml");
JAXBContext jaxbContext = JAXBContext.newInstance(result.getClass());
Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
StringWriter sw = new StringWriter();
jaxbMarshaller.marshal(result, sw);
System.out.println(sw.toString()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
response.getWriter().print(sw.toString());
return;
}
if(service.getResponseType()==ResponseType.FILE)
{
if(result==null)
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
String str=null;
StringBuffer sb=new StringBuffer();
if(service.getReturnType()==ResponseFile.class)
{
ResponseFile responseFile=(ResponseFile)result;
response.setContentType(responseFile.getMimeType());
System.out.println("Content Type is : "+responseFile.getMimeType());
System.out.println("Response ka Content Type is : "+response.getContentType());
if(!responseFile.getShouldSave())
{
RandomAccessFile raf=new RandomAccessFile(responseFile.getFile(),"r");
raf.seek(0);
while(raf.getFilePointer()<raf.length())
{
//pw.println(raf.readLine());
str=raf.readLine();
sb.append(str+"\r\n");
}
raf.close();
System.out.println("File content issssssssss: "+sb.toString());
response.getWriter().print(sb.toString());
System.out.println("PW.PRINTLN hogyaaaaaaaaaaaaaaa");
}
else
{
File file1=new File(responseFile.getName());
File file2=responseFile.getFile();
//response.setContentLength(int(file2.length()));
RandomAccessFile raf1=new RandomAccessFile(file1,"rw");
RandomAccessFile raf2=new RandomAccessFile(file2,"r");
raf1.seek(0);
raf2.seek(0);
while(raf2.getFilePointer()<raf2.length())
{
str=raf2.readLine();
raf1.writeBytes(str+"\n");
}
raf1.close();
raf2.close();
response.setHeader("Content-Disposition","attachment; filename=\"" + file1.getName() + "\"");   
FileInputStream fileInputStream = new FileInputStream(file1.getName());  
int i;   
while ((i=fileInputStream.read()) != -1) response.getWriter().write(i);  
fileInputStream.close();   
response.getWriter().close();
}//else ends
}//response File ends
if(service.getReturnType()==File.class)
{
ByteArrayOutputStream baos=new ByteArrayOutputStream();
ZipOutputStream zos=new ZipOutputStream(baos);
File resultFile=(File)result;
if(resultFile.isDirectory()) 
{
zipDirectory(resultFile,zos,"/");
response.setContentType("application/zip");
try
{
response.setHeader("Content-Disposition","attachment; filename=\"" + resultFile.getName()+".zip" + "\"");   
response.getWriter().print(zos);
}catch(IOException ioException)
{
ioException.printStackTrace();
System.exit(0);
}
}
else
{
response.setContentType("text/html");
RandomAccessFile raf=new RandomAccessFile((File)result,"r");
raf.seek(0);
while(raf.getFilePointer()<raf.length())
{
//pw.println(raf.readLine());
str=raf.readLine();
sb.append(str+"\r\n");
}
raf.close();
System.out.println("File content issssssssss: "+sb.toString());
response.getWriter().print(sb.toString());
System.out.println("PW.PRINTLN hogyaaaaaaaaaaaaaaa");
}
}
/*
return type -> ResponseFile (No Issues)
return type -> File or File[]
File : Determine mime type and set content type and
write
File [] : zip all and send the zip file (set content type as zip)

what if returned value is null in case of (ResponseFile, File or File[])
in that case SC_404

return type is not either of the above
convert it to JSON/XML/CSV, set file name as something.json/xml/csv
set it for download

what i return type is void (we should not consider it as a service
or ignore the response type, we will consider the response type as NONE)

@Res
return_type method

*/
return;
}
if(service.getResponseType()==ResponseType.HTML)
{
/*
if instance of result is of String/primitive type, just write it as text/html
if result is null, then write empty string as text/html
if result is of file [] type (read (all) and write (all)), if does not exist _SC_INTERNAL_ERROR

Note : Consider the discussion we went through during the classroom session regarding the following scenarios

a file xyz.data
sam <cool> tim

a file xyz.html or xyz.htm
sam <cool> tim
Now what if the return type represents a Class (other than wrapper/String)

toString()

*/
response.setContentType("application/html");
response.getWriter().print(result);
return;
}
if(service.getResponseType()==ResponseType.NONE)
{
/*
what if return type is not void
we will ignore the returned material
send back 200 without any content, is it possible ????
if it is possible, then why not invoke the service on separate 
thread
*/
System.out.println("NONEEEEEEEEEEEEE");
response.setStatus(HttpServletResponse.SC_OK);
return;
}
// if not forwarding part ends here
}catch(Throwable t)
{
t.printStackTrace();
/*Throwable t=invocationTargetException.getCause();
if(service.hasExceptionHandler())
{
if(service.isExceptionHandlerAService())
{
String prefix=request.getServletPath(); 
request.getRequestDispatcher(prefix+service.getExceptionHandler()).forward(request,response);
}
else
{
request.getRequestDispatcher(service.getExceptionHandler()).forward(request,response);
}
return;
}
else
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
}
}catch(IllegalAccessException illegalAccessException)
{
System.out.println("IllegalAccessException shouldn't have happened : "+illegalAccessException);
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
return;
*/
}//processUec ends
}catch(Throwable t)
{
t.printStackTrace();
}
System.out.println("Process ends........................");
}
private void zipDirectory(File directory,ZipOutputStream zos,String prefix)
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
