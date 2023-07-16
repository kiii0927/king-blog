

## Spring

### @Component/@Service 无法注入到 ioc容器中

> SpringBoot多模块项目中无法注入其他模块中的spring bean
```text
SpringBoot多模块项目中无法注入其他模块中的spring bean
在一个springboot项目中创建了一个system模块，一个api模块，
在api模块中引入system模块依赖，发现无法注入system模块中的bean，idea提示无法找到bean。
原因是两个模块路径不同，一个是com.king.system，另一个是com.king.api。
这是因为springboot的@SpringBootApplication注解默认扫描范围为自己的启动类所在的包（com.king.api）及其子包，所以此时模块api并没有扫描到模块system的bean，那么自然无法在模块api中注入模块system的Service类。
最后，方法一：都改成com.king就好了。
方法二：手动修改成导入多个包，@SpringBootApplication(scanBasePackages = {"com.king.api", "com.king.system"}) 或者 @SpringBootApplication(scanBasePackages = "com.king.*")
```
[原文链接](https://blog.csdn.net/jeason13920089655/article/details/110619214)



### DataSourceAutoConfiguration.class

> @SpringBootApplication(exclude={DataSourceAutoConfiguration.class})

[原文链接](https://blog.csdn.net/wangrongfei136/article/details/108622712)
