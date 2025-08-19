package pl.medflow.medflowbackend.domain.patient_management.dto;

import lombok.Builder;

@Builder
public record PatientConsentRequest(
    boolean consentGiven,
    boolean termsAccepted,
    boolean privacyPolicyAccepted
) {}
