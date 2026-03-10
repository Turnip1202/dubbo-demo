package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.demo.api.DemoService;

@DubboService
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        System.out.println("Provider received: " + name);
        return "Hello, " + name + "! [from provider]";
    }

    @Override
    public String sayGoodbye(String name) {
        System.out.println("Provider received goodbye request: " + name);
        return "Goodbye, " + name + "! [from provider]";
    }
}