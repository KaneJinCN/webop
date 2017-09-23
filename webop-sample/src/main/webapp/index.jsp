<%@page language="java" pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Sample</title>
</head>
<body>

<ul>
<li><a href="#sampleForward">/sample/forward</a></li>
<li><a href="#sampleRedirect">/sample/redirect</a></li>
<li><a href="#sampleNext">/sample/next</a></li>
<li><a href="#sampleStep">/sample/step</a></li>
<li><a href="#sampleText">/sample/text</a></li>
<li><a href="#sampleResponse">/sample/response</a></li>
<li><a href="#sampleAttribute">/sample/attribute</a></li>
<li><a href="#sampleJson">/sample/json</a></li>
<li><a href="#sampleJsonp">/sample/jsonp?callback=sayHello</a></li>
<li><a href="#sampleScript">/sample/script?callback=sayWorld</a></li>
<li><a href="#sampleXml">/sample/xml</a></li>
<li><a href="#sampleOperation">/sample/operation?name=Kane</a></li>
<li><a href="#sampleReturn0">/sample/return?return=0</a></li>
<li><a href="#sampleReturn1">/sample/return?return=1</a></li>
<li><a href="#sampleReturn2">/sample/return?return=2</a></li>
<li><a href="#sampleReturn3">/sample/return?return=3</a></li>
<li><a href="#sampleReturn4">/sample/return?return=4</a></li>
<li><a href="#sampleReturnError">/sample/return?return=-1</a></li>
<li><a href="#sample1">/sample/1</a></li>
<li><a href="#sample2">/sample/2</a></li>
<li><a href="#sample3">/sample/3</a></li>
<li><a href="#sampleInfinite">/sample/infinite</a></li>

</ul>

<h2 id="sampleForward">/sample/forward : </h2>
<iframe src="<%=request.getContextPath()%>/sample/forward" width="800" height="200"></iframe>

<h2 id="sampleRedirect">/sample/redirect : </h2>
<iframe src="<%=request.getContextPath()%>/sample/redirect" width="800" height="200"></iframe>

<h2 id="sampleNext">/sample/next : </h2>
<iframe src="<%=request.getContextPath()%>/sample/next" width="800" height="200"></iframe>

<h2 id="sampleStep">/sample/step : </h2>
<iframe src="<%=request.getContextPath()%>/sample/step" width="800" height="200"></iframe>

<h2 id="sampleText">/sample/text : </h2>
<iframe src="<%=request.getContextPath()%>/sample/text" width="800" height="80"></iframe>

<h2 id="sampleResponse">/sample/response : </h2>
<iframe src="<%=request.getContextPath()%>/sample/response" width="800" height="200"></iframe>

<h2 id="sampleAttribute">/sample/attribute : </h2>
<iframe src="<%=request.getContextPath()%>/sample/attribute" width="800" height="80"></iframe>

<h2 id="sampleJson">/sample/json : </h2>
<iframe src="<%=request.getContextPath()%>/sample/json" width="800" height="80"></iframe>

<h2 id="sampleJsonp">/sample/jsonp?callback=sayHello : </h2>
<iframe src="<%=request.getContextPath()%>/sample/jsonp?callback=sayHello" width="800" height="80"></iframe>

<h2 id="sampleScript">/sample/script?callback=sayWorld : </h2>
<iframe src="<%=request.getContextPath()%>/sample/script?callback=sayWorld" width="800" height="80"></iframe>

<h2 id="sampleXml">/sample/xml : </h2>
<iframe src="<%=request.getContextPath()%>/sample/xml" width="800" height="80"></iframe>

<h2 id="sampleOperation">/sample/operation?name=Kane : </h2>
<iframe src="<%=request.getContextPath()%>/sample/operation?name=Kane" width="800" height="200"></iframe>

<h2 id="sampleReturn0">/sample/return?return=0 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=0" width="800" height="200"></iframe>

<h2 id="sampleReturn1">/sample/return?return=1 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=1" width="800" height="200"></iframe>

<h2 id="sampleReturn2">/sample/return?return=2 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=2" width="800" height="200"></iframe>

<h2 id="sampleReturn3">/sample/return?return=3 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=3" width="800" height="200"></iframe>

<h2 id="sampleReturn4">/sample/return?return=4 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=4" width="800" height="200"></iframe>

<h2 id="sampleReturnError">/sample/return?return=-1 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/return?return=-1" width="800" height="200"></iframe>

<h2 id="sample1">/sample/1 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/1" width="800" height="200"></iframe>
<h2 id="sample2">/sample/2 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/2" width="800" height="200"></iframe>
<h2 id="sample3">/sample/3 : </h2>
<iframe src="<%=request.getContextPath()%>/sample/3" width="800" height="200"></iframe>

<h2 id="sampleInfinite">/sample/infinite : </h2>
<iframe src="<%=request.getContextPath()%>/sample/infinite" width="800" height="200"></iframe>


</body>

</html>