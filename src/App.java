import main.java.com.algorithm.*;
import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.Vehicle;
import main.java.com.result.Result;
import main.java.com.utils.Location;
import main.java.com.utils.RandomNumberUtils;

import java.text.DecimalFormat;
import java.util.*;

public class App {
    static DecimalFormat df = new DecimalFormat("#.00");
    public static void main(String[] args) throws Exception {
        compare();
    }
    private static void compare() throws Exception{
         // 记录结果
         List<Result> results = new ArrayList<Result>();
         List<Result> tempResults = new ArrayList<Result>();
         Result result;
   
        int edgeNum = Constant.EDGEHOSTS_NUM; // 服务器数
        ArrayList<Edge> edgeList = initEdgeList(edgeNum); // 初始化服务器数

        int timeSlots = 10;
        double totalCompletionTime = 0;
        int[] taskNums = {10,20,30,40,50,60,70,80,90,100};
        for(int time = 0; time < timeSlots; time++)
        {
            // ArrayList<Edge> edgeList = initEdgeList(edgeNum); // 初始化服务器数
            // int taskNum = RandomNumberUtils.getRandomNumber(Constant.TASK_NUM_MIN, Constant.TASK_NUM_MAX); // 要产生的任务数
            int taskNum = taskNums[time];
            ArrayList<Vehicle> vehicleList = initVehicleList(taskNum);
            ArrayList<Task> taskList = initTaskList(taskNum, vehicleList); // 初始化任务列表

            // 车辆位置关联所处RSU
            associateVehicleAndRSU(vehicleList, edgeList);
            
            // taskMatchEdge(int taskNum, int[][] edge_match_task, List<Edge> edgeList)
            for(int Algorithm = 1; Algorithm <5; Algorithm++)
            {
                double startTime = System.currentTimeMillis();
                Scheduler scheduler = null;
                switch(Algorithm)
                {
                    case 1:
                        // System.out.println("Algorithm1");
//                        scheduler = new Algorithm1();
//                        scheduler.runSchedule(edgeList,vehicleList);
                        break;
                    case 2:
                        // System.out.println("Algorithm2");
//                        scheduler = new Algorithm2();
//                        scheduler.runSchedule(edgeList,vehicleList);
                        break;
                    case 3:
                        // System.out.println("Algorithm3");
//                        scheduler = new Algorithm3();
//                        scheduler.runSchedule(edgeList,vehicleList);
                        break;
                    case 4:
                        // System.out.println("Algorithm4");
//                        scheduler = new Algorithm4();
//                        scheduler.runSchedule(edgeList,vehicleList);
                        break;
                }
                
                double endTime = System.currentTimeMillis();
                // System.out.println("程序运行时间: "+(endTime - startTime));
                result = new Result(taskNum, 1, Algorithm, scheduler.getTotalMakeSpan(), 
                                    scheduler.getMeanCompletionTime(), endTime-startTime);
                
                // System.out.println(result.toString());
                tempResults.add(result);
                reset(edgeList,taskList);
            }
            results.addAll(getRPD(tempResults));
            tempResults.clear();
            resetEdge(edgeList);
        }
        for(Result result2: results)
        {
            System.out.println(result2.toString());
        }
//        Print print = new Print();
//        print.exportToExcel_Node(results);
        results.clear();
        System.out.println("finish");
    }

