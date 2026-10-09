package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Utilidades para procesamiento, conversión y escalado de imágenes
 * para el campo foto (LONGTEXT / Base64) de la tabla libro.
 */
public class ImageUtil {

    /**
     * Convierte un archivo de imagen en un String Base64 redimensionado para almacenamiento.
     */
    public static String fileToBase64(File file, int maxWidth, int maxHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        if (originalImage == null) {
            throw new IOException("El archivo no es una imagen válida.");
        }

        // Redimensionar manteniendo aspecto para optimizar almacenamiento en Base64
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);

        int targetWidth = (int) (originalWidth * ratio);
        int targetHeight = (int) (originalHeight * ratio);
        if (targetWidth <= 0) targetWidth = originalWidth;
        if (targetHeight <= 0) targetHeight = originalHeight;

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Convierte un String Base64 a un ImageIcon escalado para mostrar en un JLabel.
     */
    public static ImageIcon base64ToImageIcon(String base64Str, int width, int height) {
        if (base64Str == null || base64Str.trim().isEmpty()) {
            return null;
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(base64Str.trim());
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bais);
            if (image == null) return null;

            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Error al decodificar imagen Base64: " + e.getMessage());
            return null;
        }
    }
}
