package com.assignment.smartloading.algorithm;

import com.assignment.smartloading.model.Product;
import java.util.List;

public class ProductQuickSort {

    //public api
    public static void quickSortByPriceDesc(List<Product> products) {
        if (products == null || products.size() <= 1) return;
        quickSort(products, 0, products.size() - 1);
    }

    private static void quickSort(List<Product> products, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(products, low, high);
            quickSort(products, low, pivotIndex - 1);
            quickSort(products, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Product> products, int low, int high) {
        double pivot = products.get(high).getPrice(); // pivot = last element price
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (products.get(j).getPrice() >= pivot) {
                i++;
                swap(products, i, j);
            }
        }

        swap(products, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Product> products, int i, int j) {
        Product temp = products.get(i);
        products.set(i, products.get(j));
        products.set(j, temp);
    }
}
