作业描述


* 去掉RsService上的@Service注解
通过使用@Bean这种方式进行spring bean的定义和注入
reference: https://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch02s02.html

* 阅读：https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/using-boot-spring-beans-and-dependency-injection.html
修改所有的service和controller 将依赖注入的方式改为通过构造函数注入（而非直接在字段上添加@Autowired）

