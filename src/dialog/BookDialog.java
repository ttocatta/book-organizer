package dialog;

import library.Book;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class BookDialog extends JDialog {
    // UPDATED: Specific paths for C: drive
    private static final String BASE_PATH = "C:/BookOrganizer";
    private static final String COVER_FOLDER = BASE_PATH + "/BooksCover";
    private static final String DOCUMENT_FOLDER = BASE_PATH + "/BooksFile";

    private static final String[] GENRES = {
            "Fiction", "Mystery / Thriller", "Science Fiction", "Fantasy",
            "Romance", "Historical", "Horror", "Biography / Autobiography",
            "Non-Fiction", "Classics", "Academic & Professional"
    };

    private JTextField titleField, authorField;
    private JComboBox<String> genreComboBox;
    private JSpinner yearSpinner;
    private JLabel imagePreviewLabel, documentLabel;
    private String imagePath = "", documentPath = "";
    private boolean confirmed = false;
    private Book editingBook;

    public BookDialog(Frame parent, String title, Book existingBook) {
        super(parent, title, true);
        this.editingBook = existingBook;
        initComponents(existingBook);
        pack();
        setLocationRelativeTo(parent);
    }



    /**
     * DUPLICATION LOGIC:
     * Copies file to C:/BookOrganizer/ subfolders
     */
    private String importFile(File sourceFile, String targetFolder) throws IOException {
        File destDir = new File(targetFolder);
        if (!destDir.exists()) destDir.mkdirs(); // Creates C:/BookOrganizer if missing

        // Generate unique name to prevent overwriting
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destFile = new File(destDir, fileName);

        // Perform the duplication
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return destFile.getAbsolutePath();
    }

    private void initComponents(Book existingBook) {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Row
        gbc.gridx=0; gbc.gridy=0; formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx=1; gbc.gridwidth=2; titleField = new JTextField(25); formPanel.add(titleField, gbc);

        // Author Row
        gbc.gridx=0; gbc.gridy=1; gbc.gridwidth=1; formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx=1; gbc.gridwidth=2; authorField = new JTextField(25); formPanel.add(authorField, gbc);

        // Genre Row
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=1; formPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx=1; gbc.gridwidth=2; genreComboBox = new JComboBox<>(GENRES); formPanel.add(genreComboBox, gbc);

        // Year Row
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=1; formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx=1; gbc.gridwidth=2; yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 1000, 2100, 1));
        formPanel.add(yearSpinner, gbc);

        // Image Row
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=1; formPanel.add(new JLabel("Book Cover:"), gbc);
        gbc.gridx=1; JButton browseImg = new JButton("Browse..."); formPanel.add(browseImg, gbc);
        gbc.gridx=1; gbc.gridy=5; gbc.gridwidth=2;
        imagePreviewLabel = new JLabel("No Image", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(100, 130));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(imagePreviewLabel, gbc);

        // Document Row
        gbc.gridx=0; gbc.gridy=6; gbc.gridwidth=1; formPanel.add(new JLabel("Book File:"), gbc);
        gbc.gridx=1; JButton browseDoc = new JButton("Browse..."); formPanel.add(browseDoc, gbc);
        gbc.gridx=1; gbc.gridy=7; gbc.gridwidth=2;
        documentLabel = new JLabel("No file selected");
        formPanel.add(documentLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        bp.add(ok); bp.add(cancel);
        add(bp, BorderLayout.SOUTH);

        // Actions
        browseImg.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    imagePath = importFile(fc.getSelectedFile(), COVER_FOLDER);
                    updatePreview();
                } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error copying image"); }
            }
        });

        browseDoc.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    documentPath = importFile(fc.getSelectedFile(), DOCUMENT_FOLDER);
                    documentLabel.setText(fc.getSelectedFile().getName());
                } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error copying document"); }
            }
        });

        ok.addActionListener(e -> {
            try {
                service.BookValidator.validate(
                        titleField.getText(),
                        authorField.getText(),
                        (String) genreComboBox.getSelectedItem(),
                        (Integer) yearSpinner.getValue()
                );

                confirmed = true;
                applyData();
                dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        if(existingBook != null) loadExisting(existingBook);
    }

    private void updatePreview() {
        if(!imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH));
            imagePreviewLabel.setIcon(icon);
            imagePreviewLabel.setText("");
        }
    }

    private void loadExisting(Book b) {
        titleField.setText(b.getTitle());
        authorField.setText(b.getAuthor());
        genreComboBox.setSelectedItem(b.getGenre());
        yearSpinner.setValue(b.getPublishingDate());
        imagePath = b.getImagePath();
        documentPath = b.getDocumentPath();
        updatePreview();
        if(!documentPath.isEmpty()) documentLabel.setText(new File(documentPath).getName());
    }

    private void applyData() {
        if(editingBook == null) {
            editingBook = new Book(titleField.getText(), authorField.getText(), new Date(),
                    (String)genreComboBox.getSelectedItem(), (Integer)yearSpinner.getValue(), imagePath, documentPath);
        } else {
            editingBook.setTitle(titleField.getText());
            editingBook.setAuthor(authorField.getText());
            editingBook.setGenre((String)genreComboBox.getSelectedItem());
            editingBook.setPublishingDate((Integer)yearSpinner.getValue());
            editingBook.setImagePath(imagePath);
            editingBook.setDocumentPath(documentPath);
        }
    }



    public boolean isConfirmed() { return confirmed; }
    public Book getBook() { return editingBook; }
}