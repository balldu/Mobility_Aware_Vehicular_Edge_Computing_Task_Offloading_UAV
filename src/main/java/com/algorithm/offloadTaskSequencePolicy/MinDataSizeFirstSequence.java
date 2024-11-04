//package main.java.com.algorithm.offloadTaskSequencePolicy;
//
//import main.java.com.application.Task;
//
//import java.util.Comparator;
//import java.util.List;
//
//public class MinDataSizeFirstSequence extends AbstractOffloadTaskSequenceSort{
//    @Override
//    public void sort(List<Task> taskList)
//    {
//        // 排序，按照既定规则：数据大小
//        taskList.sort(new Comparator<Task>(){
//            @Override
//            public int compare(Task o1, Task o2){
//                double ratio1 = o1.totalInputData;
//                double ratio2 = o2.totalInputData;
//
//                if(ratio1 == ratio2) return 0;
//                return ratio1 < ratio2 ? -1 : 1; // 从小到大排序
//            }
//        });
//    }
//}
