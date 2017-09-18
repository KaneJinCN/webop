<%@page language="java" pageEncoding="UTF-8" isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Return 4 Sample</title>
</head>
<body>
<h3>Return 4 Sample</h3>
<textarea cols="100" rows="20" style="border:none;">
<operation id="sample.return" name="Return Action Sample">
  <step id="stepReturn"
    class="cn.kanejin.webop.sample.opstep.ReturnActionStep">
    <return-action>
      <if return="0"><forward page="/jsp/sample/return0.jsp"/></if>
      <if return="1"><forward page="/jsp/sample/return1.jsp"/></if>
      <if return="2"><forward page="/jsp/sample/return2.jsp"/></if>
      <if return="3"><forward page="/jsp/sample/return3.jsp"/></if>
      <if return="4"><forward page="/jsp/sample/return4.jsp"/></if>
      <else><error/></else>
    </return-action>
  </step>
</operation>
</textarea>
</body>
</html>