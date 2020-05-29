# SpringBoot整合Swagger-UI实现在线API文档
**Swagger-UI**是HTML, Javascript, CSS的一个集合，是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务，可以动态地根据注解生成在线API文档。

总体目标是使客户端和文件系统作为服务器以同样的速度来更新。文件的方法，参数和模型紧密集成到服务器端的代码，允许API来始终保持同步。
在项目开发中，根据业务代码自动生成API文档，给前端提供在线测试，自动显示JSON格式，方便了后端与前端的沟通与调试成本。

**常用注解**

 - <code>@Api</code>：用于修饰Controller类，生成Controller相关文档信息，在Controller类的开头。表示这个类是swagger的资源，tags表示分组说明标签
 
 - <code>@ApiOperation</code>：用于修饰Controller类中的方法，生成接口方法相关文档信息，方法前面。表示一个http请求的操作，value用于方法描述 ，notes用于提示内容
 
 - <code>@ApiImplicitParam</code>：用于Controller类修饰接口中的参数，生成接口参数相关文档信息。name–参数名， value–参数说明 ，dataType–数据类型 ，paramType–参数类型 ，example–举例说明
 
 - <code>@ApiModelProperty</code>：用于修饰实体类的属性，当实体类是请求参数或返回结果时，直接生成相关文档信息。表示对model属性的说明或者数据操作更改 ，value–字段说明 ，name–重写属性名字 ，dataType–重写属性类型 ，required–是否必填 ，example–举例说明 ，hidden–隐藏

