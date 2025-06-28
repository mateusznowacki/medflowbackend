package pl.medflow.medflowbackend.aws.secrets;

public interface AwsSecrets {

    String getJwtPrivateKeyPem();

    String getJwtPublicKeyPem();

    String getS3BucketName();
}

