package main.java.com.algorithm.matchEdgePolicy;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * EarliestAvailableTimeFirstSchedule, not suitable.
 * TODO 代码还有问题，主要是带宽设置，处理速度
 */
public class MinEstimatedCompletionTimeFirstMatch extends AbstractMatchEdge {
    @Override
    public void match(List<Task> taskList)
    {
        for(Task task: taskList)
        {
            double task_X = task.getVehicle().getLocation().getXPos();
            List<Edge> candidateEdgeList = task.getCandidateEdgeList();
            double minCompletionTime = Double.MAX_VALUE;
            Edge targetOffloadEdge = null;
            for(Edge edge: candidateEdgeList)
            {
                // time1 = vehicleTravelTime or taskTravelTime??
                // taskTravelTime = hops * inputDataSize / edge_to_edge_bandwidth;
                // vehicleTravelTime = travelDistance / speed;
                // time2 = uploadTime
                // time3 = executionTime
                // time4 = (uploadTime + executionTime) * speed -> newLocation, feedbackTime
                double edge_X = edge.getLocation().getXPos();
                double distance = Math.min(Math.abs(edge_X + Constant.COMMUNICATION_RANGE - task_X), Math.abs(edge_X - Constant.COMMUNICATION_RANGE - task_X));
                int hops = (int)(Math.abs((edge_X - task.getCoveredEdge().getLocation().getXPos())) / 200);

                double vehicleTravelTime = distance / task.getVehicle().getSpeed();
                double taskTravelTime = hops * task.getTotalInputData() / Constant.EDGE_TO_EDGE_BANDWIDTH;
                double uploadTime = task.getTotalInputData() / Constant.TOTAL_BANDWIDTH; // ???
                double executionTime = task.getInstructions() / (edge.getProcessSpeed() / edge.getVMList().size()); // ???

                double totalCompletionTime = Math.min(vehicleTravelTime,taskTravelTime) + uploadTime + executionTime;
                if(minCompletionTime > totalCompletionTime)
                {
                    minCompletionTime = totalCompletionTime;
                    targetOffloadEdge = edge;
                }
            }
            if(targetOffloadEdge != null)
            {
                task.setTargetOffloadEdge(targetOffloadEdge);
                ArrayList<Task> exeTaskList = (targetOffloadEdge.getExeTaskList() == null ? new ArrayList<>() :  targetOffloadEdge.getExeTaskList());
                exeTaskList.add(task);
                targetOffloadEdge.setExeTaskList(exeTaskList);
            }
        }
    }
    @Override
    public void match(Task task)
    {
        double task_X = task.getVehicle().getLocation().getXPos();
        List<Edge> candidateEdgeList = task.getCandidateEdgeList();
        double minCompletionTime = Double.MAX_VALUE;
        Edge targetOffloadEdge = null;
        for(Edge edge: candidateEdgeList)
        {
            double edge_X = edge.getLocation().getXPos();
            double distance = Math.min(Math.abs(edge_X + Constant.COMMUNICATION_RANGE - task_X), Math.abs(edge_X - Constant.COMMUNICATION_RANGE - task_X));
            int hops = (int)(Math.abs((edge_X - task.getCoveredEdge().getLocation().getXPos())) / 200);

            double vehicleTravelTime = distance / task.getVehicle().getSpeed();
            double taskTravelTime = hops * task.getTotalInputData() / Constant.EDGE_TO_EDGE_BANDWIDTH;
            double uploadTime = task.getTotalInputData() / Constant.TOTAL_BANDWIDTH; // ???
            double executionTime = task.getInstructions() / edge.getProcessSpeed(); // ???

            double totalCompletionTime = Math.min(vehicleTravelTime,taskTravelTime) + uploadTime + executionTime;
            if(minCompletionTime > totalCompletionTime)
            {
                minCompletionTime = totalCompletionTime;
                targetOffloadEdge = edge;
            }
        }
        if(targetOffloadEdge != null)
        {
            task.setTargetOffloadEdge(targetOffloadEdge);
            ArrayList<Task> exeTaskList = (targetOffloadEdge.getExeTaskList() == null ? new ArrayList<>() :  targetOffloadEdge.getExeTaskList());
            exeTaskList.add(task);
            targetOffloadEdge.setExeTaskList(exeTaskList);
        }
    }
}
