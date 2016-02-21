package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @RequestMapping("/send_msg")
    public Greeting greeting(@RequestParam(value="msg_text", defaultValue="hey") String msg_text,
		@RequestParam(value="sending_id", defaultValue="0") String sending_id) {
        return new Greeting(msg_text, sending_id);
    }
}
