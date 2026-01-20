package service;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageHandler {
    // Cache to avoid reloading the same image from disk multiple times
    private static final Map<String, BufferedImage> rawCache = new HashMap<>();

    public static ImageIcon getScaledIcon(String path, int width, int height) {
        if (path == null || path.isEmpty()) return null;

        try {
            BufferedImage original = rawCache.get(path);
            if (original == null) {
                File file = new File(path);
                if (!file.exists()) return null;
                original = ImageIO.read(file);
                rawCache.put(path, original);
            }

            Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}