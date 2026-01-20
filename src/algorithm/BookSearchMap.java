package algorithm;

import library.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookSearchMap {

    private HashMap<String, List<Book>> bookMap;

    public BookSearchMap(List<Book> books) {
        bookMap = new HashMap<>();

        for (Book b : books) {
            // store under multiple keys for easy searching
            addToMap(b.getTitle(), b);
            addToMap(b.getAuthor(), b);
            addToMap(b.getGenre(), b);
            addToMap(String.valueOf(b.getPublishingDate()), b);
            addToMap(b.getDateAdded().toString(), b);
        }
    }

    private void addToMap(String key, Book book) {
        String lower = key.toLowerCase();
        bookMap.computeIfAbsent(lower, k -> new ArrayList<>()).add(book);
    }

    // Inside BookSearchMap.java
    public List<Book> search(String query, BookAlgorithm.SortField sortField, boolean ascending) {
        final String lowerQuery = query.toLowerCase(); // Normalize once

        // Use a HashSet to avoid O(n^2) duplicate checks
        Set<Book> uniqueResults = bookMap.entrySet().stream()
                .filter(entry -> entry.getKey().contains(lowerQuery))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toSet()); // HashSet handles uniqueness in O(1)

        List<Book> sortedList = new ArrayList<>(uniqueResults);

        if (sortField != null) {
            BookAlgorithm.mergeSort(sortedList, sortField, ascending);
        }

        return sortedList;
    }


}
