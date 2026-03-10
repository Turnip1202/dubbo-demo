# Dubbo分布式服务演示项目总结

## 1. 项目概述

本项目是一个基于Apache Dubbo 3.2.0的分布式服务演示项目，旨在展示Dubbo的核心功能：
- 服务注册与发现（使用Nacos作为注册中心）
- 远程服务调用（基于Triple协议）
- 应用级服务发现机制
- Spring Boot集成

项目通过分离的服务提供者和消费者，完整演示了Dubbo的分布式通信能力。

## 2. 项目结构

```
dubbo-demo/
├── docker-compose-nacos.yml      # Nacos启动脚本
├── dubbo-api/                    # 服务接口定义模块
│   └── src/main/java/org/apache/dubbo/demo/api/
│       └── DemoService.java      # 服务接口
├── dubbo-provider/               # 服务提供者模块
│   ├── src/main/java/org/apache/dubbo/demo/provider/
│   │   ├── DemoServiceImpl.java  # 服务实现
│   │   └── ProviderApplication.java  # 应用入口
│   └── src/main/resources/
│       └── application.yml       # 提供者配置
└── dubbo-consumer/               # 服务消费者模块
    ├── src/main/java/org/apache/dubbo/demo/consumer/
    │   ├── ConsumerController.java  # REST控制器
    │   └── ConsumerApplication.java  # 应用入口
    └── src/main/resources/
        └── application.yml       # 消费者配置
```

## 3. 核心技术栈

| 技术/框架 | 版本 | 用途 |
|---------|------|------|
| Spring Boot | 3.2.0 | 应用框架 |
| Apache Dubbo | 3.2.0 | 分布式服务框架 |
| Nacos | 2.2.0 | 服务注册中心 |
| Triple | - | Dubbo 3.x默认通信协议 |
| Protobuf | 3.21.12 | 序列化框架 |

## 4. 关键配置说明

### 4.1 Nacos注册中心配置

**docker-compose-nacos.yml**:
```yaml
version: '3.8'
services:
  nacos:
    image: nacos/nacos-server:v2.2.0
    container_name: nacos-standalone
    environment:
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - NACOS_AUTH_ENABLE=false
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9849:9849"
    volumes:
      - nacos_data:/home/nacos/data
      - nacos_logs:/home/nacos/logs
volumes:
  nacos_data:
  nacos_logs:
```

### 4.2 服务提供者配置

**dubbo-provider/application.yml**:
```yaml
dubbo:
  registry:
    address: nacos://127.0.0.1:8848
    parameters:
      registry-type: service  # 应用级服务发现
  protocol:
    name: tri  # Triple协议
    port: 3000
  application:
    name: dubbo-demo-provider
    qos-enable: false
  metadata-report:
    file-cache: false  # 禁用文件缓存避免权限问题

spring:
  application:
    name: dubbo-demo-provider
```

### 4.3 服务消费者配置

**dubbo-consumer/application.yml**:
```yaml
server:
  port: 8080

dubbo:
  registry:
    address: nacos://127.0.0.1:8848
    parameters:
      registry-type: service  # 应用级服务发现
  application:
    name: dubbo-demo-consumer
    qos-enable: false
  metadata-report:
    file-cache: false  # 禁用文件缓存避免权限问题

spring:
  application:
    name: dubbo-demo-consumer
```

## 5. 核心代码实现

### 5.1 服务接口定义

```java
package org.apache.dubbo.demo.api;

public interface DemoService {
    String sayHello(String name);
    String sayGoodbye(String name);
}
```

### 5.2 服务实现

```java
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
```

### 5.3 服务消费

```java
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
```

## 6. 使用方法

### 6.1 启动Nacos

```bash
cd dubbo-demo
docker-compose -f docker-compose-nacos.yml up -d
```

### 6.2 启动服务提供者

```bash
cd dubbo-demo/dubbo-provider
mvn spring-boot:run -DskipTests
```

### 6.3 启动服务消费者

```bash
cd dubbo-demo/dubbo-consumer
mvn spring-boot:run -DskipTests
```

### 6.4 测试服务调用

```bash
# 测试sayHello方法
curl http://localhost:8080/hello/World

# 测试sayGoodbye方法
curl http://localhost:8080/goodbye/World

# 检查消费者状态
curl http://localhost:8080/status
```

## 7. 常见问题及解决方法

### 7.1 端口冲突
- **错误信息**: `Address already in use: bind`
- **解决方法**: 查找并终止占用端口的进程或修改配置中的端口号

### 7.2 文件权限问题
- **错误信息**: `拒绝访问。` 或 `java.io.FileNotFoundException`
- **解决方法**: 禁用文件缓存，在配置中添加 `metadata-report.file-cache: false`

### 7.3 Protobuf依赖缺失
- **错误信息**: `java.lang.NoClassDefFoundError: com/google/protobuf/Message`
- **解决方法**: 添加Protobuf依赖到pom.xml

### 7.4 元数据获取失败
- **错误信息**: `Failed to get app metadata... Canceled by remote peer`
- **解决方法**: 确保服务提供者和消费者使用相同的服务发现类型（都使用`registry-type: service`）

## 8. 技术亮点

1. **Dubbo 3.x 新特性**: 使用了应用级服务发现机制，提升性能和可扩展性
2. **Triple协议**: 基于HTTP/2的现代化协议，支持多语言和多平台
3. **Nacos集成**: 实现了服务的动态注册与发现
4. **Spring Boot集成**: 简化了Dubbo的配置和使用
5. **分离架构**: 清晰展示了服务提供者和消费者的角色分工

## 9. 项目源码

项目源码位于 `dubbo-demo/` 目录下，包含完整的服务接口、实现和配置文件。

## 10. 相关资源

- [Apache Dubbo官方文档](https://dubbo.apache.org/zh/docs/)  
- [Nacos官方文档](https://nacos.io/zh-cn/docs/quick-start.html)  
- [Dubbo 3.0 新特性](https://dubbo.apache.org/zh/docs/migration/version-3/)  
- [Triple协议介绍](https://dubbo.apache.org/zh/docs/concepts/triple-protocol/)  

---

**文档生成时间**: 2026-03-10
**项目版本**: Dubbo 3.2.0
**最后更新**: 2026-03-10