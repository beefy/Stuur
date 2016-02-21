package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrowdShoutController {

    @RequestMapping("/send_msg")
    public SendMsg sendmsg(@RequestParam(value="msg_text", defaultValue="hey") String msg_text,
		@RequestParam(value="sending_id", defaultValue="0") String sending_id) {
        return new SendMsg(msg_text, sending_id);
    }

    @RequestMapping("/receive_msg")
    public ReceiveMsg receivemsg(@RequestParam(value="receive_id", defaultValue="0") String receive_id) {
	return new ReceiveMsg(receive_id);
    }
}
