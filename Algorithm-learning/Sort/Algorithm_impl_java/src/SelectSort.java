
public class SelectSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		selectSort(args);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	public static void selectSort(int[] args) {
        int len = args.length;
        for (int i = 0,k = 0; i < len; i++,k = i) {
            // 在这一层循环中找最小
            for (int j = i + 1; j < len; j++) {
                // 如果后面的元素比前面的小，那么就交换下标，每一趟都会选择出来一个最小值的下标
                if (args[k] > args[j]) k = j;
    		}
    
    		if (i != k) {
    			int tmp = args[i];
    			args[i] = args[k];
    			args[k] = tmp;
    		}
    	}
    }
}
