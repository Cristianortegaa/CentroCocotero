package srangeldev.centrococotero.models.pago;

/**
 * Enum para el estado del pago
 */
public enum EstadoPago {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO,
    FALLIDO,
    REEMBOLSADO,
    CANCELADO;
}
