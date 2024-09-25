import os
import sys
from PIL import Image,ImageDraw,ImageFont
z=sys.argv
print("Hellooooo")
imageFilePath=z[1];
dataFilePath=z[2];
#imageFilePath="C:\\tomcat9\\webapps\\report\\WEB-INF\\images\\template.jpg";
#dataFilePath="C:\\tomcat9\\webapps\\report\\WEB-INF\\data\\data.txt";
print(imageFilePath)
print(dataFilePath)
file=open(dataFilePath,'r')
data=[]
values={"name":"Prayukti Jain","fatherName":"Mr. Nitin Jain","motherName":"Mrs. Mona Jain","email":"jljain2011@gmail.com","contactNumber":"7869403331","dob":"02/12/1999"}
for line in file:
	if line!='\n' and line!='' and len(line)>0:
		n=[]
		for i in line.split("|"):
			a,b=i.split(":")
			n.append(b.rstrip())
		data.append(n)
		print(n)		
print(data)
img=Image.open(imageFilePath)
print(imageFilePath)
d=ImageDraw.Draw(img)
print("About..")
font=ImageFont.truetype(r'C:\\Users\\HP\\Desktop\\arial.ttf',20)
print("To")
for i in data:
	print("LOOP")
	print(values[i[2]])
	d.text((int(i[0]),int(i[1])),values[i[2]],fill="black",font=font)
	print("Loop")
print("Be")
img.save("C:\\tomcat9\\webapps\\report\\WEB-INF\\images\\result.jpg")
print("Done")