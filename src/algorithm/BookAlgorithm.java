package algorithm;

import library.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookAlgorithm {

    public enum SortField {
        TITLE, AUTHOR, GENRE, PUBLISHING_DATE, DATE_ADDED
    }

    public static void mergeSort(List<Book> books, SortField field, boolean ascending) {
        Comparator<Book> cmp = getComparator(field);

        if (!ascending) {
            cmp = cmp.reversed();
        }

        mergeSort(books, cmp);
    }

    private static Comparator<Book> getComparator(SortField field) {
        switch (field) {
            case AUTHOR:
                return Comparator.comparing(Book::getAuthor,
                        String.CASE_INSENSITIVE_ORDER);

            case GENRE:
                return Comparator.comparing(Book::getGenre,
                        String.CASE_INSENSITIVE_ORDER);

            case PUBLISHING_DATE:
                return Comparator.comparingInt(Book::getPublishingDate);

            case DATE_ADDED:
                return Comparator.comparing(Book::getDateAdded);

            case TITLE:
            default:
                return Comparator.comparing(Book::getTitle,
                        String.CASE_INSENSITIVE_ORDER);
        }
    }

    public static void mergeSort(List<Book> list, Comparator<Book> cmp) {
        if (list.size() <= 1) return;

        int mid = list.size() / 2;

        List<Book> left = new ArrayList<>(list.subList(0, mid));
        List<Book> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, cmp);
        mergeSort(right, cmp);

        merge(list, left, right, cmp);
    }

    private static void merge(List<Book> main,
                              List<Book> left,
                              List<Book> right,
                              Comparator<Book> cmp) {

        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) {
                main.set(k++, left.get(i++));
            } else {
                main.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) main.set(k++, left.get(i++));
        while (j < right.size()) main.set(k++, right.get(j++));
    }
}
