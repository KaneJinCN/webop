<%@page language="java" pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Forward Sample</title>
</head>
<body>
<textarea cols="100" rows="20" style="border:none;">
<operation id="sample.forward" name="Forward Sample">
  <step id="stepToForwardPage"
    class="cn.kanejin.webop.opstep.ForwardPage">
    <return-action>
      <if return="0"><forward page="/jsp/sample/forward.jsp"/></if>
      <else><error/></else>
    </return-action>
  </step>
</operation>
</textarea>
</body>
</html>