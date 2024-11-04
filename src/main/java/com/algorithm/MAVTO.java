package main.java.com.algorithm;

import main.java.com.algorithm.matchEdgePolicy.*;
import main.java.com.algorithm.offloadTaskSequencePolicy.*;
import main.java.com.algorithm.remoteTaskSchedule.EarliestFinishTimeSchedule;
import main.java.com.algorithm.remoteTaskSchedule.MaxMinSchedule;
import main.java.com.algorithm.remoteTaskSchedule.MinVarSchedule;
import main.java.com.algorithm.remoteTaskSchedule.RemoteTaskSchedule;
import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;
import main.java.com.resource.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MAVTO extends Scheduler{
    @Override
    public void runSchedule(List<Edge> edgeList, List<Vehicle> vehicleList, HashMap<String, Integer> map)
    {
        int OTSS = map.get("OTSS");
        int OD = map.get("OD");
        int RA = map.get("RA");
        // 产生服务节点候选组，返回要卸载的任务序列
         List<Task> taskList = generateCandidateEdgeGroup(edgeList, vehicleList);

        // 产生本地执行序列和卸载序列，并对卸载序列排序
         List<Task> offloadTaskSequence = generateOffloadSequence(OTSS, taskList);

        // 分配通信资源，上传任务，计算传输/驶入时间
        //bandwidthAllocation(taskList, Constant.TOTAL_BANDWIDTH);
        //uploadTask(taskList);
        //bandwidthAllocation(offloadTaskSequence, Constant.TOTAL_BANDWIDTH);
        // uploadTask(offloadTaskSequence);

        // 卸载决策
        //generateOffloadDecision(OD, taskList);
        //generateOffloadDecision(OD, offloadTaskSequence);

        // 资源分配
         //generateResourceAllocate(RA, edgeList);

        generateOffloadDecision(OD, RA,offloadTaskSequence, edgeList);

        // 计算结果，统计任务成功完成率
//         calResult(edgeList);
        calMeanCompletionTime(taskList,edgeList);
    }

    /**
     * 为每个任务产生候选节点组
     * @param edgeList
     * @param vehicleList
     * @return
     */
    public List<Task> generateCandidateEdgeGroup(List<Edge> edgeList, List<Vehicle> vehicleList)
    {
        // 车辆前进方向上的几个RSU都可以作为候选集
        List<Task> taskList = new ArrayList<>();
        for(Vehicle vehicle: vehicleList)
        {
            Task task = vehicle.getTaskList().get(0);
            // 计算位置
            double vehicle_x = vehicle.getLocation().getXPos();
            Constant.VEHICLE_DIRECTION direction = vehicle.getDirection();
            ArrayList<Edge> candidateEdgeList = new ArrayList<>();
            if(direction == Constant.VEHICLE_DIRECTION.SOUTH)
            {
                // 大于 vehicle_x，都是会作为经过的RSU
                for(Edge edge: edgeList)
                {
                    double edge_x = edge.getLocation().getXPos();
                    double edge_x_min = edge_x - edge.getCommunicationRange();
                    double edge_x_max = edge_x + edge.getCommunicationRange();
                    if(edge_x_min <= vehicle_x && vehicle_x < edge_x_max) // covered
                    {
                        candidateEdgeList.add(edge);
                    }
                    else if(edge_x_min >= vehicle_x)
                    {
                        candidateEdgeList.add(edge);
                    }
                }
            }
            else
            {
                // 小于 vehicle_x，都是会作为经过的RSU
                for(Edge edge: edgeList)
                {
                    double edge_x = edge.getLocation().getXPos();
                    double edge_x_min = edge_x - edge.getCommunicationRange();
                    double edge_x_max = edge_x + edge.getCommunicationRange();
                    if(edge_x_min < vehicle_x && vehicle_x <= edge_x_max) // covered
                    {
                        candidateEdgeList.add(edge);
                    }
                    else if(edge_x_min < vehicle_x)
                    {
                        candidateEdgeList.add(edge);
                    }
                }
            }
            if(candidateEdgeList.size() == 0) continue;
            task.setCandidateEdgeList(candidateEdgeList);
            taskList.add(task);
        }
        return taskList;
    }
//    public List<Task> generateCandidateEdgeGroup(List<Edge> edgeList, List<Vehicle> vehicleList)
//    {
//        List<Task> taskList = new ArrayList<>();
//        for(Vehicle vehicle: vehicleList)
//        {
//            Task task = vehicle.getTaskList().get(0);
//            // 计算位置
//            double vehicle_x = vehicle.getLocation().getXPos();
//            Constant.VEHICLE_DIRECTION direction = vehicle.getDirection();
//            double deadline = task.getDeadline();
//            double speed = vehicle.getSpeed();
//            double vehicle_x_next;
//            if(direction == Constant.VEHICLE_DIRECTION.SOUTH)
//            {
//                vehicle_x_next = vehicle_x + speed * deadline;
//            }
//            else
//            {
//                vehicle_x_next = vehicle_x - speed * deadline;
//            }
//            double x_min = Math.min(vehicle_x, vehicle_x_next);
//            double x_max = Math.max(vehicle_x, vehicle_x_next);
//            ArrayList<Edge> candidateEdgeList = new ArrayList<>();
//            for(Edge edge: edgeList)
//            {
//                double edge_x = edge.getLocation().getXPos();
//                double edge_x_min = edge_x - edge.getCommunicationRange();
//                double edge_x_max = edge_x + edge.getCommunicationRange();
//                if(x_min <= edge_x && edge_x <= x_max)
//                {
//                    candidateEdgeList.add(edge);
//                }
//                else if(edge_x_min <= x_min && x_min <edge_x_max)
//                {
//                    candidateEdgeList.add(edge);
//                }
//                else if(edge_x_min <= x_max && x_max < edge_x_max)
//                {
//                    candidateEdgeList.add(edge);
//                }
//            }
//            if(candidateEdgeList.size() == 0) continue;
//            task.setCandidateEdgeList(candidateEdgeList);
//            taskList.add(task);
//        }
//        return taskList;
//    }

    /**
     * 产生本地执行序列和卸载序列
     * 并对卸载序列排序
     * @return
     */
    public List<Task> generateOffloadSequence(int OTSS, List<Task> taskList)
    {
        OffloadTaskSequenceSort offloadTaskSequenceSort = null;
        switch (OTSS)
        {
            case 1: // 最少可选节点先选
                offloadTaskSequenceSort = new MinCandidatesFirstSequence();
                break;
            case 2: // 最少可用时间，驶出范围的时间
                offloadTaskSequenceSort = new MinAvailableTimeFirstSequence();
                break;
            case 3: // 最大本地执行时间优先
                offloadTaskSequenceSort = new MaxLocalTimeFirstSequence();
                break;
            case 4: // 最小数据量计算量比率优先
                offloadTaskSequenceSort = new MinCompDataRatioFirstSequence();
                break;
        }
        List<Task> offloadSequence = offloadTaskSequenceSort.sort(taskList);
        //return taskList;
        return offloadSequence;
    }

    /**
     * 任务卸载决策与资源分配循环交替
     * @param OD
     * @param RA
     * @param taskList
     */
    public void generateOffloadDecision(int OD, int RA, List<Task> taskList, List<Edge> edgeList)
    {
        MatchEdge matchEdge = null;
        switch (OD)
        {
            /**
             * MCTF和 NEF两个效果差不多，也就是说在各服务节点之间没有明显区别时，如计算能力、负载情况，两者效果差不多。
             */
            case 1:
                matchEdge = new MinEstimatedCompletionTimeFirstMatch();
                break;
            case 2:
                matchEdge = new MaxProcessCapacityFirstMatch();
                break;
            case 3: // 综合考虑了计算能力和请求数
                matchEdge = new LoadBalanceFirstMatch();
                break;
            case 4:
                matchEdge = new NearestEdgeFirstMatch();
                break;
        }

        RemoteTaskSchedule remoteTaskSchedule = null;
        switch (RA)
        {
            case 1:
                remoteTaskSchedule = new EarliestFinishTimeSchedule();
                break;
            case 2:
                remoteTaskSchedule = new MaxMinSchedule();
                break;
            case 3:
                remoteTaskSchedule = new MinVarSchedule();
                break;
        }


        for(Task task: taskList)
        {
            // OD
            matchEdge.match(task);

            uploadTask(task);

            // RA, just for EFTS
            if(RA == 1)
            {
                remoteTaskSchedule.exeRemote(task);
            }
        }
        // MAX-MIN和MIN-VAR都是存在排序的.
        if(RA != 1)
        {
            remoteTaskSchedule.exeRemote(edgeList);
        }
    }

    /**
     * 卸载决策，决定每个任务要卸载到的边缘节点
     * @param OD
     * @param taskList
     */
    public void generateOffloadDecision(int OD, List<Task> taskList)
    {
        MatchEdge matchEdge = null;
        switch (OD)
        {
            case 1:
                matchEdge = new MinEstimatedCompletionTimeFirstMatch();
                break;
            case 2:
                matchEdge = new MaxProcessCapacityFirstMatch();
                break;
            case 3:
                matchEdge = new LoadBalanceFirstMatch();
                break;
            case 4:
                matchEdge = new NearestEdgeFirstMatch();
                break;
        }
        matchEdge.match(taskList);
    }
    /**
     * 资源分配，taskSchedule
     */
    public void generateResourceAllocate(int RA, List<Edge> edgeList)
    {
        RemoteTaskSchedule remoteTaskSchedule = null;
        switch (RA)
        {
            case 1:
                remoteTaskSchedule = new EarliestFinishTimeSchedule();
                break;
            case 2:
                remoteTaskSchedule = new MaxMinSchedule();
                break;
            case 3:
                remoteTaskSchedule = new MinVarSchedule();
                break;
        }
        remoteTaskSchedule.exeRemote(edgeList);
    }

    /**
     * NO.6
     * 计算任务执行结果：执行时间、成功率
     * 计算包括本地执行
     * param List<Task> localExecutionSequence
     */
    private void calResult(List<Edge> edgeList)
    {
        // 卸载的
        // 计算每个VM的exeList
        // 取平均完成时间最大，因为是并行
        // 然后再与本地执行，取最大
        double maxAvgCompletionTime = 0;
        double totalCompletionTime = 0;
        int successfulTaskNumber = 0;
        int totalTaskNumber = 0;

        // 边缘
        for(Edge edge: edgeList)
        {
            for(VM vm: edge.getVMList())
            {
                List<Task> exeList = vm.getExeTaskList();
                double oneVMTotalCompletionTime = 0;
                if(exeList == null) continue;
                for(Task task: exeList)
                {
                    totalTaskNumber++;
                    oneVMTotalCompletionTime += task.getAFT();
                    if(task.getAFT() < task.getDeadline()&&task.getStatus() == Constant.TASK_STATUS.FINISHED)
                    {
                        successfulTaskNumber++;
                    }
                }
                totalCompletionTime += oneVMTotalCompletionTime;
                double avgCompletionTime = oneVMTotalCompletionTime / exeList.size();
                maxAvgCompletionTime = maxAvgCompletionTime < avgCompletionTime ? avgCompletionTime: maxAvgCompletionTime;
            }
        }

        setMeanCompletionTime(maxAvgCompletionTime);
        setTotalCompletionTime(totalCompletionTime);

        setSuccessfulTaskNum(successfulTaskNumber);
        double successRatio = (double) successfulTaskNumber / (double)totalTaskNumber;
        setSuccessfulTaskRatio(successRatio);

        System.out.println("总执行成功数："+successfulTaskNumber);
        System.out.println("总数："+totalTaskNumber);
        System.out.println("平均执行时间："+maxAvgCompletionTime);
    }
}
