package main.java.com.algorithm.matchEdgePolicy;

import main.java.com.application.Task;
import main.java.com.resource.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * 选最近的Edge
 */
public class NearestEdgeFirstMatch extends AbstractMatchEdge {
    @Override
    public void match(List<Task> taskList)
    {
        for(Task task: taskList)
        {
            Edge edge = task.getCoveredEdge();
            task.setTargetOffloadEdge(edge);
            ArrayList<Task> exeTaskList = (edge.getExeTaskList() == null ? new ArrayList<>() : edge.getExeTaskList());
            exeTaskList.add(task);
            edge.setExeTaskList(exeTaskList);
        }
    }
    @Override
    public void match(Task task)
    {
        Edge edge = task.getCoveredEdge();
        task.setTargetOffloadEdge(edge);
        ArrayList<Task> exeTaskList = (edge.getExeTaskList() == null ? new ArrayList<>() : edge.getExeTaskList());
        exeTaskList.add(task);
        edge.setExeTaskList(exeTaskList);
    }
}


//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.PriorityQueue;
//import java.util.TreeMap;
//
//import main.java.com.application.Task;
//import main.java.com.resource.Edge;
//
//public class NearestEdgeFirstMatch extends AbstractMatchEdge {
//    /*
//     * 选择最近的节点，得到preference list.
//     */
//    @Override
//    public void match(Task task, List<Edge> edgeList)
//    {
//        double curpos_x = task.getVehicle().getLocation().getXPos();
//
//        int k = 2; // 保留top k个preference list. 优先列表
//
//        ArrayList<Edge> preferenList = new ArrayList<>();
//        TreeMap<Double, List<Edge>> map = new TreeMap<>();
//
//        for(Edge edge: edgeList)
//        {
//            double edge_x = edge.getLocation().getXPos();
//            double distance = Math.abs(curpos_x-edge_x);
//            // 堆？
//            if(map.containsKey(distance))
//            {
//                List<Edge> es = map.get(distance);
//                es.add(edge);
//                map.put(distance, es);
//            }
//            else
//            {
//                List<Edge> es = new ArrayList<>();
//                es.add(edge);
//                map.put(distance, es);
//            }
//        }
//        Iterator iter = map.keySet().iterator();
//        while(iter.hasNext())
//        {
//            double key = (Double)iter.next();
//            ArrayList<Edge> es = (ArrayList<Edge>)map.get(key);
//            for(Edge e:es)
//            {
//                preferenList.add(e);
//                if(preferenList.size()==k)
//                {
//                    // return preferenList;
//                    task.setPreferenceEdgeList(preferenList);
//                    // break;
//                    return;
//                }
//            }
//        }
//    }
//
//}
