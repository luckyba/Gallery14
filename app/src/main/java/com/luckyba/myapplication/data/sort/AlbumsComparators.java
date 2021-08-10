package com.luckyba.myapplication.data.sort;

import com.luckyba.myapplication.data.model.AlbumFolder;
import com.luckyba.myapplication.util.NumericComparator;

import java.util.Comparator;

/**
 * Created by dnld on 26/04/16.
 */
public class AlbumsComparators {

    private static Comparator<AlbumFolder> getComparator(SortingMode sortingMode, Comparator<AlbumFolder> base) {
        switch (sortingMode) {
            case NAME:
                return getNameComparator(base);
            case SIZE:
                return getSizeComparator(base);
//            case DATE: default:
//                return getDateComparator(base);
            case NUMERIC:
                return getNumericComparator(base);
            default:
                return getNameComparator(base);
        }
    }

    public static Comparator<AlbumFolder> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {

        Comparator<AlbumFolder> comparator = getComparator(sortingMode, sortingOrder);

        return sortingOrder == SortingOrder.ASCENDING
                ? comparator : reverse(comparator);
    }

    private static Comparator<AlbumFolder> reverse(Comparator<AlbumFolder> comparator) {
        return (o1, o2) -> comparator.compare(o2, o1);
    }

//    private static Comparator<AlbumFolder> getDateComparator(Comparator<AlbumFolder> base){
//        return (a1, a2) -> {
//            int res = base.compare(a1, a2);
//            if (res == 0)
//                return (int) (a1.getDateModified()-(a2.getDateModified()));
//            return res;
//        };
//    }

    private static Comparator<AlbumFolder> getNameComparator(Comparator<AlbumFolder> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return a1.getName().toLowerCase().compareTo(a2.getName().toLowerCase());
            return res;
        };
    }

    private static Comparator<AlbumFolder> getSizeComparator(Comparator<AlbumFolder> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return a1.getAlbumFiles().size() - a2.getAlbumFiles().size();
            return res;
        };
    }

    private static Comparator<AlbumFolder> getNumericComparator(Comparator<AlbumFolder> base) {
        return (a1, a2) -> {
            int res = base.compare(a1, a2);
            if (res == 0)
                return NumericComparator.filevercmp(a1.getName().toLowerCase(), a2.getName().toLowerCase());
            return res;
        };
    }
}
