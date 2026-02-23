package com.payments.orchestrator.infrastructure.config.constants;


public class ErrorMessagesConstants {

    private ErrorMessagesConstants() {
    }

    // Domain messages
    public static final String MSG_INVALID_TRANSACTION = "La transacción contiene datos inválidos o incompletos";
    public static final String MSG_INVALID_AMOUNT = "El monto debe ser mayor a cero";
    public static final String MSG_INVALID_SOURCE_ACCOUNT = "La cuenta origen es inválida";
    public static final String MSG_INVALID_DESTINATION_ACCOUNT = "La cuenta destino es inválida";
    public static final String MSG_INSUFFICIENT_FUNDS = "Fondos insuficientes en la cuenta origen";

    // Application messages
    public static final String MSG_BANK_NOT_FOUND = "El banco especificado no existe en el catálogo";
    public static final String MSG_STRATEGY_RESOLUTION = "No se pudo resolver la estrategia de integración para el banco";
    public static final String MSG_TRANSFER_PROCESSING = "Error al procesar la transferencia";
    public static final String MSG_DUPLICATE_STRATEGY = "Existen múltiples estrategias registradas para el mismo banco";

    // Infrastructure messages
    public static final String MSG_DB_ERROR = "Error al acceder a la base de datos";
    public static final String MSG_BANK_UNAVAILABLE = "El banco destino no está disponible en este momento";
    public static final String MSG_EXTERNAL_SERVICE = "Servicio externo no disponible";
    public static final String MSG_TIMEOUT = "La solicitud excedió el tiempo máximo de espera";
    public static final String MSG_CONNECTION_ERROR = "Error de conexión con el servicio externo";

    // Validation messages
    public static final String MSG_VALIDATION_FAILED = "La solicitud contiene datos inválidos";
    public static final String MSG_MISSING_REQUIRED_FIELD = "Campo requerido ausente: %s";
    public static final String MSG_INVALID_FORMAT = "Formato inválido para el campo: %s";

    // Generic messages
    public static final String MSG_INTERNAL_ERROR = "Ocurrió un error interno del servidor";
    public static final String MSG_SUCCESS = "Operación completada correctamente";
}
