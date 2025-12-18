package srangeldev.centrococotero.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import srangeldev.centrococotero.models.LineaPedido;
import srangeldev.centrococotero.models.Pedido;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Servicio para generar facturas en PDF usando iText
 */
@Slf4j
@Service
public class PdfService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un PDF de factura para un pedido
     * 
     * @param pedido Pedido del que generar la factura
     * @return Bytes del PDF generado
     * @throws DocumentException Si hay error al generar el PDF
     */
    public byte[] generarFacturaPdf(Pedido pedido) throws DocumentException {
        log.info("Generando PDF de factura para pedido: {}", pedido.getId());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();

        // ENCABEZADO
        addHeader(document, pedido);
        document.add(new Paragraph("\n"));

        // INFORMACIÓN DEL CLIENTE
        addCustomerInfo(document, pedido);
        document.add(new Paragraph("\n"));

        // TABLA DE PRODUCTOS
        addProductsTable(document, pedido);
        document.add(new Paragraph("\n"));

        // TOTALES
        addTotals(document, pedido);
        document.add(new Paragraph("\n"));

        // PIE DE PÁGINA
        addFooter(document);

        document.close();

        log.info("PDF generado exitosamente para pedido: {}", pedido.getId());
        return baos.toByteArray();
    }

    /**
     * Añade el encabezado con logo y datos de la empresa
     */
    private void addHeader(Document document, Pedido pedido) throws DocumentException {
        Paragraph titulo = new Paragraph("CENTRO COCOTERO", TITLE_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Factura de Pedido", HEADER_FONT);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        document.add(new Paragraph("\n"));

        // Información del pedido
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);

        addCell(infoTable, "Número de Pedido:", HEADER_FONT);
        addCell(infoTable, pedido.getId(), NORMAL_FONT);

        addCell(infoTable, "Fecha:", HEADER_FONT);
        addCell(infoTable, pedido.getCreatedAt().format(dateFormatter), NORMAL_FONT);

        addCell(infoTable, "Estado:", HEADER_FONT);
        addCell(infoTable, pedido.getEstado().toString(), NORMAL_FONT);

        document.add(infoTable);
    }

    /**
     * Añade información del cliente
     */
    private void addCustomerInfo(Document document, Pedido pedido) throws DocumentException {
        Paragraph clienteTitulo = new Paragraph("DATOS DEL CLIENTE", HEADER_FONT);
        document.add(clienteTitulo);

        Paragraph clienteNombre = new Paragraph(
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellidos(),
                NORMAL_FONT
        );
        document.add(clienteNombre);

        Paragraph clienteEmail = new Paragraph(
                "Email: " + pedido.getUsuario().getEmail(),
                NORMAL_FONT
        );
        document.add(clienteEmail);

        if (pedido.getDireccionEnvio() != null && !pedido.getDireccionEnvio().isEmpty()) {
            Paragraph direccion = new Paragraph(
                    "Dirección de envío: " + pedido.getDireccionEnvio(),
                    NORMAL_FONT
            );
            document.add(direccion);
        }
    }

    /**
     * Añade tabla de productos
     */
    private void addProductsTable(Document document, Pedido pedido) throws DocumentException {
        Paragraph productosTitulo = new Paragraph("PRODUCTOS", HEADER_FONT);
        document.add(productosTitulo);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(4); // Producto, Precio, Cantidad, Subtotal
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 1f, 1.5f});

        // Encabezados
        addHeaderCell(table, "Producto");
        addHeaderCell(table, "Precio");
        addHeaderCell(table, "Cant.");
        addHeaderCell(table, "Subtotal");

        // Filas de productos
        for (LineaPedido linea : pedido.getLineas()) {
            addCell(table, linea.getProducto().getNombre(), NORMAL_FONT);
            addCell(table, currencyFormat.format(linea.getPrecioUnitario()), NORMAL_FONT);
            addCell(table, String.valueOf(linea.getCantidad()), NORMAL_FONT);
            addCell(table, currencyFormat.format(linea.getSubtotal()), NORMAL_FONT);
        }

        document.add(table);
    }

    /**
     * Añade totales del pedido
     */
    private void addTotals(Document document, Pedido pedido) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addCell(totalsTable, "TOTAL:", HEADER_FONT);
        addCell(totalsTable, currencyFormat.format(pedido.getTotal()), TITLE_FONT);

        document.add(totalsTable);
    }

    /**
     * Añade pie de página
     */
    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph("\n\n"));
        
        Paragraph gracias = new Paragraph("¡Gracias por su compra!", HEADER_FONT);
        gracias.setAlignment(Element.ALIGN_CENTER);
        document.add(gracias);

        Paragraph info = new Paragraph(
                "Centro Cocotero - Tienda de productos naturales\n" +
                "Email: info@centrococotero.com | Teléfono: 123-456-789",
                SMALL_FONT
        );
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);
    }

    /**
     * Añade una celda normal a la tabla
     */
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Añade una celda de encabezado a la tabla
     */
    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
