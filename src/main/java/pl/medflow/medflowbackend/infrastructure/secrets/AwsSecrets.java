package pl.medflow.medflowbackend.infrastructure.secrets;

public interface AwsSecrets {

    String getJwtPrivateKeyPem();

    String getJwtPublicKeyPem();

    String getS3BucketName();
}

