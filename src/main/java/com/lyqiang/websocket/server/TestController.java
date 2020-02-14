package com.lyqiang.websocket.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author lyqiang
 */
@RestController
public class TestController {

    @GetMapping(value = "/send")
    public Object send(@RequestParam String userId) throws IOException {
        TestServer.sendInfo("hello", userId);
        return "success";
    }
}
