package wGroup;

import java.util.ArrayList;
import java.util.List;

public class HeapSort {
	public static void main(String[] args) {  
        Double[] data5 = new Double[] { 5.0, 3.0, 6.0,100.0,14.0, 1.0, 9.0,  7.0 };
        List<Double> testList = new ArrayList<Double>();
        for(Double ii:data5){
        	testList.add(ii);
        }
        System.out.println(testList.toString()); 
        List<Integer> RESULTList = heapSort(testList,testList.size());
        System.out.println("排序后的数组：");  
        System.out.println(RESULTList.toString());   
    }  
  
    public static void swap(List<Double> data, int i, int j,int[]position) {  
        if (i == j) {  
            return;  
        }  
        data.set(i, data.get(i) + data.get(j));
        data.set(j, data.get(i) - data.get(j));
        data.set(i, data.get(i) - data.get(j));
        position[i]=position[i]+position[j];
        position[j]=position[i]-position[j];
        position[i]=position[i]-position[j];
    }
  
    public static List<Integer> heapSort(List<Double> scoreList,int num) {
    	List<Integer> resultList = new ArrayList<Integer>();
    	
    	List<Double> data = new ArrayList<Double>();
    	data.addAll(scoreList);
    	
    	int[] position = new int[data.size()];
    	for(int i=0;i<position.length;i++){
    		position[i]=i;
    	}
    	
        for (int i = 0; i < num; i++) {  
        	createMaxdHeap(data, data.size()-1-i,position);
        	swap(data,0,data.size()-1-i,position);
        	resultList.add(position[position.length-1-i]);
        }
        return resultList;
    }  
  
    public static void createMaxdHeap(List<Double> data, int lastIndex,int []position) {
    	if(lastIndex>data.size()-1){
    		System.out.println("这里不对id4");
    	}
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            // 保存当前正在判断的节点  
            int k = i;
            // 若当前节点的子节点存在  
            while (2 * k + 1 <= lastIndex) {  
                // biggerIndex总是记录较大节点的值,先赋值为当前判断节点的左子节点  
                int biggerIndex = 2 * k + 1;  
                if (biggerIndex < lastIndex) {  
                    // 若右子节点存在，否则此时biggerIndex应该等于 lastIndex  
                    if (data.get(biggerIndex) < data.get(biggerIndex + 1)) {  
                        // 若右子节点值比左子节点值大，则biggerIndex记录的是右子节点的值  
                        biggerIndex++;  
                    }  
                }  
                if (data.get(k) < data.get(biggerIndex)) {  
                    // 若当前节点值比子节点最大值小，则交换2者得值，交换后将biggerIndex值赋值给k  
                    swap(data, k, biggerIndex,position);
                    k = biggerIndex;
                } else {
                    break;
                }
            }
        }
    }  
  
    public static void print(int[] data) {  
        for (int i = 0; i < data.length; i++) {  
            System.out.print(data[i] + "\t");  
        }  
        System.out.println();  
    }  
}
