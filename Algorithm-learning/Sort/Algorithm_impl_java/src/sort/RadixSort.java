package sort;
import java.util.ArrayList;
import java.util.List;

public class RadixSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		radixSortByAsc(args);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	/**
	 * 基数排序法
	 * 升序排列
	 * @param data
	 */
	public static  void radixSortByAsc(int[] data) {
	    /** step1:确定排序的趟数*/     
	    int max=data[0];     
	    for(int i=1;i<data.length;i++) {     
	        if(data[i]>max) {
	            max=data[i];     
	        }     
	    }   
	    /** step2:判断位数*/
	    int digit = 0;       
	    while(max > 0) {     
	        max/=10;     
	    digit++;     
	    }  
	    /**初始化一个二维数组，相当于二维数组，可以把重复的存进去*/
	    List<ArrayList<Integer>> temp = new ArrayList<>();
	    for(int i = 0;i < 10;i++) {
	        temp.add(new ArrayList<Integer>());
	    }
	    /**开始合并收集*/
	    for(int i = 0; i < digit; i++) {
	        /** 对每一位进行排序 */
	        for(int j = 0; j < data.length; j++) {
	            /**求每一个数的第i位的数，然后存到相对应的数组中*/
	            int digitInx = data[j]%(int)Math.pow(10, i + 1)/(int)Math.pow(10, i);
	            ArrayList<Integer> tempInside = temp.get(digitInx);
	            tempInside.add(data[j]);
	            temp.set(digitInx,tempInside );
	        }
	        /**收集数组元素*/
	        int count = 0;
	        for(int k = 0;k < 10;k++) {
	            for(;temp.get(k).size()>0;count++) {
	                ArrayList<Integer> temp2 = temp.get(k);
	                data[count] = temp2.get(0);
	                temp2.remove(0);
	            }
	        }
	    }
	}
	
	/**
	 * 基数排序法
	 * 降序排列
	 * @param data
	 */
	public static void radixSortByDesc(int[] data) {

	    /** step1:确定排序的趟数*/     
	    int max=data[0];     
	    for(int i=1;i<data.length;i++) {     
	        if(data[i]>max) {     
	            max=data[i];     
	        }     

	    }   
	    /** step2:判断位数*/
	    int digit = 0;       
	    while(max > 0) {     
	        max/=10;     
	        digit++;     
	    }  
	    /**初始化一个二维数组，相当于二维数组，可以把重复的存进去*/
	    List<ArrayList<Integer>> temp = new ArrayList<>();
	    for(int i = 0;i < 10;i++) {
	        temp.add(new ArrayList<Integer>());
	    }
	    /**开始合并收集*/
	    for(int i = 0; i < digit; i++) {
	        /** 对每一位进行排序 */
	        for(int j = 0; j < data.length; j++) {
	            /**求每一个数的第i位的数，然后存到相对应的数组中*/
	            int digitInx = data[j]%(int)Math.pow(10, i + 1)/(int)Math.pow(10, i);
	            ArrayList<Integer> tempInside = temp.get(digitInx);
	            tempInside.add(data[j]);
	            temp.set(digitInx,tempInside );
	        }
	        int count = data.length - 1;
	        for(int k = 0;k < 10;k++) { 
	            for(;temp.get(k).size()>0;count--) {
	                ArrayList<Integer> temp2 = temp.get(k);
	                data[count] = temp2.get(0);
	                temp2.remove(0);
	            }
	        }
	    }
	}
}
