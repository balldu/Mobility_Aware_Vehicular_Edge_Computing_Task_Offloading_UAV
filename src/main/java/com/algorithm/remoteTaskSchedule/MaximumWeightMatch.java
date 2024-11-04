package main.java.com.algorithm.remoteTaskSchedule;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MaximumWeightMatch extends AbstractRemoteTaskSchedule{
    int vmNum;
    int taskNum;
    double[][] weight;
    double[] lx;
    double[] ly; // A, B集合顶标值
    int[] flagX;
    int[] flagY; // 记录A, B里的点是否搜索过
    int[] match; // B集合中第i个点在A集合中的匹配编号
    int count;
    @Override
    public void exeRemote(List< Edge > edgeList)
    {
        for(Edge edge: edgeList)
        {
            count=0;
            // flag: 标志是否已经分配 status
            List<Task> ts = edge.getExeTaskList();

            if(ts == null) continue;
            vmNum = edge.getVMList().size();
            taskNum = ts.size();

            while(count < taskNum && ((count + vmNum) <= taskNum))
            {
                // 每次匹配vmNum个，考虑任务完成时间，而不是任务执行时间，考虑到后面任务的等待时间

                // 1. 构造二分图
                // 图的两个点集的数目一样
                // 每次挑选一批（同vm数目的task集）

                constructGraph(count, count+vmNum, ts, edge.getVMList());

                lx = new double[vmNum+1];
                ly = new double[vmNum+1]; // A, B集合顶标值
                flagX = new int[vmNum+1];
                flagY = new int[vmNum+1]; // 记录A, B里的点是否搜索过
                match = new int[vmNum+1]; // B集合中第i个点在A集合中的匹配编号

                // 2. 二分图最大权匹配
                boolean successFlag = KM();
                if(!successFlag)
                {
                    // 匹配失败
//                    System.out.println("max weight match algorithm failed");
                    // 找其他方案
                    otherMatchApproachCall(count, count+vmNum, ts, edge);
                    count+=vmNum;
                    continue;
                }

                double sum = 0;
                for(int j=1;j<=vmNum;j++)
                {
                    if (match[j] !=0 )
                    {
                        // 匹配
                        Task tj = ts.get(count+j-1); // 这里有问题
                        VM vj = edge.getVMList().get(match[j]-1);
                        // 任务匹配VM
                        tj.setTargetOffloadVM(vj);
                        // VM加入任务
                        List<Task> taskListVM = vj.getExeTaskList() == null ? new ArrayList<>() : vj.getExeTaskList();
                        taskListVM.add(tj);
                        vj.setExeTaskList(taskListVM);
                        // 执行
                        double exeTime = tj.getInstructions() / vj.getProcessSpeed();
                        double aft = Math.max(vj.getAvailTime(), tj.getArriveEdgeTime()) + exeTime;
                        vj.setAvailTime(aft);
                        tj.setAFT(aft);
                        sum += weight[match[j]][j];
                    }
                }
                count+=vmNum;
            }
            if(count < taskNum && vmNum > taskNum - count) // 直接一对一匹配
            {
                // 本来想调用的别的策略，但是不能截取list，因为截取后不能修改
                otherMatchApproachCall(count, taskNum, ts, edge);
            }
        }
    }

    @Override
    public void exeRemote(Task task)
    {
        // nothing
    }

    /**
     * 应该是只能调用到该edge上的VM，别的edge的match不用管.
     * @param begin
     * @param end
     * @param taskList
     * @param edge
     */
    private void otherMatchApproachCall(int begin, int end, List<Task> taskList, Edge edge)
    {
        List<Task> subTaskList = new ArrayList<>();
        for(int ti = 0; ti < end - begin; ti++)
        {
            Task task = taskList.get(ti + begin);
            subTaskList.add(task);
        }
        MaxMinCopy(edge, subTaskList);
    }

    /**
     * Max-Min Copy
     */
    private void MaxMinCopy(Edge edge, List<Task> exeList)
    {
        // 记录每个task在所有vm上的最小完成时间
        int countTask = 0;
        int listLength = exeList.size();
        boolean[] completed = new boolean[listLength];
        while(countTask < listLength)
        {
            int maxIndex = -1;
            double maxCompletionTime = 0;

            VM targetVM = null;
            Task targetTask = null;
            int index = 0;
            for(Task task: exeList)
            {
                double minTaskCompletionTime = Double.MAX_VALUE;
                VM minTaskCompletionTimeVM = null;
                if(!completed[index])
                {
                    for(VM vm: edge.getVMList())
                    {
                        double completionTime = Math.max(task.getArriveEdgeTime(),vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
                        if(completionTime < minTaskCompletionTime)
                        {
                            minTaskCompletionTime = completionTime;
                            minTaskCompletionTimeVM = vm;
                        }
                    }
                }

                if(minTaskCompletionTime != Double.MAX_VALUE && minTaskCompletionTime > maxCompletionTime)
                {
                    maxCompletionTime = minTaskCompletionTime;
                    maxIndex = index;
                    targetTask = task;
                    targetVM = minTaskCompletionTimeVM;
                }
                index++;
            }
            // 开始执行
            if(maxIndex != -1)
            {
                completed[maxIndex]= true;
                countTask++;
                execute(targetTask, targetVM);
            }
        }
    }

    /**
     * 为任务集和vm集构造二分图
     * @param taskList
     * @param vmList
     * @return double[][]
     */
    private void constructGraph(int begin, int end, List<Task> taskList, List<VM> vmList)
    {
        weight = new double[vmNum+1][vmNum+1];

        for(int ti = 0; ti < end - begin; ti++)
        {
            Task task = taskList.get(ti + begin);
            for(int vi = 1; vi <= vmNum; vi++)
            {
                VM vm = task.getTargetOffloadEdge().getVMList().get(vi-1);
                // 这个时间有问题，因为还没计算出来
                // 最开始就运行remainTask
                // 每次配对vmNum个就执行
                weight[vi][ti+1] = Constant.CONSTANT_NUMBER / (Math.max(task.getArriveEdgeTime(),vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed());
            }
        }
    }

    /**
     * KM算法
     * @return boolean，表示是否匹配成功
     */
    private boolean KM()
    {
        // 初始化顶标值
        for(int i=1;i<=vmNum;i++)
        {
            lx[i] = Arrays.stream(weight[i]).max().getAsDouble();
        }
        for(int i=1;i<=vmNum;i++)
        {
            ly[i] = 0;
        }
        // 每次匹配vmNum个任务
        for(int i=1; i<=vmNum;i++)
        {
            while(true)
            {
                flagX = new int[vmNum+1];
                flagY = new int[vmNum+1]; // 置零，这样不好

                if(matching(i)) break;
                // 不能匹配
                boolean exit = update();
                if(!exit) return false; // 有时候inc太小，导致更新起不到作用。
            }
        }
        return true;
    }

    /**
     * 更新顶标值lx, ly
     * @return
     */
    private boolean update()
    {
        double inc = Double.MAX_VALUE;
        for(int i=1;i<=vmNum;i++)
        {
            if(flagX[i] == 1)
            {
                for(int j=1;j<=vmNum;j++)
                {
                    if(flagY[j] == 0)
                    {
                        inc = Math.min(inc, lx[i]+ly[j]-weight[i][j]);
                    }
                }
            }
        }
        if(inc < Math.pow(10, -8)) // inc值太小，起不到作用，导致死循环
        {
            return false;
        }
        for(int i=1;i<=vmNum;i++)
        {
            if(flagX[i] == 1) // 对于所有访问过的A集合的点，将它的顶标-inc
            {
                lx[i]-=inc;
            }
        }
        for(int i=1;i<=vmNum;i++)
        {
            if(flagY[i] == 1) // 对于所有访问过的B集合的点，将它的顶标+inc
            {
                ly[i]+=inc;
            }
        }
        return true;
    }

    /**
     * 将vm i匹配给合适的task j
     * @param i
     * @return
     */
    private boolean matching(int i)
    {
        flagX[i] = 1;
        for(int j = 1; j <= vmNum; j++)
        {
            // i到j可行且j未被访问
            if(lx[i]+ly[j]==weight[i][j] && flagY[j] == 0)
            {
                flagY[j]=1;
                if(match[j]==0 || matching(match[j]))
                {
                    match[j] = i;
                    return true;
                }
            }
        }
        return false;
    }
}
