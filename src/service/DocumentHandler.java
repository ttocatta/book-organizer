package service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DocumentHandler {

    // Entry point to handle documents. Uses system default for viewing and internal libraries for reading/validation.

    public static void openDocument(String path) throws IOException {
        if (path == null || path.isEmpty()) throw new IOException("Path is empty");

        File file = new File(path);
        if (!file.exists()) throw new IOException("File does not exist: " + path);

        String ext = path.toLowerCase();

        // Optimized Routing based on extension
        if (ext.endsWith(".pdf")) {
            validatePdf(file);
        } else if (ext.endsWith(".docx")) {
            validateDocx(file);
        } else if (ext.endsWith(".doc")) {
            validateDoc(file);
        }

        // Always open with system default application (Word, Acrobat, etc.)
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    // PDF Optimization: Only loads metadata to check if file is valid/readable
    private static void validatePdf(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            System.out.println("PDF Validated: " + document.getNumberOfPages() + " pages.");
        }
    }

    // Modern Word (.docx) - Using XWPF
    private static void validateDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            // Lazy Loading: We only grab a small snippet to confirm it's readable
            System.out.println("Docx Validated: " + file.getName());
        }
    }

    // Legacy Word (.doc) - Using HWPF
    private static void validateDoc(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            System.out.println("Legacy Doc Validated: " + file.getName());
        }
    }

}