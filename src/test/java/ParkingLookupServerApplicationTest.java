import com.fasterxml.jackson.databind.JsonNode;
import org.code.ParkingLookupServerApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ParkingLookupServerApplication.class)
@AutoConfigureMockMvc
public class ParkingLookupServerApplicationTest {

    @Autowired
    private ParkingLookupServerApplication parkingLookupServerApplication;

    @Test
    public void getParkingListInJsonFormat() throws Exception{
        JsonNode jsonNode = parkingLookupServerApplication.getNearbyParkingsAsJson(43.32535569739267,5.42178123862934,false,10);

        assertEquals(10, jsonNode.size());
    }

    @Test
    public void getFreeParkingInJsonFormat() throws Exception{
        JsonNode jsonNode = parkingLookupServerApplication.getNearbyParkingsAsJson(43.32535569739267,5.42178123862934,true,1);

        assertTrue(Boolean.parseBoolean(jsonNode.get(0).get("gratuit").asText()));
    }

    @Test
    public void firstParkingClosestThanSecond() throws Exception{
        JsonNode jsonNode = parkingLookupServerApplication.getNearbyParkingsAsJson(43.32535569739267,5.42178123862934,true,2);

        Double distanceFirst = Double.parseDouble(jsonNode.get(0).get("distance").toString());
        Double distanceSecond = Double.parseDouble(jsonNode.get(1).get("distance").toString());

        assertTrue(distanceFirst <= distanceSecond);
    }
}
