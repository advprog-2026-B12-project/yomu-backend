package id.ac.ui.cs.advprog.yomubackend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoogleConfigTest {

    @Test
    void googleIdTokenVerifier_BeanCreated() {
        GoogleConfig config = new GoogleConfig("test-client-id");
        GoogleIdTokenVerifier verifier = config.googleIdTokenVerifier();
        assertNotNull(verifier);
    }
}
