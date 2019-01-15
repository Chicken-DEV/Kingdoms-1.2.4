package com.songoda.kingdoms.utils;

import java.util.*;

public class Sort {
    private static int partition(int arr[], int left, int right) {
        int i = left, j = right;
        int tmp;
        int pivot = arr[(left + right) / 2];

        while (i <= j) {
            while (arr[i] < pivot)
                i++;
            while (arr[j] > pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        ;

        return i;
    }

    public static void quickSort(int arr[], int left, int right) {
        int index = partition(arr, left, right);
        if (left < index - 1)
            quickSort(arr, left, index - 1);
        if (index < right)
            quickSort(arr, index, right);
    }

    public static <K, V extends Comparable<V>> Map<K, V> sort(Map<K, V> map) {
        return sort(map, map.size());
    }

    public static <K, V extends Comparable<V>> Map<K, V> sort(Map<K, V> map, int limit) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        int index = 0;
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            if (index == limit)
                break;
            result.put(entry.getKey(), entry.getValue());

            index++;
        }
        return result;
    }

    public static <K, V extends Comparable<V>> Map<K, V> reverse(Map<K, V> map) {
        return reverse(map, map.size());
    }

    public static <K, V extends Comparable<V>> Map<K, V> reverse(Map<K, V> map, int limit) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        int index = 0;
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            if (index == limit)
                break;
            result.put(entry.getKey(), entry.getValue());

            index++;
        }
        return result;
    }
}
