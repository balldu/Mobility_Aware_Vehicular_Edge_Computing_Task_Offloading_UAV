package main.java.com.application;

import main.java.com.constant.Constant;
import main.java.com.resource.Edge;
import main.java.com.resource.VM;
import main.java.com.resource.Vehicle;
import main.java.com.utils.RandomNumberUtils;

import java.util.ArrayList;

public  class Task {

    public int taskId;

    public int vehicleId;

	private Vehicle vehicle; // 产生该任务的设备

	// 任务数据量，MB
    public double totalInputData;

	// 任务需要处理的指令数
    public double instructions;

	// 任务执行开始时间
    public double AST;

	// 任务执行结束时间
    public double AFT;

	// 偏好列表，根据规则的偏好，如最近节点、、、
	private ArrayList<Edge> preferenceEdgeList;

	// 候选组
	private ArrayList<Edge> candidateEdgeList;

	// 任务所处覆盖范围内的服务器
	private Edge coveredEdge;

	// 传输时间
    private double transmissionTime;

	// 分配的带宽
	private double apBandWidth;

	// 任务所需容器类型
	private Constant.VM_TYPES requiredType;

	// 要卸载上去的服务器
	private Edge targetOffloadEdge;

	private VM targetOffloadVM;

	// 估计计算时间
    private double estimateComputingTime;

	// 估计总时间=计算时间+传输时间
    private double estimateTotalTime;

	// 任务的ddl
    private double deadline;

	// 是否要卸载
    private boolean isOffload;

	// 任务到达edge server的时间，考虑上传+传输几跳/驶入范围
	private double arriveEdgeTime;

	// 任务进行状态
	private Constant.TASK_STATUS status; // 0：未执行，1：执行完成，2：执行未完成，3：执行失败

    public Task(){

    }

    public Task(int taskId, int vehicleId)
    {
        AST = 0;
        AFT = 0;
        this.taskId = taskId;
        this.vehicleId = vehicleId;
    }

    public Task(int taskID, int vehicleId, double instrcutions, double deadline) {
		AST = 0;
		AFT = 0;
		this.taskId = taskID;
		this.vehicleId = vehicleId;
		this.instructions = instrcutions;
		this.deadline = deadline;
	}

	public Task(int taskID, int vehicleId, double dataSize , double instrcutions, double deadline) {
		AST = 0;
		AFT = 0;
		this.taskId = taskID;
		this.vehicleId = vehicleId;
		this.instructions = instrcutions;
		this.totalInputData = dataSize;
		this.deadline = deadline;
	}
	
	public void reset() {
		AST = 0;
		AFT = 0;
		estimateTotalTime = 0;
		estimateComputingTime = 0;
		targetOffloadEdge = null;
		targetOffloadVM = null;
		apBandWidth = 0;
		transmissionTime = 0;
		isOffload = false;
		status = Constant.TASK_STATUS.NOT_EXECUTION;
		candidateEdgeList = null;
		preferenceEdgeList = null;
		// coveredEdge = null;
		arriveEdgeTime = 0;
	}

	/**
	 * 随机生成任务的大小和隐私等级
	 * @param dataSizeIndex
	 */
	public void initTask(int ddlIndex, int dataSizeIndex)
	{
		// 1. 随机生成输入数据量
		double remain = RandomNumberUtils.getDoubleRandomNumber(Constant.TASK_DATA_SIZE_RANGE[dataSizeIndex][0], Constant.TASK_DATA_SIZE_RANGE[dataSizeIndex][1]);
		totalInputData = remain;

		// 2.随机生成指令数
		instructions = RandomNumberUtils.getRandomNumber(Constant.TASK_INSTRUCTIONS_MIN, Constant.TASK_INSTRUCTIONS_MAX);

		// 3. 随机生成ddl
		deadline = RandomNumberUtils.getDoubleRandomNumber(Constant.TASK_DEADLINE_RANGE[ddlIndex][0], Constant.TASK_DEADLINE_RANGE[ddlIndex][1]);

	}
	public void initTask(int dataSizeIndex) {

		// 1. 随机生成输入数据量
		double remain = RandomNumberUtils.getDoubleRandomNumber(Constant.TASK_INPUT_SIZE[dataSizeIndex][0], Constant.TASK_INPUT_SIZE[dataSizeIndex][1]);
		totalInputData = remain;

		// 2.随机生成指令数
		instructions = RandomNumberUtils.getRandomNumber(Constant.TASK_INSTRUCTIONS_MIN, Constant.TASK_INSTRUCTIONS_MAX);

		// 3. 随机生成ddl
		deadline = RandomNumberUtils.getDoubleRandomNumber(Constant.TASK_DEADLINE_MIN, Constant.TASK_DEADLINE_MAX);
		
	}