[点击这里>查看更多API注解用法](https://github.com/swagger-api/swagger-core/wiki/Annotations)

## SpringBoot整合Swagger-UI实现在线API文档步骤
**1.引入swagger依赖**

```java
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```
**2.自定义实体User类和Message类**

```java
    private Long id;
    private String name;
    private Long age;
    //省略 get set方法
```

```java
	private Long id;
	@ApiModelProperty(value = "消息体")
	private String text;
	@ApiModelProperty(value = "消息总结")
	private String summary;
	private Date createDate;
	//省略 get set方法
```

**3.这里可以写一个通用响应对象，返回用户数据结果**

**【User】**

```java
	private BaseResult(T data) {
        this(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }
    public static <T> BaseResult<T> successWithData(T data) {
        return new BaseResult<>(data);
    }
```

**也可以写一个服务类和业务逻辑的实现类**

**【Message】**

```java
public interface MessageService {

	List<Message> findAll();

	Message save(Message message);

	Message update(Message message);

	Message updateText(Message message);

	Message findMessage(Long id);

	void deleteMessage(Long id);

}
```

```java
@Service("messageService")
public class MessageServiceImpl implements MessageService {

	private static AtomicLong counter = new AtomicLong();
	private final ConcurrentMap<Long, Message> messages = new ConcurrentHashMap<>();

	@Override
	public List<Message> findAll() {
		List<Message> messages = new ArrayList<Message>(this.messages.values());
		return messages;
	}

	@Override
	public Message save(Message message) {
		Long id = message.getId();
		if (id == null) {
			id = counter.incrementAndGet();
			message.setId(id);
		}
		this.messages.put(id, message);
		return message;
	}

	@Override
	public Message update(Message message) {
		this.messages.put(message.getId(), message);
		return message;
	}

	@Override
	public Message updateText(Message message) {
		Message msg=this.messages.get(message.getId());
		msg.setText(message.getText());
		this.messages.put(msg.getId(), msg);
		return msg;
	}

	@Override
	public Message findMessage(Long id) {
		return this.messages.get(id);
	}

	@Override
	public void deleteMessage(Long id) {
		this.messages.remove(id);
	}

}
```
**4.Controller接口方法**

**【User】**

```java
@Api(value = "用户管理", description = "用户管理API", position = 100, protocols = "http")
@RestController
@RequestMapping(value = "/user")
public class UserController {
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    @ApiOperation(
            value = "获取用户列表",
            notes = "查询用户")
    @RequestMapping(value = {""}, method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 100, message = "异常数据")
    })
    public List<User> getUserList() {
        return new ArrayList<>(users.values());
    }

    @ApiOperation(
            value = "创建用户信息",
            notes = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ipAdd", value = "ip地址", required = false, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResult<User> postUser(@ApiIgnore User user) {
        users.put(user.getId(), user);
        return BaseResult.successWithData(user);
    }

    @ApiOperation(
            value = "获取详细信息",
            notes = "根据ID获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    @ApiOperation(
            value = "更新用户信息",
            notes = "根据ID更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public BaseResult<User> putUser(@PathVariable Long id, @ApiIgnore User user) {
        User u = users.get(id);
        u.setName(user.getName());
        u.setAge(user.getAge());
        users.put(id, u);
        return BaseResult.successWithData(u);
    }
    @ApiOperation(
            value = "删除用户信息",
            notes = "根据ID删除用户")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long id) {
        users.remove(id);
        return "success";
    }

}
```

**【Message】**

```java
@Api(value = "消息", description = "消息操作 API", position = 100, protocols = "http")
@RestController
@RequestMapping("/")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@ApiOperation(
			value = "消息列表",
			notes = "完整的消息内容列表",
			produces="application/json, application/xml",
			consumes="application/json, application/xml",
			response = List.class)
	@GetMapping(value = "messages")
	public List<Message> list() {
		List<Message> messages = this.messageService.findAll();
		return messages;
	}

	@ApiOperation(
			value = "添加消息",
			notes = "根据参数创建消息"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "消息 ID", required = true, dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "text", value = "正文", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "summary", value = "摘要", required = false, dataType = "String", paramType = "query"),
	})
	@PostMapping(value = "message")
	public Message create(Message message) {
		System.out.println("message===="+message.toString());
		message = this.messageService.save(message);
		return message;
	}

	@ApiOperation(
			value = "修改消息",
			notes = "根据参数修改消息"
	)
	@PutMapping(value = "message")
	@ApiResponses({
			@ApiResponse(code = 100, message = "请求参数有误"),
			@ApiResponse(code = 101, message = "未授权"),
			@ApiResponse(code = 103, message = "禁止访问"),
			@ApiResponse(code = 104, message = "请求路径不存在"),
			@ApiResponse(code = 200, message = "服务器内部错误")
	})
	public Message modify(Message message) {
		Message messageResult=this.messageService.update(message);
		return messageResult;
	}

	@ApiOperation(
			value = "消息补丁"
	)
	@PatchMapping(value="/message/text")
	public BaseResult<Message> patch(Message message) {
		Message messageResult=this.messageService.updateText(message);
		return BaseResult.successWithData(messageResult);
	}

	@ApiOperation(
			value = "消息详情",
			notes = "根据ID查看消息详情"
	)
	@GetMapping(value = "message/{id}")
	public Message get(@PathVariable Long id) {
		Message message = this.messageService.findMessage(id);
		return message;
	}

	@ApiOperation(
			value = "删除消息",
			notes = "根据ID删除消息"
	)
	@DeleteMapping(value = "message/{id}")
	public void delete(@PathVariable("id") Long id) {
		this.messageService.deleteMessage(id);
	}

}
```

**5.添加Swagger的配置**

该套 API 说明，包含作者、简介、版本、端口、服务URL等

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 自行修改为自己的包路径
                .apis(RequestHandlerSelectors.basePackage("com.cw.swagger.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("客户管理")
                .description("客户管理中心 API 1.0 操作文档")
                //服务条款网址
                .termsOfServiceUrl("https://blog.csdn.net/weixin_44316527/article/details/106421108")
                .version("1.0")
                .contact(new Contact("学习网址", "http://www.ityouknow.com/", ""))
                .build();
    }

}
```
**下面进行测试，启动项目**

访问<code>http://localhost:8080/swagger-ui.html</code>

<font color="blue">**消息操作**</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020052916104012.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMxNjUyNw==,size_16,color_FFFFFF,t_70)

<font color="green">**添加消息**</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529161907670.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMxNjUyNw==,size_16,color_FFFFFF,t_70)

<font color="green">**Try it out**</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529162103429.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMxNjUyNw==,size_16,color_FFFFFF,t_70)

<font color="green">**Execute**</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529162256332.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMxNjUyNw==,size_16,color_FFFFFF,t_70)

其余同理。

<font color="orange">**用户管理**</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529161120772.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMxNjUyNw==,size_16,color_FFFFFF,t_70)

点击这里>CSDN项目博客地址-[SpringBoot整合Swagger-UI实现在线API文档](https://blog.csdn.net/weixin_44316527/article/details/106421108)

点击这里>[Github项目源码地址-SpringBoot整合Swagger-UI实现在线API文档](https://github.com/ChuaWi/SpringBoot-Swagger)

学习网址：[spring-boot-swagger](http://www.ityouknow.com/spring-boot.html)
