package main.java.com.algorithm.remoteTaskSchedule;

import main.java.com.application.Task;
import main.java.com.resource.Edge;

import java.util.List;

public interface RemoteTaskSchedule {
    void exeRemote(List<Edge> edgeList);

//    void match(List<Task> taskList);
    void exeRemote(Task task);
}
