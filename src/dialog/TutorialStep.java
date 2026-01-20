package dialog;

import javax.swing.*;

public class TutorialStep {

    private final String title;
    private final String description;
    private final ImageIcon image;

    public TutorialStep(String title, String description, ImageIcon image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getImage() {
        return image;
    }
}
