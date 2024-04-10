package levy.earthquake;

import levy.earthquake.json.FeatureCollection;
import levy.earthquake.json.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EarthquakeServiceFactoryTest {

    @Test
    void oneHour() {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();
        // when
        FeatureCollection collection = service.oneHour().blockingGet();
        // then 
        Properties properties = collection.feature[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0,properties.mag);
        assertNotEquals(0,properties.time);
    }

}