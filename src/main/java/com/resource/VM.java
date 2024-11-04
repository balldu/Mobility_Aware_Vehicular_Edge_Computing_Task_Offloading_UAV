package main.java.com.resource;

import main.java.com.application.Task;
import main.java.com.constant.Constant;

import java.util.List;

/**
 * 容器资源的抽象
 * 区分类型：CPLUSPLUS 0.3; JAVA 0.6; PYTHON 1s.
 * 应该限制资源，限制容器启动个数
 * 或者假设最多只有5个容器，类型可任意。先这么干。
 * 容器的属性根据类型定下。 比如处理速度。
 * 为了弥补容器启动时间，分配更多资源给启动时间长的容器。
 */
public class VM {
    private int vmId;

    // 处理速度
    private double processSpeed;

    // 最早可用时间
    private double availTime;

    // 容器启动时间
    private double startupTime;

    // 容器镜像文件类型
    private Constant.VM_TYPES type;

    // 容器是否启动
    private boolean isStarted;

    // 所属服务器节点
    private Edge edge;

    // 要执行的taskList
    private List<Task> exeTaskList;

    // 上一时段遗留的remainTaskList
    private List<Task> remainTaskList;

    public VM(int vmId, double processSpeed) {
        this.vmId = vmId;
        this.processSpeed = processSpeed;
    }
    public void reset()
    {
        exeTaskList = null;
        remainTaskList = null;
        availTime = 0;
    }
    public int getVmId() {
        return vmId;
    }

    public void setVmId(int vmId) {
        this.vmId = vmId;
    }

    public double getProcessSpeed() {
        return processSpeed;
    }

    public void setProcessSpeed(double processSpeed) {
        this.processSpeed = processSpeed;
    }

    public double getAvailTime() {
        return availTime;
    }

    public void setAvailTime(double availTime) {
        this.availTime = availTime;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public List<Task> getExeTaskList() {
        return exeTaskList;
    }

    public void setExeTaskList(List<Task> exeTaskList) {
        this.exeTaskList = exeTaskList;
    }

    public List<Task> getRemainTaskList() {
        return remainTaskList;
    }

    public void setRemainTaskList(List<Task> remainTaskList) {
        this.remainTaskList = remainTaskList;
    }

    public double getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(double startupTime) {
        this.startupTime = startupTime;
    }

    public Constant.VM_TYPES getType() {
        return type;
    }

    public void setType(Constant.VM_TYPES type) {
        this.type = type;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }
}
