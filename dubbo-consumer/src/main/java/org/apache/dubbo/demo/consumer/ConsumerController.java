package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.demo.api.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @DubboReference
    private DemoService demoService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        System.out.println("Consumer calling sayHello with: " + name);
        String result = demoService.sayHello(name);
        System.out.println("Consumer received: " + result);
        return result;
    }

    @GetMapping("/goodbye/{name}")
    public String goodbye(@PathVariable String name) {
        System.out.println("Consumer calling sayGoodbye with: " + name);
        String result = demoService.sayGoodbye(name);
        System.out.println("Consumer received: " + result);
        return result;
    }

    @GetMapping("/status")
    public String status() {
        return "Consumer is running and ready to call Dubbo services!";
    }
}
