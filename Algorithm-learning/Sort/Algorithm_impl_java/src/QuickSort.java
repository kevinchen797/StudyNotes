
public class QuickSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		quickSort(args, 0, args.length-1);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	/**
     * 快速排序
     *
     * @param args   待排序的目标数组
     * @param start  数组的起始索引
     * @param end    数组的结束索引
     */
	public static void quickSort(int[] args, int start, int end) {
		//当分治的元素大于1个的时候，才有意义
		if ( end - start > 1) {
            int mid = 0;
			mid = dividerAndChange(args, start, end);
			// 对左部分排序
			quickSort(args, start, mid);
			// 对右部分排序
			quickSort(args, mid + 1, end);
		}
	}

	/**
     * 拆分数组
     *
     * @param args   要拆分的数组
     * @param start 数组拆分的起始索引 （从0开始）
     * @param end   数组拆分的结束索引
     */
	public static int dividerAndChange(int[] args, int start, int end) {   
		//标准值
    	int pivot = args[start];
		while (start < end) {
			// 从右向左寻找，一直找到比参照值还小的数值，进行替换
			// 这里要注意，循环条件必须是 当后面的数 小于 参照值的时候
			// 我们才跳出这一层循环
			while (start < end && args[end] >= pivot)
				end--;

			if (start < end) {
				swap(args, start, end);
				start++;
			}

			// 从左向右寻找，一直找到比参照值还大的数组，进行替换
			while (start < end && args[start] < pivot)
				start++;

			if (start < end) {
				swap(args, end, start);
				end--;
			}
		}

		args[start] = pivot;
		return start;
	}

	/**
     * 交换数组中指定位置的两个元素
     *
     * @param args
     * @param fromIndex
     * @param toIndex
     */
	private static void swap(int[] args, int fromIndex, int toIndex) {
		args[fromIndex] = args[toIndex];
	}

}
