package com.LsmFiServices.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FIDetails {
	public static void main(String[] args) {
//		findDuplicate();
//		findSecondHighest();
//		findPairsForGivenSum();
//		findSmallestNum();
//		findLengthOfGivenStrings();
//		findLargestNum();
//		reverseArray();
		sumOfAllElements();
	}

	public static void findDuplicate() {
		int[] arr = { 3, 6, 5, 4, 7, 5, 6, 8, 2, 9, 0 };
		Set<Integer> set = new HashSet<>();
		for (int i : arr) {
			if (set.contains(i)) {
				System.out.println(i + " is duplicate");
			} else {
				set.add(i);
			}
		}
	}

	public static void findSecondHighest() {
		int[] arr = { 3, 6, 5, 4, 5, 6, 2, 9, 0 };
		Arrays.sort(arr);
		System.out.println(Arrays.toString(arr));
		int secHighest = arr[arr.length - 2];
		System.out.println("secHighest->" + secHighest);
	}

	public static void findPairsForGivenSum() {
		int[] arr1 = { 3, 6, 5, 4, 9, 0, 1 };
		int sum = 7;
		List<String> li = new ArrayList<>();
		for (int i : arr1) {
			int outerEle = i;
			for (int k : arr1) {
				int innerEle = k;
				if (sum == outerEle + innerEle) {
					String pair = "[ " + outerEle + ", " + innerEle + "]";
					li.add(pair);
				}
			}
		}
//		System.out.println(li);

		int[] arr = { 3, 6, 5, 4, 9, 1 };
		int low = 0;
		int high = arr.length - 1;
		Arrays.sort(arr); // consider array is sorted
		while (low < high) {
			if (arr[low] + arr[high] > sum) {
				high--;
			} else if (arr[low] + arr[high] < sum) {
				low++;
			} else if (arr[low] + arr[high] == sum) {
				System.out.println("[ " + arr[low] + ", " + arr[high] + " ]");
				low++;
				high--;
			}
		}
	}

	public static void findSmallestNum() {
		int[] arr1 = { 3, 6, 5, 4, 9, 1, 1 };
		int smallest = 0;
		for (int i : arr1) {
			if (i < smallest) {
				smallest = i;
			} else {
				smallest = i;
			}
		}
		System.out.println("smallest using traditional-->" + smallest);
		int smallest1 = Arrays.stream(arr1).min().orElseThrow(()->new RuntimeException("An array is empty"));
		System.out.println("smallest using stream api-->" + smallest1);
	}

	public static void findLengthOfGivenStrings() {
		String[] ar = { "LSM", "SLICE", "UNICORE" };
		for(String s : ar) {
			System.out.println(s.length());
		}
		Arrays.stream(ar).mapToInt(j -> j.length()).forEach(System.out::println);
	}
	
	public static void findLargestNum() {
		int[] arr1 = { 3, 6, 5, 4, 9, 1, 1 };
		int largest = 0;
		for (int i : arr1) {
			if (i > largest) {
				largest = i;
			} 
		}
		System.out.println("largest using traditional-->" + largest);
		int largest1 = Arrays.stream(arr1).max().orElseThrow(()->new RuntimeException("An array is empty"));
		System.out.println("largest using stream api-->" + largest1);
	}
	
	public static void reverseArray() {
		int[] arr1 = { 3, 6, 5, 4, 9, 1, 1 };
		int [] newArr = new int[arr1.length];
		int temp = 0;
		for(int i = arr1.length-1; i>=0;i--) {
			newArr[temp] = arr1[i];
			temp++;
		}
		System.out.println(Arrays.toString(newArr));
		System.out.println(newArr.length);
	}
	
	public static void sumOfAllElements() {
		int sum = 0;
		int[] arr1 = { 3, 6, 5, 4, 9, 1, 1 };
		for(int i : arr1) {
			sum +=i; 
		}
		System.out.println(sum);
		
	}
}
