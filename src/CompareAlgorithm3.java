import main.java.com.algorithm.*;
import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.Vehicle;
import main.java.com.result.Result;
import main.java.com.utils.Location;
import main.java.com.utils.Print;
import main.java.com.utils.RandomNumberUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 算法比较
 * 1. 不同datasize
 * 2. 不同ddl区间
 * 3. 不同task number
 * 4. 不同edge number
 */
public class CompareAlgorithm3
{
    static DecimalFormat df = new DecimalFormat("#.00");

    public static void main(String[] args) throws Exception
    {
//        System.out.println(Double.valueOf(df.format(12.3333666)));
//        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        compare();
    }
    private static void compare() throws Exception
    {
        // 记录结果
        List<Result> results = new ArrayList<Result>();

        Result result;
        int timeSlots = 10;
        int[] taskNums = {60,70,80,90,100};
        //int[] taskNums = {50,60,70,80,100,110,120,130,140,150}; // 忘记90了
//        int[] taskNums = {10,20,30,40,50,60,70,80,90,100};
//        int[] taskNums = {20,40,60,80,100,120,140,160,180,200};
//        int[] taskNums = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};

        double totalCompletionTime = 0;
        HashMap<String, Integer> map = new HashMap<>();
//        map.put("OTSS", 2);
//        map.put("OD",1);
//        map.put("RA",1);
        int OTSS = 4;
        int OD = 3;
        int RA = 3;
        map.put("OTSS", OTSS);
        map.put("OD",OD);
        map.put("RA",RA);
        for (int edgeNum : Constant.EDGE_NUMBER)
        {
            for (int vmNum : Constant.VM_NUMBER)
            {
                ArrayList<Edge> edgeList = initEdgeList(edgeNum, vmNum); // 初始化服务器数
                for(int ddlIndex = 1; ddlIndex < 2; ddlIndex++) //Constant.TASK_DEADLINE_RANGE.length
                {
                    for(int sizeIndex = 1; sizeIndex < 2; sizeIndex++) //Constant.TASK_DATA_SIZE_RANGE.length
                    {
                        for (int time = 0; time < taskNums.length; time++)
                        {
                            List<List<Result>> results10 = new ArrayList<>(); // 记录随机10次的结果
                            for(int i = 0; i < 5; i++)
                            {
                                List<Result> tempResults = new ArrayList<Result>();
                                int taskNum = taskNums[time];
                                ArrayList<Vehicle> vehicleList = initVehicleList(taskNum);
                                //ArrayList<Task> taskList = initTaskList(taskNum, vehicleList); // 初始化任务列表
                                ArrayList<Task> taskList = initTaskList(ddlIndex, sizeIndex, taskNum, vehicleList); // 初始化任务列表

                                // 车辆位置关联所处RSU
                                associateVehicleAndRSU(vehicleList, edgeList);
                                for(int alg = 1; alg < 5; alg++)
                                {
                                    Scheduler scheduler = null;
                                    double startTime = System.currentTimeMillis();
                                    switch (alg)
                                    {
                                        case 1:
                                            scheduler = new MAVTO();
                                            scheduler.runSchedule(edgeList, vehicleList, map);
                                            break;
                                        case 2:
                                            scheduler = new MONSA();
                                            scheduler.runSchedule(edgeList, vehicleList);
                                            break;
                                        case 3:
                                            scheduler = new TAVF();
                                            scheduler.runSchedule(edgeList, vehicleList);
                                            break;
                                        case 4:
                                            scheduler = new TFPVTO();
                                            scheduler.runSchedule(edgeList, vehicleList);
                                            break;
                                    }
                                    double endTime = System.currentTimeMillis();

                                    result = new Result(time, edgeNum, vmNum, taskNum, alg, OTSS, OD,RA,
                                            scheduler.getMeanCompletionTime(), scheduler.getTotalCompletionTime(),
                                            scheduler.getSuccessfulTaskNum(),scheduler.getSuccessfulTaskRatio(),endTime - startTime,ddlIndex,sizeIndex);

//                                    result = new Result(time, edgeNum, vmNum, taskNum, alg,2, 1,1,
//                                            scheduler.getMeanCompletionTime(), scheduler.getTotalCompletionTime(),
//                                            scheduler.getSuccessfulTaskNum(),scheduler.getSuccessfulTaskRatio(),endTime - startTime);

                                    tempResults.add(result);
                                    reset(edgeList, taskList);
                                }
                                results10.add(tempResults);
                            }
                            List<Result> tempResult = computeResult(results10);
                            results.addAll(getRPD(tempResult));
                            //tempResults.clear();
                            resetEdge(edgeList);
                        }
                    }
                }
            }
        }
        Print print = new Print();
//        print.exportToExcel_TS(results, "算法比较");
        print.exportToExcel_Node(results);
        results.clear();
        System.out.println("finish");
    }
    /**
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
    /**
     * 重置服务器资源，每一次time，tasknum换
     */
    public static void resetEdge(List<Edge> edgeList)
    {
        for(Edge e:edgeList)
        {
            e.reset();
            e.setVehicleList(null);
        }
    }
    /**
     * 初始化服务器参数
     */
    private static ArrayList<Edge> initEdgeList(int edgeNum, int vmNum)
    {
        ArrayList<Edge> edgeList = new ArrayList<>();
        for(int i=0;i<edgeNum;i++)
        {
            Edge edge = new Edge(i, vmNum);
            edge.initEdge(); // 初始化通信范围，处理能力

            edgeList.add(edge);
        }
        return edgeList;
    }