	public Constant.TASK_STATUS getStatus() {
        return status;
    }

    public void setStatus(Constant.TASK_STATUS status) {
        this.status = status;
    }

	public double getAST() {
		return AST;
	}

	public void setAST(double aST) {
		AST = aST;
	}

	public double getAFT() {
		return AFT;
	}

	public void setAFT(double aFT) {
		AFT = aFT;
	}

	public double getEstimateTotalTime() {
		return estimateTotalTime;
	}

	public void setEstimateTotalTime(double estimateTotalTime) {
		this.estimateTotalTime = estimateTotalTime;
	}

	public double getEstimateProcessTime() {
		return estimateComputingTime;
	}

	public void setEstimateComputingTime(double estimateComputingTime) {
		this.estimateComputingTime = estimateComputingTime;
	}
	public ArrayList<Edge> getPreferenceEdgeList()
	{
		return preferenceEdgeList;
	}

	public void setPreferenceEdgeList(ArrayList<Edge> preferenceEdgeList)
	{
		this.preferenceEdgeList = preferenceEdgeList;
	}

	public ArrayList<Edge> getCandidateEdgeList()
	{
		return candidateEdgeList;
	}

	public void setCandidateEdgeList(ArrayList<Edge> el)
	{
		this.candidateEdgeList = el;
	}

	public Vehicle getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle)
	{
		this.vehicle=vehicle;
	}
	
	public double getDeadline() {
		return deadline;
	}

	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getTotalInputData() {
		return totalInputData;
	}

	public void setTotalInputData(double totalInputData) {
		this.totalInputData = totalInputData;
	}

	public double getInstructions() {
		return instructions;
	}

	public void setInstructions(double instructions) {
		this.instructions = instructions;
	}

	public double getTransmissionTime() {
		return transmissionTime;
	}

	public double getArriveEdgeTime() {
		return arriveEdgeTime;
	}

	public void setArriveEdgeTime(double arriveEdgeTime) {
		this.arriveEdgeTime = arriveEdgeTime;
	}

	public void setTransmissionTime(double transmissionTime) {
		this.transmissionTime = transmissionTime;
	}
	@Override
	public String toString() {
		return "Task [vehicleId=" + vehicleId + ", instructions=" + instructions 
				+ ", totalInputData=" + totalInputData + "]";
	}

	public boolean isOffload() {
		return isOffload;
	}
	public void setOffload(boolean offload) {
		this.isOffload = offload;
	}

	public Edge getTargetOffloadEdge()
    {
        return targetOffloadEdge;
    }
    public void setTargetOffloadEdge(Edge e)
    {
        this.targetOffloadEdge = e;
    }

	public double getApBandwidth() {
        return apBandWidth;
    }
    public void setApBandwidth(double apBandwidth) {
        this.apBandWidth = apBandwidth;
    }

	public Constant.VM_TYPES getRequiredType() {
		return requiredType;
	}

	public void setRequiredType(Constant.VM_TYPES requiredType) {
		this.requiredType = requiredType;
	}

	public Edge getCoveredEdge() {
		return coveredEdge;
	}

	public void setCoveredEdge(Edge coveredEdge) {
		this.coveredEdge = coveredEdge;
	}

	public VM getTargetOffloadVM() {
		return targetOffloadVM;
	}

	public void setTargetOffloadVM(VM targetOffloadVM) {
		this.targetOffloadVM = targetOffloadVM;
	}
}
