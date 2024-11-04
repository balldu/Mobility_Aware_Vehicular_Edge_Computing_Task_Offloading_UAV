package main.java.com.algorithm.remoteTaskSchedule;

import main.java.com.application.Task;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;

import java.util.List;

/**
 * 最大最早完成时间优先
 */
public class MaxMinSchedule extends AbstractRemoteTaskSchedule
{
    @Override
    public void exeRemote(List< Edge > edgeList)
    {
        for (Edge edge : edgeList)
        {
            // 记录每个task在所有vm上的最小完成时间
            List<Task> exeList = edge.getExeTaskList();
            if(exeList == null) continue;

            int count = 0;
            int listLength = exeList.size();
            boolean[] completed = new boolean[listLength];
            while(count < listLength)
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
                            double completionTime = Math.max(task.getArriveEdgeTime(), vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
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
                    count++;
                    execute(targetTask, targetVM);
                }
            }
        }
    }

    @Override
    public void exeRemote(Task task)
    {

    }
}
