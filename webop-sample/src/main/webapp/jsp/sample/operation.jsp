<%@page language="java" pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Forward Sample</title>
</head>
<body>
<textarea cols="100" rows="18" style="border:none;">
<operation id="sample.operation" name="Operation Sample">
  <step id="stepNext"
    class="cn.kanejin.webop.opstep.ForwardPage">
    <return-action>
      <if return="0"><operation id="sample.operation.to" /></if>
      <else><error/></else>
    </return-action>
  </step>
</operation>
<operation id="sample.operation.to" name="Operation Sample">
  <step id="stepNext2"
    class="cn.kanejin.webop.opstep.ForwardPage">
    <return-action>
      <if return="0"><forward page="/jsp/sample/operation.jsp"/></if>
      <else><error/></else>
    </return-action>
  </step>
</operation>
</textarea>
</body>
</html>