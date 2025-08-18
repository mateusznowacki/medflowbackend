package pl.medflow.medflowbackend.domain.aws.secrets;

public interface AwsSecrets {

    String getJwtPrivateKeyPem();

    String getJwtPublicKeyPem();

    String getS3BucketName();
}

