package dialog;

import dialog.TutorialStep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class HowToUseDialog extends JDialog {

    private int currentIndex = 0;
    private final List<TutorialStep> steps;

    private JLabel titleLabel;
    private JLabel imageLabel;
    private JTextArea descriptionArea;
    private JLabel pageLabel;
    private JButton prevBtn;
    private JButton nextBtn;

    public HowToUseDialog(Frame parent, List<TutorialStep> steps) {
        super(parent, "How to use", true);
        this.steps = steps;
        initUI();
        updateStep();
        setSize(750, 550);
        setLocationRelativeTo(parent);
    }

    private ImageIcon scaleToLabel(ImageIcon icon, JLabel label) {
        if (icon == null || icon.getIconWidth() <= 0) return icon;

        int maxWidth = 550;   // maximum width allowed
        int maxHeight = 260;  // maximum height allowed

        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();

        // Calculate scale factor to fit within max bounds while preserving aspect ratio
        double widthRatio = (double) maxWidth / imgWidth;
        double heightRatio = (double) maxHeight / imgHeight;
        double scale = Math.min(1.0, Math.min(widthRatio, heightRatio)); // never scale up

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        Image scaled = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }



    private void initUI() {

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(
                new EmptyBorder(15, 15, 15, 15)
        );

        // TITLE
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // IMAGE LABEL (CREATE FIRST, CONFIGURE AFTER)
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(550, 260));
        imageLabel.setBorder(null); // remove border if you want

        // DESCRIPTION
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setOpaque(false);

        // PAGE LABEL
        pageLabel = new JLabel("", SwingConstants.CENTER);
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pageLabel.setForeground(Color.GRAY);

        // CENTER PANEL
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(titleLabel, BorderLayout.NORTH);
        center.add(imageLabel, BorderLayout.CENTER);
        center.add(descriptionArea, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        add(pageLabel, BorderLayout.NORTH);

        // NAVIGATION BUTTONS
        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");

        prevBtn.addActionListener(e -> {
            currentIndex--;
            updateStep();
        });

        nextBtn.addActionListener(e -> {
            currentIndex++;
            updateStep();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(prevBtn);
        bottom.add(nextBtn);

        add(bottom, BorderLayout.SOUTH);
    }


    private void updateStep() {
        TutorialStep step = steps.get(currentIndex);

        titleLabel.setText(step.getTitle());
        descriptionArea.setText(step.getDescription());

        imageLabel.setIcon(scaleToLabel(step.getImage(), imageLabel));

        pageLabel.setText("Step " + (currentIndex + 1) + " of " + steps.size());

        prevBtn.setVisible(currentIndex > 0);
        nextBtn.setVisible(currentIndex < steps.size() - 1);
    }

}