    /*
     * 算法比较
     */
    private static Scheduler compareAlgorithm(int Algorithm, List<Vehicle> vehicleList,List<Task> taskList, List<Edge> edgeList)
    {
        Scheduler scheduler = null;
        switch(Algorithm)
        {
            case 1:
//                scheduler = new Algorithm1();
//                Map<String, Integer> map = new HashMap<>();
//                map.put("ME",1);
//                map.put("ST",1);
//                map.put("RTS",1);
//                scheduler.runSchedule(edgeList, vehicleList);

            default:
//                scheduler = new Algorithm1();
//                Map<String, Integer> map1 = new HashMap<>();
//                map1.put("ME",1);
//                map1.put("ST",1);
//                map1.put("RTS",1);
//                scheduler.runSchedule(edgeList, vehicleList);
        }
        return scheduler;
    }
    /*
     * 重置服务器资源
     */
    public static void reset(List<Edge> edgeList,List<Task> taskList)
    {
        for(Edge e:edgeList)
        {
            e.reset();
        }
        for(Task t:taskList)
        {
            t.reset();
        }
    }
    /*
     * 重置车辆资源
     */
    public static void resetEdge(List<Edge> edgeList)
    {
        for(Edge e:edgeList)
        {
            e.reset();
        }
    }
    /*
     * 初始化服务器参数
     */
    private static ArrayList<Edge> initEdgeList(int edgeNum)
    {
        ArrayList<Edge> edgeList = new ArrayList<>();
        for(int i=0;i<edgeNum;i++)
        {
            Edge edge = new Edge(i);
            edge.initEdge(); // 初始化通信范围，处理能力
            
            edgeList.add(edge);
        }

        return edgeList;
    }
    /*
     * 初始化车载终端参数
     */
    private static ArrayList<Vehicle> initVehicleList(int vehicleNum)
    {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();

        for(int i=0;i<vehicleNum;i++)
        {
            Random random = new Random();

            Constant.VEHICLE_DIRECTION direction = random.nextInt(2) == 0 ? Constant.VEHICLE_DIRECTION.SOUTH: Constant.VEHICLE_DIRECTION.NORTH;
            double processSpeed = RandomNumberUtils.getDoubleRandomNumber(Constant.VEHICLE_PROCESS_SPEED_MIN,Constant.VEHICLE_PROCESS_SPEED_MAX); // 需要改动
            Vehicle v = new Vehicle(i, processSpeed, direction);
            double X = RandomNumberUtils.getDoubleRandomNumber(Constant.VEHICLE_LOCATION_X_MIN,Constant.VEHICLE_LOCATION_X_MAX);
            Location location = new Location(X,0);
            v.setLocation(location);
            vehicleList.add(v);
        }
        return vehicleList;
    }
    /*
     * 初始化任务
     */
    private static ArrayList<Task> initTaskList(int taskNum, ArrayList<Vehicle> vehicleList) 
    {
        ArrayList<Task> taskList = new ArrayList<>();
        for(int i=0;i<taskNum;i++)
        {
            Task task = new Task(i,i);
            task.initTask(0); // ratio, datasize
            task.setVehicle(vehicleList.get(i));

            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(task);
            vehicleList.get(i).setTaskList(tasks);

            taskList.add(task);
        }
        return taskList;
    }
    /*
     * 根据位置计算RSU覆盖范围下的车辆
     * 将RSU和车辆进行关联
     */
    private static void associateVehicleAndRSU(List<Vehicle> vehicleList, List<Edge> edgeList)
    {
        for(Edge edge: edgeList)
        {
            double edgex = edge.getLocation().getXPos();
            double edgey = edge.getLocation().getYPos();
            for(Vehicle vehicle: vehicleList)
            {
                double vehiclex = vehicle.getLocation().getXPos();
                double vehicley = vehicle.getLocation().getYPos();
                // 处于该服务器通信范围内
                if(vehiclex<=(edgex+Constant.COMMUNICATION_RANGE) && vehiclex>=(edgex-Constant.COMMUNICATION_RANGE))
                {
                    ArrayList<Vehicle> vehicles = (edge.getVehicleList() == null ? new ArrayList<>() : edge.getVehicleList());

                    vehicles.add(vehicle);
                    edge.setVehicleList(vehicles);
                    vehicle.getTaskList().get(0).setCoveredEdge(edge);
                }
            }
        }
    }

    /*
	 * 计算此次任务和资源状态下，调度结果的RPD
	 * 
	 * @param tempResults
	 * @return
	 */
	private static List<Result> getRPD(List<Result> results) {
		double minMeanCompletionTime  = minMeanCompletionTime(results);
        
		for (int i = 0; i < results.size() ;i ++) {
            Result result = results.get(i);
			result.RPD = (result.meanCompletionTime - minMeanCompletionTime) / minMeanCompletionTime;
            
			try {
				result.RPD = Double.valueOf(df.format(result.RPD*100));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(result);
			}
            // System.out.println("RPD: "+result.RPD);
		}
        
		return results;
	}

	/**
	 * 获得本次调度中，不同结果中的最小任务平均完成时间
	 * 
	 * @param results
	 * @return
	 */
	private static double minMeanCompletionTime(List<Result> results) {
		double minMeanCompletionTime = Double.MAX_VALUE;
		for (Result result : results) {
			if (minMeanCompletionTime > result.meanCompletionTime && result.meanCompletionTime != 0) {
				minMeanCompletionTime = result.meanCompletionTime;
			}
		}
		return minMeanCompletionTime;
	}
}
