package passoffTests.serverTests;

import server.ClearService;
import server.Result;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ClearServiceTests {

    @Test
    void clearTestSuccess() {  // only requires positive test case
        ClearService clearService = new ClearService();
        Result result = clearService.clear();

        assertTrue(result.isSuccess(), "Clear Operation succeeded");
        assertEquals("Database cleared successfully", result.getMessage(), "[200]");
    }

}