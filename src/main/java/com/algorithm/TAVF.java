package main.java.com.algorithm;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;
import main.java.com.resource.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TAVF: TASK ALLOCATION VEHICULAR FOGS(资源分配). + GODA(贪婪性卸载决策)
 * source: https://ieeexplore.ieee.org/document/8977328. Mobile Vehicles as Fog Nodes for Latency Optimization in Smart Cities
 * 选择节点：任务按需求降序排，服务节点按可用资源升序排，遍历任务，遍历服务节点，可用就分配。
 * 资源分配：任务按需求升序排，给small task优先级，减少等待时间，分配最小完成时间的VM给任务
 * 为小任务分配高的优先级，其次预期最小完成时间优先选择服务节点。
 * 但是没有考虑任务的方向性任务之间的优先级问题，和移动性所带来的额外的结果传输时间。而且没有考虑任务的紧急程度
 */
public class TAVF extends Scheduler
{
    @Override
    public void runSchedule(List<Edge> edgeList, List<Vehicle> vehicleList)
    {
//        List<Task> taskList = new ArrayList<>();
//        for (Vehicle vehicle : vehicleList)
//        {
//            for (Task task : vehicle.getTaskList())
//            {
//                taskList.add(task);
//            }
//        }
//        // 按资源需求降序
//        taskList.sort(new Comparator<Task>() {
//            @Override
//            public int compare(Task o1, Task o2) {
//                double f1 = o1.getInstructions();
//                double f2 = o2.getInstructions();
//                if (f1 == f2)
//                {
//                    double b1 = o1.getTotalInputData();
//                    double b2 = o2.getTotalInputData();
//                    if(b1 == b2) return 0;
//                    return b1 < b2 ? 1 : -1; // 从大到小
//                }
//                return f1 < f2 ? 1 : -1; // 从大到小
//            }
//        });
        // 资源升序排，？？？这个有点子问题
//        edgeList.sort(new Comparator<Edge>() {
//            @Override
//            public int compare(Edge o1, Edge o2) {
//                double r1 = o1.getProcessSpeed();
//                double r2 = o2.getProcessSpeed();
//                if(r1 == r2)
//                {
//                    double b1 = o1.getBandwidth();
//                    double b2 = o2.getBandwidth();
//                    if(b1 == b2) return 0;
//                    return b1 < b2 ? -1 : 1;
//                }
//                return r1 < r2 ? -1 : 1; // 从小到大升序
//            }
//        });

        // 任务卸载，GODA: greedy offloading decision algorithm
//        for(Task task: taskList)
//        {
//            Edge targetOffloadEdge = null;
//            for(Edge edge: edgeList)
//            {
//                int vmSize = edge.getVMList().size();
//                // 能够满足ddl完成
//                double estimatedCompletionTime = task.getTotalInputData() / (edge.getBandwidth() * 8) + task.getInstructions() / (edge.getProcessSpeed() / vmSize);
//                if(estimatedCompletionTime <= task.getDeadline())
//                {
//                    task.setTargetOffloadEdge(edge);
//                    ArrayList<Task> exeTaskList = (edge.getExeTaskList() == null ? new ArrayList<>() : edge.getExeTaskList());
//                    exeTaskList.add(task);
//                    edge.setExeTaskList(exeTaskList);
//                    break;
//                }
//            }
//        }
        List<Task> taskList = generateCandidateEdgeGroup(edgeList, vehicleList);
        // 按资源需求降序
        taskList.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                double f1 = o1.getInstructions();
                double f2 = o2.getInstructions();
                if (f1 == f2)
                {
                    double b1 = o1.getTotalInputData();
                    double b2 = o2.getTotalInputData();
                    if(b1 == b2) return 0;
                    return b1 < b2 ? 1 : -1; // 从大到小
                }
                return f1 < f2 ? 1 : -1; // 从大到小
            }
        });

        for(Task task: taskList)
        {
            // sort candidateEdgeList，升序
            List<Edge> candidateEdgeList = task.getCandidateEdgeList();
            candidateEdgeList.sort(new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    double r1 = o1.getProcessSpeed();
                    double r2 = o2.getProcessSpeed();
                    if(r1 == r2)
                    {
                        double b1 = o1.getBandwidth();
                        double b2 = o2.getBandwidth();
                        if(b1 == b2) return 0;
                        return b1 < b2 ? -1 : 1;
                    }
                    return r1 < r2 ? -1 : 1; // 从小到大升序
                }
            });
            // 任务卸载
            Edge targetOffloadEdge = null;
            for(Edge edge: candidateEdgeList)
            {
                int vmSize = edge.getVMList().size();
                // 能够满足ddl完成
                double estimatedCompletionTime = task.getTotalInputData() / (edge.getBandwidth()) + task.getInstructions() / (edge.getProcessSpeed() / vmSize);
                if(estimatedCompletionTime <= task.getDeadline())
                {
                    targetOffloadEdge = edge;
                    task.setTargetOffloadEdge(edge);
                    ArrayList<Task> exeTaskList = (edge.getExeTaskList() == null ? new ArrayList<>() : edge.getExeTaskList());
                    exeTaskList.add(task);
                    edge.setExeTaskList(exeTaskList);
                    break;
                }
            }
            if(candidateEdgeList != null && targetOffloadEdge == null)
            {
                targetOffloadEdge = candidateEdgeList.get(0);
                task.setTargetOffloadEdge(targetOffloadEdge);
                ArrayList<Task> exeTaskList = (targetOffloadEdge.getExeTaskList() == null ? new ArrayList<>() : targetOffloadEdge.getExeTaskList());
                exeTaskList.add(task);
                targetOffloadEdge.setExeTaskList(exeTaskList);
            }
        }

        // 资源分配，TAVF: task allocation in vehicular fogs
        for(Edge edge: edgeList)
        {
            ArrayList<Task> exeTaskList = edge.getExeTaskList();
            if(exeTaskList == null) continue;
            // smaller task with higher priority.
            exeTaskList.sort(new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    double f1 = o1.getInstructions();
                    double f2 = o2.getInstructions();
                    if (f1 == f2) return 0;
                    return f1 < f2 ? -1 : 1; // 从小到大
                }
            });

            for(Task task: exeTaskList)
            {
                // transmissionTime
                double transmissionTime = 0;
                Edge coverEdge = task.getCoveredEdge();
                Edge targetOffloadEdge = task.getTargetOffloadEdge();
                double targetEdge_x = targetOffloadEdge.getLocation().getXPos();
                if(coverEdge.getEdgeId() == targetOffloadEdge.getEdgeId())
                {
                    // 同一个edge，不需要multi-hop between RSUs.
                    transmissionTime = task.getTotalInputData() / (targetOffloadEdge.getBandwidth());
                }
                else
                {
                    // 需要计算multi-hops
                    double coveredEdge_x = coverEdge.getLocation().getXPos();
                    int hops = (int)Math.abs(targetEdge_x - coveredEdge_x) / (Constant.COMMUNICATION_RANGE * 2);
                    transmissionTime = hops * task.getTotalInputData() / (Constant.EDGE_TO_EDGE_BANDWIDTH ) + task.getTotalInputData() / (coverEdge.getBandwidth());
                }
                // 传输时间
                task.setArriveEdgeTime(transmissionTime);


//                double minCompletionTime = Double.MAX_VALUE;
//                VM targetOffloadVM = null;
//                for(VM vm: edge.getVMList())
//                {
//                    double aft = Math.max(task.getArriveEdgeTime(), vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
//                    if(aft < minCompletionTime)
//                    {
//                        minCompletionTime = aft;
//                        targetOffloadVM = vm;
//                    }
//                }
                // 资源分配
                VM targetOffloadVM = null;
                double eat = Double.MAX_VALUE;
                for(VM vm: edge.getVMList())
                {
                    double at = vm.getAvailTime();
                    if(eat > at)
                    {
                        eat = at;
                        targetOffloadVM = vm;
                    }
                }

                if(targetOffloadVM != null)
                {
                    execute(task, targetOffloadVM);
                }
                // 返回结果
                resultFeedback(edgeList,task,coverEdge,targetOffloadEdge);
            }
        }
