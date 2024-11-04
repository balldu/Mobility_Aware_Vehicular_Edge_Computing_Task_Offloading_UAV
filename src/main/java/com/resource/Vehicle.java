package main.java.com.resource;

import main.java.com.application.Task;
import main.java.com.constant.Constant;
import main.java.com.utils.Location;

import java.util.List;

public class Vehicle {
    private int vehicleId; // 车辆ID

    private List<Task> taskList; // 设备产生的任务列表
    
    private double apBandWidth; // AP为其分配的带宽

    private List<Task> offloadTasks; // 卸载任务列表

    private double tranAvailTime; // 传输任务的可用时间

    private double processSpeed;

    private double totalDataSize;

    // 车辆目前的位置
    private Location location;

    // 车辆目前的速度
    private double speed;

    // 车辆行驶方向
    private Constant.VEHICLE_DIRECTION direction; // north / south

    public Vehicle(int id, double processSpeed, Constant.VEHICLE_DIRECTION direction)
    {
        this.vehicleId = id;
        this.processSpeed = processSpeed;
        this.direction = direction;

        // offloadTasks = new ArrayList<>();
    }

    public double getApBandwidth() {
        return apBandWidth;
    }
    public void setApBandwidth(double apBandwidth) {
        this.apBandWidth = apBandwidth;
    }

    public void setTotalDataSize(double totalDataSize) {
        this.totalDataSize = totalDataSize;
    }

    public double getTotalDataSize() {
        return totalDataSize;
    }

    @Override
    public String toString() {
        return "Device" + super.toString().substring(4);
    }

    public List<Task> getTaskList()
    {
        return taskList;
    }
    public void setTaskList(List<Task> taskList)
    {
        this.taskList = taskList;
    }

    public List<Task> getOfftasks() {
        return offloadTasks;
    }

    public void setOfftasks(List<Task> offtasks) {
        this.offloadTasks = offtasks;
    }

    public double getProcessSpeed()
    {
        return processSpeed;
    }
    public void setProcessSpeed(double processSpeed)
    {
        this.processSpeed = processSpeed;
    }

    public double getTranAvailTime() {
        return tranAvailTime;
    }

    public void setTranAvailTime(double tranAvailTime) {
        this.tranAvailTime = tranAvailTime;
    }

    // 车辆目前的位置
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // 车辆目前的速度
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // 车辆行驶方向

    public Constant.VEHICLE_DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Constant.VEHICLE_DIRECTION direction) {
        this.direction = direction;
    }
}
