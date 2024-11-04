package main.java.com.algorithm.matchEdgePolicy;

import main.java.com.application.Task;

import java.util.List;

public interface MatchEdge {
//    void match(Task task, List<Edge> edgeList);
    void match(List<Task> taskList);
//    void match(List<List<Task>> taskList);

    void match(Task task);
}
