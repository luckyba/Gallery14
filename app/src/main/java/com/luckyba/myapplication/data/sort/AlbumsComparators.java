package com.luckyba.myapplication.data.sort;

import com.luckyba.myapplication.data.model.Album;
import com.luckyba.myapplication.util.NumericComparator;

import java.util.Comparator;

/**
 * Created by dnld on 26/04/16.
 */
public class AlbumsComparators {

    private static Comparator<Album> getComparator(SortingMode sortingMode, Comparator<Album> base) {
        switch (sortingMode) {
            case NAME:
                return getNameComparator(base);
            case SIZE:
                return getSizeComparator(base);
            case DATE: default:
                return getDateComparator(base);
            case NUMERIC:
                return getNumericComparator(base);
        }
    }

    public static Comparator<Album> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {

        Comparator<Album> comparator = getComparator(sortingMode, sortingOrder);

        return sortingOrder == SortingOrder.ASCENDING
                ? comparator : reverse(comparator);
    }

    private static Comparator<Album> reverse(Comparator<Album> comparator) {
        return (o1, o2) -> comparator.compare(o2, o1);
    }

    private static Comparator<Album> getDateComparator(Comparator<Album> base){
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return (int) (a1.getDateModified()-(a2.getDateModified()));
            return res;
        };
    }

    private static Comparator<Album> getNameComparator(Comparator<Album> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return a1.getName().toLowerCase().compareTo(a2.getName().toLowerCase());
            return res;
        };
    }

    private static Comparator<Album> getSizeComparator(Comparator<Album> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return a1.getCount() - a2.getCount();
            return res;
        };
    }

    private static Comparator<Album> getNumericComparator(Comparator<Album> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return NumericComparator.filevercmp(a1.getName().toLowerCase(), a2.getName().toLowerCase());
            return res;
        };
    }
}
