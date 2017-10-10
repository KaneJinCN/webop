<#assign sample=JspTaglibs["http://webop.kanejin.cn/tags"]>
<#assign form=JspTaglibs["http://www.springframework.org/tags/form"]>

<!DOCTYPE html>
<html lang="zh" xmlns="http://www.w3.org/1999/html">
<head>
<#include "include/head.ftl">
	<title>Test Freemarker</title>
</head>
<body>

<pre>
	姓名：${name}
	性别：${sex} - <#if sex == '1'>男<#else>女</#if>
	出生日期：${dob?date}
	年龄：${age}
	信息：${message}
</pre>

<@sample.hello name=name />
<br/>
<br/>

<@form.form id="loginForm" action="/sample/freemarker" method="post">
	用户：<input name="name" /><br/>
	密码：<input name="password" type="password"/><br/>
	<input type="submit" value="登录" />
</@form.form>

<br/>
<br/>
</body>
</html>
