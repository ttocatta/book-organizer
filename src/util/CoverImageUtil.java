package util;

import library.Book;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CoverImageUtil {
    // RAM Cache: Stores thumbnails so pagination is instant
    private static final Map<String, ImageIcon> thumbnailCache = new HashMap<>();

    public static ImageIcon createCoverThumbnail(Book book) {
        String path = book.getImagePath();

        if (path != null && !path.isEmpty()) {
            if (thumbnailCache.containsKey(path)) return thumbnailCache.get(path);

            try {
                File file = new File(path);
                if (file.exists()) {
                    BufferedImage img = ImageIO.read(file);
                    Image scaled = img.getScaledInstance(50, 70, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaled);
                    thumbnailCache.put(path, icon);
                    return icon;
                }
            } catch (Exception e) {}
        }
        return createTextCover(book.getTitle());
    }

    private static ImageIcon createTextCover(String title) {
        BufferedImage img = new BufferedImage(50, 70, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(70, 130, 180));
        g.fillRect(0, 0, 50, 70);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString(title.length() > 5 ? title.substring(0, 5) : title, 5, 35);
        g.dispose();
        return new ImageIcon(img);
    }

}