/**
 * Created by Macsnow on 2017/2/24.
 */

import seed.domain.ABeanOfGreeting;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BeanTest {
    ABeanOfGreeting aBeanOfGreeting;

    @Before
    public void setup() {
        aBeanOfGreeting = new ABeanOfGreeting(3, "testCase");
    }

    @Test
    public void TestCase() {
        assertEquals(3, aBeanOfGreeting.getId());
        assertEquals("testCase", aBeanOfGreeting.getContent());
    }
}
