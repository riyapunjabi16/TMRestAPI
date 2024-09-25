package tm;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
public class Report extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
doPost(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
System.out.println("Servlet about to start..");
String s=request.getParameter("data");
System.out.println(s);
String filePath="C:\\tomcat9\\webapps\\report\\WEB-INF\\images\\template.jpg";
System.out.println(filePath);
String dataFilePath=parseData(s);
System.out.println(dataFilePath);

ProcessBuilder pb = new ProcessBuilder("python","c:/tomcat9/webapps/report/WEB-INF/python/process.py",filePath,dataFilePath);
Process p = pb.start();
BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
String line;
System.out.println("HELLLLLLLL");
System.out.println(in.readLine());
while ((line =in.readLine()) != null) 
{
System.out.println(line);
}

filePath = "C:\\tomcat9\\webapps\\report\\WEB-INF\\images\\result.jpg";
File downloadFile = new File(filePath);
FileInputStream inStream = new FileInputStream(downloadFile);
ServletContext context = getServletContext();
String mimeType=context.getMimeType(filePath);
if (mimeType == null) 
{        
mimeType = "application/octet-stream";
}
System.out.println("MIME type: " + mimeType);
response.setContentType(mimeType);
response.setContentLength((int) downloadFile.length());
String headerKey = "Content-Disposition";
String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
response.setHeader(headerKey, headerValue);
OutputStream outStream = response.getOutputStream();
byte[] buffer = new byte[4096];
int bytesRead = -1;
while ((bytesRead = inStream.read(buffer)) != -1) outStream.write(buffer, 0, bytesRead);
inStream.close();
outStream.close();     

System.out.println("Resposne Sent");
}catch(Exception e)
{
e.printStackTrace();
}
}
private String parseData(String s)
{
String filePath="C:\\tomcat9\\webapps\\report\\WEB-INF\\data\\data.txt";
try
{
String tmp;
tmp=s;
File file=new File(filePath);
if(file.exists()) file.delete();
file.createNewFile();
RandomAccessFile raf=new RandomAccessFile(file,"rw");
raf.seek(0);
while(s!="" && s.length()>0)
{
tmp=s.substring(s.indexOf("{") + 1);
tmp = tmp.substring(0, s.indexOf("}")-1);
s=s.substring(s.indexOf("}")+1);
raf.writeBytes(tmp);
raf.writeBytes("\n");
}
raf.close();
}catch(Exception e) {e.printStackTrace();}
return filePath;
}
}