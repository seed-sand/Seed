package seed.web;
/**
 * Created by Macsnow on 2017/2/24.
 */
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.ABeanOfGreeting;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public ABeanOfGreeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new ABeanOfGreeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}