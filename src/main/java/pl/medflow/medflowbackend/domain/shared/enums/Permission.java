package pl.medflow.medflowbackend.domain.shared.enums;

public enum Permission {

    // Zarządzanie użytkownikami
    USER_CREATE,
    USER_READ,
    USER_UPDATE,
    USER_DELETE,

    // Zarządzanie lekarzami i personelem
    STAFF_CREATE,
    STAFF_READ,
    STAFF_UPDATE,
    STAFF_DELETE,

    // Zarządzanie pacjentami
    PATIENT_CREATE,
    PATIENT_READ,
    PATIENT_UPDATE,
    PATIENT_DELETE,

    // Rejestracja i zarządzanie wizytami
    APPOINTMENT_CREATE,
    APPOINTMENT_READ,
    APPOINTMENT_UPDATE,
    APPOINTMENT_CANCEL,

    // Dostęp do dokumentacji medycznej
    MEDICAL_RECORD_CREATE,
    MEDICAL_RECORD_READ,
    MEDICAL_RECORD_UPDATE,
    MEDICAL_RECORD_DELETE,

    // Obsługa leków, alergii, chorób
    MEDICATIONS_MANAGE,
    ALLERGIES_MANAGE,
    DIAGNOSES_MANAGE,

    // Uprawnienia administracyjne
    ROLE_ASSIGN,
    PERMISSION_MANAGE,
    SYSTEM_CONFIGURE,

    // Zarządzanie plikami (np. dokumentacja, wyniki)
    FILE_UPLOAD,
    FILE_DOWNLOAD,
    FILE_DELETE,

    // Zarządzanie oddziałami/salami
    DEPARTMENT_CREATE,
    DEPARTMENT_READ,
    DEPARTMENT_UPDATE,
    DEPARTMENT_DELETE,

    // Rezerwacje zabiegów / rehabilitacji
    TREATMENT_SCHEDULE,
    TREATMENT_MANAGE,

    // Akcje własne pacjenta
    SELF_PROFILE_READ,
    SELF_PROFILE_UPDATE,
    SELF_BOOK_APPOINTMENT,
    SELF_UPLOAD_DOCUMENT,

    // Zgody i polityki
    CONSENT_VIEW,
    CONSENT_UPDATE,

    // Notyfikacje
    NOTIFICATION_READ,
    NOTIFICATION_SEND,

    // Audyt / logi
    AUDIT_VIEW
}
