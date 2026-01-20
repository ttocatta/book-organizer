package service;

import java.util.regex.Pattern;

public class BookValidator {

    private static final Pattern STARTS_WITH_SPECIAL =
            Pattern.compile("^[^a-zA-Z0-9].*");

    public static void validate(String title, String author, String genre, int year) {

        if (title == null || author == null || genre == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (title.trim().isEmpty() || author.trim().isEmpty() || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (year < 1440) {
            throw new IllegalArgumentException("Publishing date must be 1440 or later.");
        }

        if (title.length() < 2 || STARTS_WITH_SPECIAL.matcher(title).matches()) {
            throw new IllegalArgumentException(
                    "Title must be at least 2 characters and not start with a special character."
            );
        }

        if (author.length() < 2 || STARTS_WITH_SPECIAL.matcher(author).matches()) {
            throw new IllegalArgumentException(
                    "Author must be at least 2 characters and not start with a special character."
            );
        }
    }
}
