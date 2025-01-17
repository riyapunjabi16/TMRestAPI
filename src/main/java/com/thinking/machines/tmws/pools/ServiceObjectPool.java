package com.thinking.machines.tmws.pools;
import com.thinking.machines.tmws.pojo.*;
import java.util.*;
import com.thinking.machines.tmws.interfaces.*;
public class ServiceObjectPool
{
private static Map<Class,List<ServiceController>> pool;
private ServiceObjectPool(){}
private static int poolSize;
static
{
pool=new HashMap<>();
poolSize=100;
}
public static ServiceController get(Class serviceClass) throws Throwable
{
synchronized(pool)
{
List<ServiceController> serviceControllers=pool.get(serviceClass);
if(serviceControllers!=null && serviceControllers.size()>0)
{
ServiceController serviceController=serviceControllers.remove(0);
serviceController.init();
return serviceController;
}
if(serviceControllers==null)
{
serviceControllers=new LinkedList<>();
pool.put(serviceClass,serviceControllers);
}
ServiceController serviceController=(ServiceController)serviceClass.newInstance();
serviceController.init();
return serviceController;
}
}
public static void submit(ServiceController serviceController)
{
synchronized(pool)
{
Class serviceClass=serviceController.getClass();
List<ServiceController> serviceControllers=pool.get(serviceClass);
if(serviceControllers.size()<poolSize) serviceControllers.add(serviceController);
}
}
}
