package main.java.com.algorithm;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;
import main.java.com.resource.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Task Offloading with Task Classification and Offloading Nodes Selection for MEC-Enabled IoV.
 * https://dl.acm.org/doi/10.1145/3475871. 2022 ACM Transactions on Internet Technology
 * 只选coveredRSU，但考虑结果传输时间。
 * 没有考虑卸载到其他节点会减少负载，利用更高的计算能力和传输能力，
 * 根据计算资源和传输速率对每个任务的可用节点进行评估，然后选择评估值最大的节点作为卸载节点.
 *  * CCRi
 *  * αi βi
 *  * Qij = αi*Rj + βi*Fj
 *  * 按Qij 降序排
 *  * 应该需要更新参数。否则都会只选处理能力最大的，导致过载
 *  没有考虑车辆移动性带来的任务额外的传输代价，只衡量了直接传输的带宽资源和计算资源，且容易造成过载
 *  任务数变化，RPD变化不大，原因是因为，路段上的流量比较平均，没有特别过载的服务器现象。
 */
public class MONSA extends Scheduler{
    @Override
    public void runSchedule(List<Edge> edgeList, List<Vehicle> vehicleList)
    {
        // 为每个task产生候选节点
        List<Task> taskList = generateCandidateEdgeGroup(edgeList, vehicleList);

        for(Vehicle vehicle: vehicleList)
        {
            for(Task task: vehicle.getTaskList())
            {
                double maxQ = 0;
                Edge targetOffloadEdge = null;

                List<Edge> candidateEdgeList = task.getCandidateEdgeList();
                // 从候选节点中选出maxQ作为卸载节点
                if(candidateEdgeList == null) {
                    task.setStatus(Constant.TASK_STATUS.FAILED);
                    continue;
                }
                for(Edge edge: candidateEdgeList)
                {
                    //double bandwidth = task.getApBandwidth();
                    double bandwidth = edge.getBandwidth();
                    double CCR = task.getTotalInputData() * bandwidth * task.getVehicle().getProcessSpeed()/(task.getInstructions()*(4.18)); // 根据论文数据计算出来
                    // System.out.println("task bandwidth CCR "+task.getTaskId()+" "+task.getApBandWidth()+" "+CCR);

                    // 计算αi βi
                    double beta = 1 / (CCR + 1);
                    double alpha = 1 - beta;
                    double sinr = 1.1699;
                    double Rj = bandwidth * sinr;

                    double Q = alpha * Rj + beta * edge.getProcessSpeed();
                    if(maxQ < Q)
                    {
                        maxQ = Q;
                        targetOffloadEdge = edge;
                    }
                }
//                System.out.println("coveredge & targetedge "+task.getCoveredEdge().getEdgeId() +" "+targetOffloadEdge.getEdgeId());

//                Edge targetOffloadEdge = task.getCoveredEdge();
                task.setTargetOffloadEdge(targetOffloadEdge);
                double transmissionTime = task.getTotalInputData() / (targetOffloadEdge.getBandwidth());
                task.setArriveEdgeTime(transmissionTime);

                ArrayList<Task> exeTaskList = targetOffloadEdge.getExeTaskList() == null ? new ArrayList<>() : targetOffloadEdge.getExeTaskList();
                exeTaskList.add(task);
                targetOffloadEdge.setExeTaskList(exeTaskList);

                VM targetOffloadVM = null;
                double minCompletionTime = Double.MAX_VALUE;
                for(VM vm: targetOffloadEdge.getVMList())
                {
                    // 最早完成时间优先
                    double aft = Math.max(task.getArriveEdgeTime(), vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
                    if(aft < minCompletionTime)
                    {
                        minCompletionTime = aft;
                        targetOffloadVM = vm;
                    }
                }
                if(targetOffloadVM != null)
                {
                    execute(task, targetOffloadVM);
                }
                if(task.getStatus() == Constant.TASK_STATUS.FAILED) continue; // 已经超时

                double completionTime = task.getAFT();
                double edge_x = targetOffloadEdge.getLocation().getXPos();
                double edge_x_boundary;
//                double edge_x_boundary_left;

                double vehicle_x = vehicle.getLocation().getXPos();
                double vehicleNextPosition;
                boolean exceed = false; // 超出RSU范围标志

                if(vehicle.getDirection() == Constant.VEHICLE_DIRECTION.SOUTH)
                {
                    vehicleNextPosition = vehicle_x + vehicle.getSpeed() * completionTime;
                    edge_x_boundary = edge_x + targetOffloadEdge.getCommunicationRange();

                    if(vehicleNextPosition > edge_x_boundary)
                    {
                        exceed = true;
                    }
                    if(!exceed)
                    {
                        task.setStatus(Constant.TASK_STATUS.FINISHED);
                        continue; // 没超出范围
                    }
                    else // 超出范围，考虑结果传输
                    {
                        int coverEdgeId = targetOffloadEdge.getEdgeId();
                        int hops = -1;
                        for(int i=coverEdgeId + 1; i < edgeList.size(); i++)
                        {
                            Edge edge = edgeList.get(i);
                            if(vehicleNextPosition >= edge.getLocation().getXPos()-edge.getCommunicationRange()&&vehicleNextPosition <=edge.getLocation().getXPos()+edge.getCommunicationRange())
                            {
                                hops = coverEdgeId - i;
                                break;
                            }
                        }
                        if(hops == -1)
                        {
                            task.setStatus(Constant.TASK_STATUS.FAILED); // 驶出覆盖范围了
                            continue;
                        }
                        double feedbackTime = hops * Constant.TASK_RESULT_SIZE / (Constant.EDGE_TO_EDGE_BANDWIDTH);
                        double totalCompletionTime = feedbackTime + completionTime;
                        if(totalCompletionTime > task.getDeadline())
                        {
                            task.setStatus(Constant.TASK_STATUS.FAILED); // 驶出覆盖范围了
                            continue;
                        }
                        task.setAFT(totalCompletionTime);
                    }
                }
                else
                {
                    vehicleNextPosition = vehicle_x - vehicle.getSpeed() * completionTime;
                    edge_x_boundary = edge_x - targetOffloadEdge.getCommunicationRange();
                    if(vehicleNextPosition < edge_x_boundary)
                    {
                        exceed = true;
                    }
                    if(!exceed)
                    {
                        task.setStatus(Constant.TASK_STATUS.FINISHED);
                        continue; // 没超出范围
                    }
                    else // 超出范围，考虑结果传输
                    {
                        int coverEdgeId = targetOffloadEdge.getEdgeId();
                        int hops = -1;
                        for(int i=coverEdgeId - 1; i>=0; i--)
                        {
                            Edge edge = edgeList.get(i);
                            if(vehicleNextPosition >= edge.getLocation().getXPos()-edge.getCommunicationRange()&&vehicleNextPosition <=edge.getLocation().getXPos()+edge.getCommunicationRange())
                            {
                                hops = coverEdgeId - i;
                                break;
                            }
                        }
                        if(hops == -1)
                        {
                            task.setStatus(Constant.TASK_STATUS.FAILED); // 驶出覆盖范围了
                            continue;
                        }
                        double feedbackTime = hops * Constant.TASK_RESULT_SIZE / (Constant.EDGE_TO_EDGE_BANDWIDTH);
                        double totalCompletionTime = feedbackTime + completionTime;
                        if(totalCompletionTime > task.getDeadline())
                        {
                            task.setStatus(Constant.TASK_STATUS.FAILED); // 驶出覆盖范围了
                            continue;
                        }
                        task.setAFT(totalCompletionTime);
                    }
                }
            }
        }
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
//    /**
//     * Task is executed on VM.
//     * @param task
//     * @param vm
//     */
//    public void execute(Task task, VM vm)
//    {
//        double deadline = task.getDeadline();
//
//        double aft = Math.max(task.getArriveEdgeTime(), vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
//        task.setAFT(aft);
//        task.setTargetOffloadVM(vm);
//
//        vm.setAvailTime(aft);
//        List<Task> exeTaskVMList = (vm.getExeTaskList() == null ? new ArrayList<>(): vm.getExeTaskList());
//        exeTaskVMList.add(task);
//        vm.setExeTaskList(exeTaskVMList);
//
//        if(aft > deadline) // 超时
//        {
////            System.out.println("超时");
//            task.setStatus(Constant.TASK_STATUS.FAILED);
//        }
////        else // 完成
////        {
////            task.setStatus(Constant.TASK_STATUS.FINISHED);
////        }
//    }
}
