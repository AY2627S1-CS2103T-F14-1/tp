package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MainAppTest {

    @Test
    public void version_isV13() {
        assertEquals("V1.3", MainApp.VERSION);
    }
}
