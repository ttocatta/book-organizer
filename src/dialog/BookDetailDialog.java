package dialog;

import library.Book;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;

public class BookDetailDialog extends JDialog {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    private Book book;

    public BookDetailDialog(Frame parent, Book book) {
        super(parent, "Book Details - " + book.getTitle(), true);
        this.book = book;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(600, 500));
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left panel - Book cover (Optimized with ImageHandler)
        JPanel coverPanel = new JPanel(new BorderLayout());
        coverPanel.setPreferredSize(new Dimension(200, 300));

        JLabel coverLabel = new JLabel();
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Optimization: Use ImageHandler to scale dynamically and avoid UI lag
        ImageIcon coverIcon = service.ImageHandler.getScaledIcon(book.getImagePath(), 180, 270);
        if (coverIcon != null) {
            coverLabel.setIcon(coverIcon);
        } else {
            coverLabel.setText(createTextCover());
        }

        coverPanel.add(coverLabel, BorderLayout.CENTER);
        add(coverPanel, BorderLayout.WEST);

        // Right panel - Book details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        // Efficient row addition
        addDetailRow(detailsPanel, "Title:", book.getTitle());
        addDetailRow(detailsPanel, "Author:", book.getAuthor());
        addDetailRow(detailsPanel, "Genre:", book.getGenre());
        addDetailRow(detailsPanel, "Published:", String.valueOf(book.getPublishingDate()));
        addDetailRow(detailsPanel, "Date Added:", DATE_FORMAT.format(book.getDateAdded()));

        if (book.getDocumentPath() != null && !book.getDocumentPath().isEmpty()) {
            // Optimization: Use DocumentHandler logic for display/access
            addDetailRow(detailsPanel, "Document:", new File(book.getDocumentPath()).getName());
        }

        add(detailsPanel, BorderLayout.CENTER);

        // Bottom panel - Actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Optimization: Add a "Read" button if a document exists
        if (book.getDocumentPath() != null && !book.getDocumentPath().isEmpty()) {
            JButton readButton = new JButton("Open Document");
            readButton.addActionListener(e -> {
                try {
                    service.DocumentHandler.openDocument(book.getDocumentPath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error opening document: " + ex.getMessage());
                }
            });
            buttonPanel.add(readButton);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(labelComp.getFont().deriveFont(Font.BOLD, 14f));
        labelComp.setPreferredSize(new Dimension(100, 25));

        JLabel valueComp = new JLabel("<html><body style='width: 250px'>" + value + "</body></html>");
        valueComp.setFont(valueComp.getFont().deriveFont(14f));

        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
        panel.add(Box.createVerticalStrut(5));
    }

    private String createTextCover() {
        return "<html><center><br><br><b>" +
                book.getTitle() + "</b><br><br>by<br>" +
                book.getAuthor() + "<br><br>" +
                book.getPublishingDate() + "</center></html>";
    }
}
