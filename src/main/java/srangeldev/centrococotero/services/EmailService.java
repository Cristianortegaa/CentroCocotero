package srangeldev.centrococotero.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import srangeldev.centrococotero.models.Pedido;

import java.io.UnsupportedEncodingException;

/**
 * Servicio para envío de correos electrónicos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.name}")
    private String fromName;

    /**
     * Envía un email de confirmación de pedido con factura en PDF
     * 
     * @param pedido Pedido realizado
     * @param pdfBytes Bytes del PDF de la factura
     * @throws MessagingException Si hay error al enviar el email
     */
    public void enviarConfirmacionPedido(Pedido pedido, byte[] pdfBytes) throws MessagingException, UnsupportedEncodingException {
        log.info("Enviando email de confirmación para pedido: {}", pedido.getId());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Configurar destinatario
        helper.setTo(pedido.getUsuario().getEmail());
        helper.setFrom(fromEmail, fromName);
        helper.setSubject("Confirmación de pedido #" + pedido.getId());

        // Cuerpo del email en HTML
        String htmlContent = construirEmailHtml(pedido);
        helper.setText(htmlContent, true);

        // Adjuntar PDF
        helper.addAttachment(
                "Factura_" + pedido.getId() + ".pdf",
                new ByteArrayResource(pdfBytes)
        );

        // Enviar
        mailSender.send(message);

        log.info("Email enviado exitosamente a: {}", pedido.getUsuario().getEmail());
    }

    /**
     * Construye el contenido HTML del email de confirmación
     * 
     * @param pedido Pedido
     * @return HTML del email
     */
    private String construirEmailHtml(Pedido pedido) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .pedido-info { background: white; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .pedido-info h3 { color: #667eea; margin-top: 0; }
                    .info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
                    .total { font-size: 1.5em; font-weight: bold; color: #667eea; text-align: right; margin-top: 20px; }
                    .footer { text-align: center; color: #888; margin-top: 30px; font-size: 0.9em; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Pedido Confirmado!</h1>
                        <p>Gracias por tu compra en Centro Cocotero</p>
                    </div>
                    
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        
                        <p>Tu pedido ha sido confirmado y está siendo procesado. A continuación encontrarás los detalles:</p>
                        
                        <div class="pedido-info">
                            <h3>Detalles del Pedido</h3>
                            
                            <div class="info-row">
                                <span><strong>Número de Pedido:</strong></span>
                                <span>%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span><strong>Estado:</strong></span>
                                <span>%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span><strong>Productos:</strong></span>
                                <span>%d artículos</span>
                            </div>
                            
                            %s
                            
                            <div class="total">
                                Total: %.2f €
                            </div>
                        </div>
                        
                        <p>Adjunto encontrarás la factura en formato PDF.</p>
                        
                        <p>Puedes seguir el estado de tu pedido desde tu perfil en nuestra tienda.</p>
                        
                        <p>Si tienes alguna duda, no dudes en contactarnos.</p>
                        
                        <p>¡Gracias por confiar en nosotros!</p>
                    </div>
                    
                    <div class="footer">
                        <p>Centro Cocotero - Productos naturales de calidad</p>
                        <p>Este es un email automático, por favor no respondas a este mensaje.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                pedido.getUsuario().getNombre(),
                pedido.getId(),
                pedido.getEstado().toString(),
                pedido.getLineas().size(),
                pedido.getDireccionEnvio() != null && !pedido.getDireccionEnvio().isEmpty()
                        ? "<div class=\"info-row\"><span><strong>Dirección de envío:</strong></span><span>" + pedido.getDireccionEnvio() + "</span></div>"
                        : "",
                pedido.getTotal()
        );
    }

    /**
     * Envía un email simple de texto
     * 
     * @param to Destinatario
     * @param subject Asunto
     * @param body Cuerpo del mensaje
     * @throws MessagingException Si hay error al enviar
     */
    public void enviarEmailSimple(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        log.info("Enviando email simple a: {}", to);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setFrom(fromEmail, fromName);
        helper.setSubject(subject);
        helper.setText(body, false);

        mailSender.send(message);

        log.info("Email simple enviado exitosamente");
    }
}
