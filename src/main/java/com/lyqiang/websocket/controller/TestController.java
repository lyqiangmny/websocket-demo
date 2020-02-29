package com.lyqiang.websocket.controller;

import com.lyqiang.websocket.server.TestServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lyqiang
 */
@RestController
public class TestController {

    @GetMapping(value = "/send")
    public Object send(@RequestParam String userId) {
        TestServer.sendInfo("hello", userId);
        return "success";
    }
}
