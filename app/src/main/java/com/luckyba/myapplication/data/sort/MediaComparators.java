package com.luckyba.myapplication.data.sort;

import androidx.annotation.NonNull;

import com.luckyba.myapplication.data.model.AlbumFile;
import com.luckyba.myapplication.ui.timeline.data.TimelineHeaderModel;
import com.luckyba.myapplication.util.NumericComparator;

import java.util.Comparator;


public class MediaComparators {

//    public static Comparator<Media> getComparator(AlbumSettings settings) {
//        return getComparator(settings.getSortingMode(), settings.getSortingOrder());
//    }

    public static Comparator<AlbumFile> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {
        return sortingOrder == SortingOrder.ASCENDING
                ? getComparator(sortingMode) : reverse(getComparator(sortingMode));
    }

    public static Comparator<TimelineHeaderModel> getTimelineComparator(@NonNull SortingOrder sortingOrder) {
        return sortingOrder.isAscending() ? getTimelineComparator() : reverse(getTimelineComparator());
    }

    public static Comparator<AlbumFile> getComparator(SortingMode sortingMode) {
        switch (sortingMode) {
            case NAME: return getNameComparator();
            case DATE: default: return getDateComparator();
            case SIZE: return getSizeComparator();
            case TYPE: return getTypeComparator();
            case NUMERIC: return getNumericComparator();
        }
    }

    private static <T> Comparator<T> reverse(Comparator<T> comparator) {
        return (o1, o2) -> comparator.compare(o2, o1);
    }

    private static Comparator<AlbumFile> getDateComparator() {
        return (f1, f2) -> (int) (f1.getModifiedDate()- (f2.getModifiedDate()));
    }

    private static Comparator<AlbumFile> getNameComparator() {
        return (f1, f2) -> f1.getPath().compareTo(f2.getPath());
    }

    private static Comparator<AlbumFile> getSizeComparator() {
        return (f1, f2) -> Long.compare(f1.getSize(), f2.getSize());
    }

    private static Comparator<AlbumFile> getTypeComparator() {
        return (f1, f2) -> f1.getMimeType().compareTo(f2.getMimeType());
    }

    private static Comparator<AlbumFile> getNumericComparator() {
        return (f1, f2) -> NumericComparator.filevercmp(f1.getPath(), f2.getPath());
    }

    private static Comparator<TimelineHeaderModel> getTimelineComparator() {
        return (t1, t2) -> t1.getDate().compareTo(t2.getDate());
    }
}
