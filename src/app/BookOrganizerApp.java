package app;

import library.Book;
import library.BookLibrary;
import dialog.BookDetailDialog;
import dialog.BookDialog;
import service.BookCSVHandler;
import ui.BookTablePanel;
import dialog.TutorialStep;
import dialog.HowToUseDialog;



import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookOrganizerApp extends JFrame {

    // COLOR
    private static final Color BACKGROUND_COLOR = new Color(0xE8, 0xEB, 0xEF);
    private static final Color PANEL_COLOR = new Color(0xF5, 0xF7, 0xF9);
    private static final Color BORDER_COLOR = new Color(0xD0, 0xD4, 0xD8);
    private static final Color TEXT_COLOR = new Color(0x33, 0x33, 0x33);
    private static final Color GREEN_BUTTON = new Color(0x4C, 0xAF, 0x50);

    // DATA
    private BookLibrary library;
    private List<Book> filteredBooks = new ArrayList<>();
    private List<Book> pagedBooks = new ArrayList<>();

    private int currentPage = 1;
    private final int rowsPerPage = 25; // Updated to 25 rows per page
    private boolean ascending = true;
    private String currentSortField = "Title";
    private String selectedField = "Title";

    // UI
    private JTextField searchField;
    private JLabel statusLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel pageNumbersPanel;
    private BookTablePanel bookTablePanel;




    // CONSTRUCTOR
    public BookOrganizerApp() {
        library = new BookLibrary();

        // Init table panel first (needed for menu toggle)
        bookTablePanel = new BookTablePanel();
        bookTablePanel.setOnViewDetails(this::viewBookDetails);
        bookTablePanel.setOnEdit(this::editBook);
        bookTablePanel.setOnRemove(this::removeBook);
        bookTablePanel.setOnReadDocument(this::readDocument);

        initUI();
        loadCSVOnLaunch();
        applyFiltersAndSort();
    }

    private void loadCSVOnLaunch() {
        try {
            BookCSVHandler.initCSV();
            library.setBooks(BookCSVHandler.loadFromCSV());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }



    // UI SETUP
    private void initUI() {
        setTitle("Book Organizer");
        setSize(1200, 675);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/app_icon.png"));
        setIconImage(icon.getImage());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(createToolbar(), BorderLayout.NORTH);
        mainPanel.add(createTableSection(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBackground(PANEL_COLOR);
        toolbar.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        toolbar.setLayout(new BorderLayout());

        // LEFT SIDE
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right padding

        JButton menuBtn = createMenuButton();
        JButton addBtn = createButton("+ Add Book", GREEN_BUTTON, TEXT_COLOR);
        JButton editBtn = createButton("Edit", Color.WHITE, TEXT_COLOR);
        JButton deleteBtn = createButton("Delete", Color.WHITE, TEXT_COLOR);

        addBtn.addActionListener(e -> addBook());
        editBtn.addActionListener(e -> {
            if (bookTablePanel.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else editBook();
        });
        deleteBtn.addActionListener(e -> {
            if (bookTablePanel.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else removeBook();
        });

        left.add(menuBtn);
        left.add(Box.createHorizontalStrut(5));
        left.add(addBtn);
        left.add(Box.createHorizontalStrut(5));
        left.add(editBtn);
        left.add(Box.createHorizontalStrut(5));
        left.add(deleteBtn);

        // RIGHT SIDE
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right padding

        searchField = new JTextField("Search book here...");
        searchField.setPreferredSize(new Dimension(250, 28));
        searchField.setMaximumSize(new Dimension(250, 28));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search book here...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search book here...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSort(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSort(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSort(); }
        });

        JButton sortBtn = createSortButton();

        right.add(Box.createHorizontalGlue());
        right.add(searchField);
        right.add(Box.createHorizontalStrut(5));
        right.add(sortBtn);

        // WRAPPER
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(left, BorderLayout.WEST);
        wrapper.add(right, BorderLayout.EAST);

        toolbar.add(wrapper, BorderLayout.CENTER);
        return toolbar;
    }

    //  MENU
    private JButton createMenuButton() {
        JButton menuBtn = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/menu.png"));
            int size = 28;
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            menuBtn.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            menuBtn.setText("Menu");
        }

        menuBtn.setPreferredSize(new Dimension(36, 36)); // square clickable area
        menuBtn.setContentAreaFilled(false);
        menuBtn.setBorderPainted(false);
        menuBtn.setFocusPainted(false);
        menuBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu menu = new JPopupMenu();

        JMenuItem saveItem = new JMenuItem("Save to CSV");
        saveItem.addActionListener(e -> saveCSV());
        JMenuItem loadItem = new JMenuItem("Load from CSV");
        loadItem.addActionListener(e -> loadCSV());
        menu.add(saveItem);
        menu.add(loadItem);
        menu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About / Credit");
        aboutItem.addActionListener(e -> showAbout());
        menu.add(aboutItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);

        JMenuItem toggleViewItem = new JMenuItem("Switch to Simple View");
        toggleViewItem.addActionListener(e -> {
            if (bookTablePanel != null) {
                bookTablePanel.toggleViewMode();
                toggleViewItem.setText(bookTablePanel.isBigPictureMode() ? "Switch to Simple View" : "Switch to Big Picture View");
            }
        });
        menu.addSeparator();
        menu.add(toggleViewItem);

        menuBtn.addActionListener(e -> menu.show(menuBtn, 0, menuBtn.getHeight()));
        return menuBtn;
    }

    public void showAbout() {
        JOptionPane.showMessageDialog(this ,"Developed by: \n \n" +
                "BSCpE 2B (2025-2026) \n" + "\n" +
                "Alviar \n" +
                "Lambojon  \n" +
                "Melchor \n \n","About",JOptionPane.INFORMATION_MESSAGE);
    }

    // CSV HANDLING
    private void saveCSV() {
        try {
            BookCSVHandler.saveToCSV(library.getBooks());
            JOptionPane.showMessageDialog(this, "Books saved to CSV File! \n" + "File Location (C:/BookOrganizer/book.csv)");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadCSV() {
        try {
            library.setBooks(BookCSVHandler.loadFromCSV());
            applyFiltersAndSort();
            JOptionPane.showMessageDialog(this, "Books loaded from CSV FIle! \n" + "File Location (C:/BookOrganizer/book.csv)");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // SORT BUTTON
    private JButton createSortButton() {
        JButton sortButton = new JButton("Sort");
        JPopupMenu sortMenu = new JPopupMenu();

        String[] fields = {"Title", "Author", "Genre", "Publishing Date"};
        ButtonGroup fieldGroup = new ButtonGroup();
        for (String field : fields) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(field);
            item.setSelected(field.equals(selectedField));
            item.addActionListener(e -> {
                selectedField = field;
                currentSortField = field;
                applyFiltersAndSort();
            });
            fieldGroup.add(item);
            sortMenu.add(item);
        }

        sortMenu.addSeparator();

        String[] orders = {"Ascending", "Descending"};
        ButtonGroup orderGroup = new ButtonGroup();
        for (String order : orders) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(order);
            item.setSelected(order.equals(ascending ? "Ascending" : "Descending"));
            item.addActionListener(e -> {
                ascending = order.equals("Ascending");
                applyFiltersAndSort();
            });
            orderGroup.add(item);
            sortMenu.add(item);
        }

        sortButton.addActionListener(e -> sortMenu.show(sortButton, 0, sortButton.getHeight()));
        return sortButton;
    }

    // TABLE
    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        panel.add(bookTablePanel, BorderLayout.CENTER);
        return panel;
    }

    

    // BOTTOM
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(PANEL_COLOR);
        bottom.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        statusLabel = new JLabel("0 books");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        bottom.add(statusLabel, BorderLayout.WEST);

        // ===== HOW TO USE LINK =====
        JLabel howToUse = new JLabel("<html><u>How to use?</u></html>");
        howToUse.setForeground(Color.BLUE);
        howToUse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        howToUse.setHorizontalAlignment(SwingConstants.CENTER);

        howToUse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHowToUse();
            }
        });

        bottom.add(howToUse, BorderLayout.CENTER);

        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        pagination.setOpaque(false);

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        pageNumbersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        pageNumbersPanel.setOpaque(false);

        prevButton.addActionListener(e -> { currentPage--; updatePagination(); });
        nextButton.addActionListener(e -> { currentPage++; updatePagination(); });

        pagination.add(prevButton);
        pagination.add(pageNumbersPanel);
        pagination.add(nextButton);
        bottom.add(pagination, BorderLayout.EAST);

        bottom.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));

        return bottom;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        return btn;
    }

    private void applyFiltersAndSort() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("search book here...")) {
            query = "";
        }

        final String finalQuery = query;

        filteredBooks = library.getBooks().stream()
                .filter(b -> finalQuery.isEmpty() ||
                        b.getTitle().toLowerCase().contains(finalQuery) ||
                        b.getAuthor().toLowerCase().contains(finalQuery) ||
                        b.getGenre().toLowerCase().contains(finalQuery) ||
                        String.valueOf(b.getPublishingDate()).contains(finalQuery))
                .collect(Collectors.toList());

        algorithm.BookAlgorithm.SortField field = algorithm.BookAlgorithm.SortField
                .valueOf(selectedField.toUpperCase().replace(" ", "_"));
        algorithm.BookAlgorithm.mergeSort(filteredBooks, field, ascending);

        currentPage = 1; // Reset to page 1 on search/sort
        updatePagination();
    }



    // PAGE
    private void updatePagination() {
        int total = filteredBooks.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / rowsPerPage));
        currentPage = Math.max(1, Math.min(currentPage, totalPages));

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, total);

        // Update the list of books to display
        pagedBooks = (total > 0) ? filteredBooks.subList(start, end) : new ArrayList<>();

        // PRIORITY: Refresh the table data first so the user sees the books instantly
        bookTablePanel.refreshTable(pagedBooks);

        // OFF-LOAD: Rebuilding the page buttons is moved to the next UI cycle to prevent stutter
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(total + " books total | Page " + currentPage + " of " + totalPages);
            pageNumbersPanel.removeAll();

            // Smart button builder: only shows a few numbers if there are too many pages
            if (totalPages <= 7) {
                for (int i = 1; i <= totalPages; i++) addPageButton(i);
            } else {
                addPageButton(1);
                if (currentPage > 4) pageNumbersPanel.add(new JLabel("..."));

                int startRange = Math.max(2, currentPage - 1);
                int endRange = Math.min(totalPages - 1, currentPage + 1);
                for (int i = startRange; i <= endRange; i++) addPageButton(i);

                if (currentPage < totalPages - 3) pageNumbersPanel.add(new JLabel("..."));
                addPageButton(totalPages);
            }

            prevButton.setEnabled(currentPage > 1);
            nextButton.setEnabled(currentPage < totalPages);
            pageNumbersPanel.revalidate();
            pageNumbersPanel.repaint();
        });
    }

    private void addPageButton(int pageNumber) {
        JButton btn = new JButton(String.valueOf(pageNumber));
        btn.setEnabled(pageNumber != currentPage);
        btn.addActionListener(e -> {
            currentPage = pageNumber;
            updatePagination();
        });
        pageNumbersPanel.add(btn);
    }


    private Book getSelectedBook() {
        int row = bookTablePanel.getSelectedRow();
        // Safety check to ensure the row index is within the current page list
        if (row >= 0 && row < pagedBooks.size()) {
            return pagedBooks.get(row);
        }
        return null;
    }

    private void addBook() {
        BookDialog dialog = new BookDialog(this, "Add Book", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            library.addBook(dialog.getBook());
            applyFiltersAndSort();
        }

    }

    private void editBook() {
        Book book = getSelectedBook();
        if (book == null) return;
        BookDialog dialog = new BookDialog(this, "Edit Book", book);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) applyFiltersAndSort();
    }

    private void removeBook() {
        Book book = getSelectedBook();
        if (book == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + book.getTitle() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            library.removeBook(book);
            applyFiltersAndSort();
        }
    }

    private void viewBookDetails() {
        Book book = getSelectedBook();
        if (book != null) new BookDetailDialog(this, book).setVisible(true);
    }


    private void readDocument() {
        Book book = getSelectedBook();
        if (book != null && book.getDocumentPath() != null) {
            try {
                service.DocumentHandler.openDocument(book.getDocumentPath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
            }
        }
    }

    private void showHowToUse() {

        List<TutorialStep> steps = List.of(

                new TutorialStep(
                        "Adding a Book",
                        "Clicking the add icon will result in a window popping up requesting about the information of book the use want to add.",
                        new ImageIcon(getClass().getResource("/resources/tutorial_add.png"))
                ),

                new TutorialStep(
                        "Deleting",
                        "Selecting a book and clicking the delete button will open a “Delete Book” confirmation window. ",
                        new ImageIcon(getClass().getResource("/resources/tutorial_delete.png"))
                ),

                new TutorialStep(
                        "Editing",
                        "When you select a book and click the edit button, " +
                                "another new window will pop-up asking what particular info you want to change about a book.",
                        new ImageIcon(getClass().getResource("/resources/tutorial_edit.png"))
                ),

                new TutorialStep(
                        "Menu",
                        "If the user click the 3 dash line or Menu in Upper Left, " +
                                "the user will be presented with other function the program offers, from saving & loading to CSV, " +
                                "a credit panel, table display switch and an exit button",
                        new ImageIcon(getClass().getResource("/resources/tutorial_menu.png"))
                ),

                new TutorialStep(
                        "CSV Save",
                        "If the user saves the CSV File in the Menu panel, " +
                                "a window telling the action was done will appear." + "\n" + "\n" +
                                "(The CSV Location is in: C:/BookOrgnizer/book.csv",
                        new ImageIcon(getClass().getResource("/resources/tutorial_CSVSave.png"))
                ),

                new TutorialStep(
                        "CSV Load",
                        "If the user saves or loads the CSV File in the Menu panel, " +
                                "a window telling the action was done will appear." + "\n" + "\n" +
                                "(The CSV Location is in: C:/BookOrgnizer/book.csv",
                        new ImageIcon(getClass().getResource("/resources/tutorial_CSVLoad.png"))
                ),

                new TutorialStep(
                        "About/Credits",
                        "Clicking the “About” button in the Menu panel " +
                                "will result into a window with the book organizer developers name " + "\n" + "\n" +
                                "Alviar" + "\n" +
                                "Lambojon" +"\n"  +
                                "Melchor",
                        new ImageIcon(getClass().getResource("/resources/tutorial_creditstemp.png"))
                ),

                new TutorialStep(
                        "Searching",
                        "In the main window the user can use the search bar to search " +
                                "for a specific book, simply by typing a corresponding Title, Author, Genre or Publishing Year. ",
                        new ImageIcon(getClass().getResource("/resources/tutorial_searchbar.png"))
                ),

                new TutorialStep(
                        "Sorting",
                        "On the main window, located in the upper-right corner, is the Sort button. " +
                                "Clicking it allows the user to sort the table/books by ascending, descending, title, author, or publishing date.",
                        new ImageIcon(getClass().getResource("/resources/tutorial_sort.png"))
                ),

                new TutorialStep(
                        "Big Picture View",
                        "In the Menu panel, switching to ‘Simple’ or ‘Big Picture’ view mode will cause the book table to switch its viewing mode" +
                                "NOTE: Big Picture View includes a thumbnail or the book cover for a book",
                        new ImageIcon(getClass().getResource("/resources/tutorial_viewbig.png"))
                ),

                new TutorialStep(
                        "Simple View",
                        "In the Menu panel, switching to ‘Simple’ or ‘Big Picture’ view mode will cause the book table to switch its viewing mode",
                        new ImageIcon(getClass().getResource("/resources/tutorial_viewsmall.png"))
                ),

                new TutorialStep(
                        "Right Click",
                        "Right-clicking a book will display a menu with the following options: " +
                                "View Full Mode, Read Document, Edit, Remove Book, or Switch Viewing Mode.",
                        new ImageIcon(getClass().getResource("/resources/tutorial_rightclk.png"))
                ),

                new TutorialStep(
                        "View Full Deatils",
                        "If the user clicks the “View Full Details” button , a large window will be displayed with all of the book’s information.\n",
                        new ImageIcon(getClass().getResource("/resources/tutorial_fulldetail.png"))
                ),

                new TutorialStep(
                        "Read Book",
                        "Clicking the “Read Document” will result into the program redirect/locate " +
                                "the user to the file embedded to the book and open it for the user\n",
                        new ImageIcon(getClass().getResource("/resources/tutorial_readbook.png"))
                ),

                new TutorialStep(
                        " Bottom Panel ",
                                " At the bottom of the main window, you can view the books currently in the program. " +
                                "You can also navigate between pages using the Next and Previous buttons, " +
                                "and see the total number of pages in the program.\n.\n",
                        new ImageIcon(getClass().getResource("/resources/tutorial_pagination.png"))
                )


        );

        new HowToUseDialog(this, steps).setVisible(true);
    }

    public static void main(String[] args) { // MAIN
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new BookOrganizerApp().setVisible(true));
    }
}
