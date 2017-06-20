package sort;

import java.util.Random;
import java.util.Scanner;

public class SortTest {
	
	public static final int BUBBLE_SORT = 0;
	public static final int HEAP_SORT = 1;
	public static final int INSERTION_SORT = 2;
	public static final int MERGE_SORT = 3;
	public static final int QUICK_SORT = 4;
	public static final int RADIX_SORT = 5;
	public static final int SELECT_SORT = 6;
	public static final int SHELL_SORT = 7;
	
	private static String[] sortNameArr = {"冒泡排序(BubbleSort)", "堆排序(HeapSort)"
									, "插入排序(InsertionSort)", "归并排序(MergeSort)"
									, "快速排序(QuickSort)", "基数排序(RadixSort)"
									, "选择排序(SelectSort)", "希尔排序(ShellSort)"};

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in); 
        System.out.println("请输入测试数组长度-LENGTH："); 
        int length = sc.nextInt(); 
        System.out.println("请输入测试数据中最大值-MAXNUM："); 
        int maxnum = sc.nextInt(); 
       
       	// 随机生成待排序的数据集合
        int[] arr = new int[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) { 
        	arr[i] = random.nextInt(maxnum);
        }
        
        for(int i=0; i<8; i++) {
        	// 算法开始的时间
        	long beginTime = System.currentTimeMillis();
        	// 执行算法
        	sortTest(arr, length, maxnum, i);
        	// 算法运行的时间 = 算法结束的时间 - beginTime
        	long deffTime = System.currentTimeMillis() - beginTime;
        	System.out.println(sortNameArr[i] + " : " + deffTime); 
        }

	}

	private static void sortTest(int[] arr, int length, int maxnum, int sortType) {
		// TODO Auto-generated method stub
		switch (sortType) {
		case BUBBLE_SORT:
			BubbleSort.bubbleSort(arr);
			break;
		case HEAP_SORT:
			HeapSort.heapSort(arr);
			break;
		case INSERTION_SORT:
			InsertionSort.insertionSort(arr);
			break;
		case MERGE_SORT:
			MergeSort.mergeSort(arr, 0, arr.length-1);
			break;
		case QUICK_SORT:
			QuickSort.quickSort(arr, 0, arr.length-1);
			break;
		case RADIX_SORT:
			RadixSort.radixSortByAsc(arr);
			break;
		case SELECT_SORT:
			SelectSort.selectSort(arr);
			break;
		case SHELL_SORT:
			ShellSort.shellSort(arr);
			break;
		default:
			break;
		}
	}

}
