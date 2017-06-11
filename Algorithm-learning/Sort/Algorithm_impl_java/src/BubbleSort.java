
public class BubbleSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		bubbleSort(args);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	public static void bubbleSort(int[] args) {
    	//第一层循环从数组的最后往前遍历
		for (int i = args.length - 1; i > 0 ; --i) {
            //这里循环的上界是 i - 1，在这里体现出 “将每一趟排序选出来的最大的数从sorted中移除”
			for (int j = 0; j < i; j++) {
                //保证在相邻的两个数中比较选出最大的并且进行交换(冒泡过程)
				if (args[j] > args[j+1]) {
					int temp = args[j];
					args[j] = args[j+1];
					args[j+1] = temp;
				}
			}
		}
    }
}