//        calMeanCompletionTime(edgeList);
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
        List<Task> taskList = new ArrayList<>();
        for(Vehicle vehicle: vehicleList)
        {
            Task task = vehicle.getTaskList().get(0);
            // 计算位置
            double vehicle_x = vehicle.getLocation().getXPos();
            Constant.VEHICLE_DIRECTION direction = vehicle.getDirection();
            double deadline = task.getDeadline();
            double speed = vehicle.getSpeed();
            double vehicle_x_next;
            if(direction == Constant.VEHICLE_DIRECTION.SOUTH)
            {
                vehicle_x_next = vehicle_x + speed * deadline;
            }
            else
            {
                vehicle_x_next = vehicle_x - speed * deadline;
            }
            double x_min = Math.min(vehicle_x, vehicle_x_next);
            double x_max = Math.max(vehicle_x, vehicle_x_next);
            ArrayList<Edge> candidateEdgeList = new ArrayList<>();
            for(Edge edge: edgeList)
            {
                double edge_x = edge.getLocation().getXPos();
                double edge_x_min = edge_x - edge.getCommunicationRange();
                double edge_x_max = edge_x + edge.getCommunicationRange();
                if(x_min <= edge_x && edge_x <= x_max)
                {
                    candidateEdgeList.add(edge);
                }
                else if(edge_x_min <= x_min && x_min <edge_x_max)
                {
                    candidateEdgeList.add(edge);
                }
                else if(edge_x_min <= x_max && x_max < edge_x_max)
                {
                    candidateEdgeList.add(edge);
                }
            }
            if(candidateEdgeList.size() == 0) continue;
            task.setCandidateEdgeList(candidateEdgeList);
            taskList.add(task);
        }
        return taskList;
    }
}
