# webop
webop是一个java webapp的MVC框架


## Usage 使用方法
1. 在工程中引用webop（以maven为例）

    在pom.xml中添加依赖
    ```xml
    <dependency>
        <groupId>cn.kanejin.webop</groupId>
        <artifactId>webop</artifactId>
        <version>3.1.0</version>
    </dependency>
    ```

2. 定义Operation
    ```xml
    <operation uri="/sample/next" name="Next Sample" method="GET">
        <description>
        <![CDATA[
            这是一个如何使用webop的一个示例
        ]]>
        </description>
		<cache>
			<expiry>
				<ttl unit="minutes">30</ttl>
			</expiry>
			<key-field>name</key-field>
			<key-field>age</key-field>
		</cache>
        <interceptor ref="helloInterceptor" />
        <interceptor ref="worldInterceptor" />
        <step id="stepNext"
            class="cn.kanejin.webop.opstep.ForwardPage">
            <return-action>
                <if return="0"><next/></if>
                <else><error/></else>
            </return-action>
        </step>
        <step id="stepNext2"
            class="cn.kanejin.webop.opstep.ForwardPage">
            <return-action>
                <if return="0"><forward page="/jsp/sample/next.jsp"/></if>
                <else><error/></else>
            </return-action>
        </step>
    </operation>
    ```
    [查看完整的示例代码](https://github.com/KaneJinCN/webop/blob/master/webop-sample/src/main/webapp/WEB-INF/config/webop/op-sample.xml)

3. 在Operation Step里实现业务逻辑
    ```java
    public class ReturnActionStep implements OperationStep {
    
        @StepMethod
        public int execute(OperationContext context,
                           @Param(name = "name") String name,
                           @Param(name = "age", ifEmpty = "20") Integer age,
                           @Param(name = "avatar") FileItem avatar,
                           @Param(name = "followers") String[] followers
    	) {
    
            System.out.println(name);
            System.out.println(age);
            System.out.println(avatar);
            System.out.println(followers);
    
            return 0;
        }
    }
    ```

    [查看完整的示例代码](https://github.com/KaneJinCN/webop/blob/master/webop-sample/src/main/java/cn/kanejin/webop/sample/opstep/ReturnActionStep.java)
    
4. 在web.xml里配置webop的DispatcherFilter，这个DispatcherFilter作为控制器，把http请求分配到相应的Operation

    ```xml
    <!-- 配置Operation定义的xml文件 -->
    <context-param>
        <param-name>webopConfigLocation</param-name>
        <param-value>
            /WEB-INF/config/webop/op-*.xml
        </param-value>
    </context-param>

    <!-- 加载webop配置 -->
    <listener>
        <listener-class>cn.kanejin.webop.context.ConfigLoaderListener</listener-class>
    </listener>

    <!-- DispatcherFilter作为控制器 -->
    <filter>
        <filter-name>webopDispatcher</filter-name>
        <filter-class>cn.kanejin.webop.DispatcherFilter</filter-class>
        <init-param>
            <param-name>ignoreUriPatterns</param-name>
            <param-value>
                /
                /**/*.html
                /statics/**
            </param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>webopDispatcher</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    ```

    [查看完整的示例代码](https://github.com/KaneJinCN/webop/blob/master/webop-sample/src/main/webapp/WEB-INF/web.xml)

## License 许可
[MIT](https://github.com/KaneJinCN/webop/blob/master/LICENSE)