    /**
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

//            int index = RandomNumberUtils.getRandomNumber(0,Constant.VEHICLE_SPEED.length-1);
            double speed = RandomNumberUtils.getRandomNumber(Constant.VEHICLE_SPEED_MIN, Constant.VEHICLE_SPEED_MAX);
            v.setSpeed(speed);

            double X = RandomNumberUtils.getDoubleRandomNumber(Constant.VEHICLE_LOCATION_X_MIN,Constant.VEHICLE_LOCATION_X_MAX);
            Location location = new Location(X,0);
            v.setLocation(location);

            vehicleList.add(v);
        }
        return vehicleList;
    }
    /**
     * 初始化任务
     */
    private static ArrayList<Task> initTaskList(int ddlIndex, int sizeIndex, int taskNum, ArrayList<Vehicle> vehicleList)
    {
        ArrayList<Task> taskList = new ArrayList<>();
        for(int i=0;i<taskNum;i++)
        {
            Task task = new Task(i,i);
            //task.initTask(0); // ratio, datasize
            task.initTask(ddlIndex, sizeIndex);
            task.setVehicle(vehicleList.get(i));

            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(task);
            vehicleList.get(i).setTaskList(tasks);

            taskList.add(task);
        }
        return taskList;
    }

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
    /**
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
                if(vehiclex<=(edgex+edge.getCommunicationRange()) && vehiclex>=(edgex-edge.getCommunicationRange()))
                {
                    ArrayList<Vehicle> vehicles = (edge.getVehicleList() == null ? new ArrayList<>() : edge.getVehicleList());
                    vehicles.add(vehicle);
                    edge.setVehicleList(vehicles);

                    vehicle.getTaskList().get(0).setCoveredEdge(edge);
                }
            }
        }
    }

    /**
     * 统计10次结果平均值
     * @param results10
     */
    private static List<Result> computeResult(List<List<Result>> results10)
    {
        int times = results10.size();
        int algs = results10.get(0).size();
        List<Result> results = new ArrayList<>();
        for(int alg = 0; alg < algs; alg++)
        {
            int sumTaskNum = 0;
            double avgCompletionTime = 0;
            double totalCompletionTime = 0;
            int sumSuccessfulTaskNum = 0;
            double successTaskRatio = 0;
            double runtime = 0;
            int iteration = 0;
            int vmNum = 0;
            int algorithm = 0;
            int edgeNum = 0;
            int ddlIndex = 0;
            int sizeIndex = 0;
            for(int i = 0; i < times; i++)
            {
                Result r = results10.get(i).get(alg);
                sumTaskNum += r.taskNum;
                avgCompletionTime += r.meanCompletionTime;
                totalCompletionTime += r.totalCompletionTime;
                sumSuccessfulTaskNum += r.successTaskNum;
                successTaskRatio += r.successTaskRatio;
                runtime += r.runtime;
                iteration = r.iteration;
                vmNum = r.vmNum;
                algorithm = r.alg;
                edgeNum = r.edgeNum;
                ddlIndex = r.ddlIndex;
                sizeIndex = r.sizeIndex;
            }
            sumTaskNum /= times;
            avgCompletionTime /= times;
            totalCompletionTime /= times;
            sumSuccessfulTaskNum /= times;
            runtime /= times;
            successTaskRatio /= times;

            Result result = new Result(iteration, edgeNum, vmNum, sumTaskNum, algorithm,2, 1,1,
                    avgCompletionTime, totalCompletionTime, sumSuccessfulTaskNum, successTaskRatio,runtime,ddlIndex,sizeIndex);
            results.add(result);
        }
        return results;
    }

    /**
     * 计算此次任务和资源状态下，调度结果的RPD
     * param tempResults
     * @return
     */
    private static List<Result> getRPD(List<Result> results) {
        // double minMeanCompletionTime  = minMeanCompletionTime(results);
        double maxRatio  = maxSuccessRatio(results);

        for (int i = 0; i < results.size() ;i ++) {
            Result result = results.get(i);
            if(maxRatio != 0)
            {
                result.RPD = (maxRatio - result.successTaskRatio) / maxRatio;
            }
            else
            {
                System.out.println("max ratio "+maxRatio);
                result.RPD = 0;
            }
            try {
//                result.RPD = Double.valueOf(df.format(result.RPD*100));
                result.RPD = Double.valueOf(result.RPD*100);
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
     * @param results
     * @return
     */
    private static double maxSuccessRatio(List<Result> results) {
        double maxRatio = 0;
        for (Result result : results) {
            if (maxRatio < result.successTaskRatio) {
                maxRatio = result.successTaskRatio;
            }
        }
        return maxRatio;
    }
}
