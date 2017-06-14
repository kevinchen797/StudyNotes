
public class ShellSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		shellSort(args);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	public static void shellSort(int[] arr) {
		int gap = 1, i, j, len = arr.length;
		int temp;
		while (gap < len / 3)
			gap = gap * 3 + 1; // <O(n^(3/2)) by Knuth,1973>: 1, 4, 13, 40, 121, ...
		for (; gap > 0; gap /= 3) {
			for (i = gap; i < len; i++) {
				temp = arr[i];
				for (j = i - gap; j >= 0 && arr[j] > temp; j -= gap)
					arr[j + gap] = arr[j];
				arr[j + gap] = temp;
			}
		}
	}
}
