package ui;

import library.Book;
import util.CoverImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BookTablePanel extends JScrollPane {
    private final JTable bookTable;
    private DefaultTableModel tableModel;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Runnable onViewDetails;
    private Runnable onReadDocument;
    private Runnable onEdit;
    private Runnable onRemove;

    private boolean isBigPictureMode = true;
    private final JButton toggleViewButton;
    private List<Book> currentBooks; // keep reference to last refreshed books

    private Color rowSelectionColor = new Color(167, 199, 217, 184); // selected row highlight
    private Color headerColor = new Color(37, 131, 206); // column header background

    public BookTablePanel() {
        // Initialize table model
        setupTableModel();


        // Create JTable with custom row selection color
        bookTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(rowSelectionColor); // selected row color
                    c.setForeground(Color.BLACK);       // ensure text is visible
                } else {
                    c.setBackground(Color.WHITE);       // normal row color
                    c.setForeground(Color.BLACK);       // normal text color
                }
                return c;
            }
        };
        bookTable.setRowHeight(80);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.getTableHeader().setResizingAllowed(false);

        // Header renderer
        bookTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(headerColor);
                label.setForeground(Color.WHITE);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                return label;
            }
        });
        bookTable.getTableHeader().setPreferredSize(new Dimension(bookTable.getWidth(), 30));

        setColumnWidths();
        setupPopupMenu();

        setViewportView(bookTable);
        setupStatusCheckColumn();


        toggleViewButton = new JButton("Simple View");
        toggleViewButton.addActionListener(e -> toggleViewMode());
    }

    // setup model for table
    private void setupTableModel() {
        String[] columnsBigPicture = {"Cover", "Title", "Author", "Genre", "Year", "Date Added", "Book File Status"};
        String[] columnsSimple = {"Title", "Author", "Genre", "Year", "Date Added", "Book File Status"};

        tableModel = new DefaultTableModel(isBigPictureMode ? columnsBigPicture : columnsSimple, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class<?> getColumnClass(int column) {
                if (isBigPictureMode && column == 0) return ImageIcon.class;

                int statusCol = isBigPictureMode ? 6 : 5;
                if (column == statusCol) return Boolean.class;

                return String.class;
            }

        };
    }
    private void setupStatusCheckColumn() {

        int statusCol = isBigPictureMode ? 6 : 5;

        bookTable.getColumnModel().getColumn(statusCol)
                .setCellRenderer(new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {

                        JCheckBox box = new JCheckBox();
                        box.setHorizontalAlignment(JLabel.CENTER);

                        boolean checked = Boolean.TRUE.equals(value);
                        box.setSelected(checked);

                        if (!isSelected) {
                            box.setBackground(checked
                                    ? new Color(144, 238, 144)
                                    : new Color(255, 182, 193));
                        }

                        return box;
                    }
                });
    }


    // table size
    private void setColumnWidths() {
        if (bookTable.getColumnModel().getColumnCount() == 0) return;

        if (isBigPictureMode) {
            int[] widths = {60, 200, 120, 110, 50, 80, 50};
            for (int i = 0; i < widths.length; i++)
                bookTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        } else {
            int[] widths = {200, 120, 110, 50, 80, 50};
            for (int i = 0; i < widths.length; i++)
                bookTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    //Pop up Menu
    private void setupPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Full Details");
        viewItem.addActionListener(e -> { if (onViewDetails != null) onViewDetails.run(); });

        JMenuItem readItem = new JMenuItem("Read Document");
        readItem.addActionListener(e -> { if (onReadDocument != null) onReadDocument.run(); });

        JMenuItem editItem = new JMenuItem("Edit Book");
        editItem.addActionListener(e -> { if (onEdit != null) onEdit.run(); });

        JMenuItem removeItem = new JMenuItem("Remove Book");
        removeItem.addActionListener(e -> { if (onRemove != null) onRemove.run(); });

        JMenuItem toggleViewMenuItem = new JMenuItem(isBigPictureMode ? "Switch to Simple View" : "Switch to Big Picture View");
        toggleViewMenuItem.addActionListener(e -> {
            toggleViewMode();
            toggleViewMenuItem.setText(isBigPictureMode ? "Switch to Simple View" : "Switch to Big Picture View");
        });

        popupMenu.add(viewItem);
        popupMenu.add(readItem);
        popupMenu.addSeparator();
        popupMenu.add(editItem);
        popupMenu.add(removeItem);
        popupMenu.addSeparator();
        popupMenu.add(toggleViewMenuItem);

        bookTable.addMouseListener(new MouseAdapter() {
            private void showPopup(MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    bookTable.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) showPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) showPopup(e); }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && onViewDetails != null) onViewDetails.run();
            }
        });
    }

    public void refreshTable(List<Book> books) {
        this.currentBooks = books;
        tableModel.setRowCount(0);

        for (Book book : books) {
            boolean docStatus = book.getDocumentPath() != null
                    && !book.getDocumentPath().isEmpty();


            if (isBigPictureMode) {
                ImageIcon coverIcon = CoverImageUtil.createCoverThumbnail(book);
                tableModel.addRow(new Object[]{
                        coverIcon,
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublishingDate(),
                        DATE_FORMAT.format(book.getDateAdded()),
                        docStatus
                });

            } else {
                tableModel.addRow(new Object[]{
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublishingDate(),
                        DATE_FORMAT.format(book.getDateAdded()),
                        docStatus
                });

            }
        }

        setColumnWidths();
    }

    public void toggleViewMode() {
        isBigPictureMode = !isBigPictureMode;
        toggleViewButton.setText(isBigPictureMode ? "Simple View" : "Big Picture");

        setupTableModel();
        bookTable.setModel(tableModel);

        setupStatusCheckColumn();

        bookTable.setRowHeight(isBigPictureMode ? 80 : 30);


        setColumnWidths();
        if (currentBooks != null) refreshTable(currentBooks);

        revalidate();
        repaint();
    }

    public int getSelectedRow() { return bookTable.getSelectedRow(); }
    public JButton getToggleViewButton() { return toggleViewButton; }

    public void setOnViewDetails(Runnable handler) { this.onViewDetails = handler; }
    public void setOnReadDocument(Runnable handler) { this.onReadDocument = handler; }
    public void setOnEdit(Runnable handler) { this.onEdit = handler; }
    public void setOnRemove(Runnable handler) { this.onRemove = handler; }

    public boolean isBigPictureMode() { return isBigPictureMode; }

}
