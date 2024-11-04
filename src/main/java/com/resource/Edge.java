package main.java.com.resource;

// import main.java.com.resource.VM;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.utils.Location;
import main.java.com.utils.RandomNumberUtils;

import java.util.ArrayList;

public class Edge {
    // 该主机的全局序号
    private int edgeId;

    // 该主机里面的虚拟机个数
    // private ArrayList<VM> vmList;

    // 该主机覆盖范围下的taskList
    private ArrayList<Vehicle> vehicleList;

    // 该主机覆盖范围下的taskList
    private ArrayList<Task> taskList;

    // 卸载执行序列
    private ArrayList<Task> exeTaskList;

    // 前一时间段剩下未完成的任务
    private ArrayList<Task> remainTaskList;

    private int vmNum;
    // 容器集合
    private ArrayList<VM> vmList;

    // 容器类型
    private int vmTypeJAVA;
    private int vmTypePYTHON;
    private int vmTypeC;

    // 通信范围
    private double communicationRange;

    // 该Host的地理位置
    private Location location;    

    private int numOfCores;
    private double mips;
    private int ram;
    private long storage;

    // 该host的信道情况？
    private double bandwidth;

    // 下一时段的预测交通流量
    private int flow_pred;

    // 处理速度，计算能力
    private double processSpeed;
    // private Edge targetOffloadEdge;

    public Edge(int id) {
        this.edgeId = id;
        // vmList = new ArrayList<VM>();
    }

     public Edge(int id, int vmNum) {
         this.edgeId = id;
         this.vmNum = vmNum;
         this.vmList = new ArrayList<>();
     }

    public Edge(int hostIdCounter, int numOfCores, double mips, int ram, long storage, long bandwidth) {
        // vmList = new ArrayList<VM>();
        this.edgeId = hostIdCounter;
        this.numOfCores = numOfCores;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.bandwidth = bandwidth;
    }

    // public ArrayList<VM> getVmList() {
    //     return vmList;
    // }

    // public void setVmList(ArrayList<VM> vmList) {
    //     this.vmList = vmList;
    // }
    
    public ArrayList<Vehicle> getVehicleList()
    {
        return vehicleList;
    }

    public void setVehicleList(ArrayList<Vehicle> vehicleList)
    {
        this.vehicleList = vehicleList;
    }

    public ArrayList<Task> getTaskList()
    {
        return taskList;
    }

    public void setTaskList(ArrayList<Task> taskList)
    {
        this.taskList = taskList;
    }

    public ArrayList<Task> getExeTaskList()
    {
        return exeTaskList;
    }

    public void setExeTaskList(ArrayList<Task> exeTaskList)
    {
        this.exeTaskList = exeTaskList;
    }

    public ArrayList<Task> getRemainTaskList()
    {
        return remainTaskList;
    }

    public void setRemainTaskList(ArrayList<Task> remainTaskList)
    {
        this.remainTaskList = remainTaskList;
    }

    public double getCommunicationRange() {
        return communicationRange;
    }

    public void setCommunicationRange(double communicationRange) {
        this.communicationRange = communicationRange;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(int hostId) {
        this.edgeId = hostId;
    }

    public int getFlowPred() {
        return flow_pred;
    }

    public void setFlow_Pred(int flow_pred) {
        this.flow_pred = flow_pred;
    }

    public double getProcessSpeed() {
        return processSpeed;
    }

    public void setProcessSpeed(double processSpeed) {
        this.processSpeed = processSpeed;
    }
    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public ArrayList<VM> getVMList() {
        return vmList;
    }

    public void setVMList(ArrayList<VM> vmList) {
        this.vmList = vmList;
    }

    /**
	 * 随机初始化服务器VM的处理能力和服务器的位置
	 */
	public void initEdge()
    {
        // 通信范围
        communicationRange = Constant.COMMUNICATION_RANGE;

        // 初始化VM，包括类型、速度根据类型而定
        double totalVMProcessSpeed = 0;

        int type1 = RandomNumberUtils.getRandomNumber(1,vmNum-2);
        int type2 = RandomNumberUtils.getRandomNumber(1,Math.min(3,vmNum-type1-1));
        int type3 = vmNum - (type1 + type2);
        vmTypeC = type1;
        vmTypeJAVA = type2;
        vmTypePYTHON = type3;

        for(int i = 0; i < vmNum; i++)
        {
            Constant.VM_TYPES vmType;
            int processSpeedId;
            if(i<type1)
            {
                vmType = Constant.VM_TYPES.CPLUSPLUS;
                processSpeedId = 2;
            }
            else if(i>=type1 && i<(type2 + type1))
            {
                vmType = Constant.VM_TYPES.JAVA;
                processSpeedId = 1;
            }
            else
            {
                vmType = Constant.VM_TYPES.PYTHON;
                processSpeedId = 0;
            }
            //double processSpeed = RandomNumberUtils.getRandomNumber(Constant.VM_PROCESSSPEED_MIN, Constant.VM_PROCESSSPEED_MAX);
            double processSpeed = RandomNumberUtils.getRandomNumber(Constant.VM_PROCESS_SPEED[processSpeedId][0], Constant.VM_PROCESS_SPEED[processSpeedId][1]);
            totalVMProcessSpeed += processSpeed;
            VM vm = new VM(i, processSpeed);
            vm.setType(vmType);
            vmList.add(vm);
        }

        processSpeed = totalVMProcessSpeed;
        // 随机服务节点的处理能力，100-300
        // processSpeed = RandomNumberUtils.getDoubleRandomNumber(Constant.EDGE_PROCESS_SPEED_MIN, Constant.EDGE_PROCESS_SPEED_MAX);

        location = new Location(Constant.RSU_LOCATION[edgeId][0],Constant.RSU_LOCATION[edgeId][1]);

        bandwidth = RandomNumberUtils.getDoubleRandomNumber(Constant.EDGE_BANDWIDTH_MIN, Constant.EDGE_BANDWIDTH_MAX);

        // 该Host的地理位置
        // private Location location;    

        // private int numOfCores;
        // private double mips;
        // private int ram;
        // private long storage;
    }

    public void reset() {
		exeTaskList = null;
        remainTaskList = null;
        // vehicleList = null;
        taskList = null;
        for(VM vm: vmList)
        {
            vm.reset();
        }
	}

    @Override
    public String toString() {
        return "Host [bandwidth=" + bandwidth + ", hostId=" + edgeId + 
                ", location=" + location + ", mips=" + mips + ", numOfCores=" + numOfCores + ", ram=" + ram
                + ", storage=" + storage + "]";
    }
    
}
