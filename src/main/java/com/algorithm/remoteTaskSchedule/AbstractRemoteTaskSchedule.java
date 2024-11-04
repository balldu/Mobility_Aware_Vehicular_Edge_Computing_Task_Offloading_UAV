package main.java.com.algorithm.remoteTaskSchedule;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.resource.VM;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRemoteTaskSchedule implements RemoteTaskSchedule{
    /**
     * Task is executed on VM.
     * @param task
     * @param vm
     */
    public void execute(Task task, VM vm)
    {
        double deadline = task.getDeadline();

        double aft = Math.max(task.getArriveEdgeTime(), vm.getAvailTime()) + task.getInstructions() / vm.getProcessSpeed();
        task.setAFT(aft);
        task.setTargetOffloadVM(vm);

        vm.setAvailTime(aft);
        List<Task> exeTaskVMList = (vm.getExeTaskList() == null ? new ArrayList<>(): vm.getExeTaskList());
        exeTaskVMList.add(task);
        vm.setExeTaskList(exeTaskVMList);

        if(aft > deadline) // 超时
        {
//            System.out.println("超时");
            task.setStatus(Constant.TASK_STATUS.FAILED);
        }
        else // 完成
        {
            task.setStatus(Constant.TASK_STATUS.FINISHED);
        }
    }


}
