package pl.medflow.medflowbackend.todo.patient_management.dto;

import lombok.Builder;

@Builder
public record PatientConsentRequest(
    boolean consentGiven,
    boolean termsAccepted,
    boolean privacyPolicyAccepted
) {}
