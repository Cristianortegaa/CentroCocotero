package srangeldev.centrococotero.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import srangeldev.centrococotero.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class StorageService {

    @Value("${storage.location:upload-dir}")
    private String location;

    private Path rootLocation;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(location);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el almacenamiento", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Fallo al guardar archivo vacío.");
            }

            // Generamos nombre único
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = Utils.generadorId() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename))
                    .normalize().toAbsolutePath();

            // Redimensionar si es imagen
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                try {
                    byte[] resizedImageBytes = redimensionarImagen(file);
                    try (InputStream inputStream = new ByteArrayInputStream(resizedImageBytes)) {
                        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception e) {
                    log.warn("No se pudo redimensionar, guardando original: {}", e.getMessage());
                    try (InputStream inputStream = file.getInputStream()) {
                        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } else {
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al guardar el archivo.", e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + filename, e);
        }
    }

    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar el archivo: " + filename, e);
        }
    }

    private byte[] redimensionarImagen(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen");
        }

        if (originalImage.getWidth() <= MAX_WIDTH && originalImage.getHeight() <= MAX_HEIGHT) {
            return file.getBytes();
        }

        BufferedImage resizedImage = Scalr.resize(originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_TO_WIDTH,
                MAX_WIDTH,
                MAX_HEIGHT,
                Scalr.OP_ANTIALIAS);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getFormatName(file.getContentType());
        ImageIO.write(resizedImage, formatName, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    private String getFormatName(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        switch (contentType) {
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/jpeg":
            case "image/jpg":
            default:
                return "jpg";
        }
    }
}
