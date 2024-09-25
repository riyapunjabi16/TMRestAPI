package tm;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
public class Reported extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
System.out.println("Servlet about to start..");
String filePath="C:/tomcat9/webapps/report/WEB-INF/images/result.jpg";
System.out.println(filePath);
ServletOutputStream outStream;
outStream = response.getOutputStream();
FileInputStream fin = new FileInputStream(filePath);
BufferedInputStream bin = new BufferedInputStream(fin);
BufferedOutputStream bout = new BufferedOutputStream(outStream);
int ch =0; ;
while((ch=bin.read())!=-1) bout.write(ch);
bin.close();
fin.close();
bout.close();
outStream.close();
response.setContentType("image/jpeg");
}catch(Exception e)
{
e.printStackTrace();
}
System.out.println("Response Sent..");
}
}