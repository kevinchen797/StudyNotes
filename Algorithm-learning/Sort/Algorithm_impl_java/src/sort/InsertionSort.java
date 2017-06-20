package sort;

public class InsertionSort {

	public static void main(String[] strs) {
		// TODO Auto-generated method stub
		int[] args = {8, 2, 1, 4,6, 3, 5, 9, 6,11,19,13,55,67,32};
		insertionSort(args);
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + ", ");
		}
		
	}
	
	public static void insertionSort(int[] arr) {
    	for( int i=0; i<arr.length-1; i++ ) {	
        	for( int j=i+1; j>0; j-- ) {
            	if( arr[j-1] <= arr[j] )
                	break;
            	int temp = arr[j];
            	arr[j] = arr[j-1];
            	arr[j-1] = temp;
        	}
    	}
	}
}
