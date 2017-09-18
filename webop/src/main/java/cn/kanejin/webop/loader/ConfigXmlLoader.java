package cn.kanejin.webop.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import cn.kanejin.webop.Interceptor;
import cn.kanejin.webop.interceptor.InterceptorMapping;
import cn.kanejin.webop.operation.Operation;
import cn.kanejin.webop.operation.OperationException;
import cn.kanejin.webop.operation.OperationMapping;
import cn.kanejin.webop.operation.OperationStepSpec;
import cn.kanejin.webop.operation.action.AttributeReturnAction;
import cn.kanejin.webop.operation.action.BackReturnAction;
import cn.kanejin.webop.operation.action.ErrorReturnAction;
import cn.kanejin.webop.operation.action.ForwardReturnAction;
import cn.kanejin.webop.operation.action.JsonReturnAction;
import cn.kanejin.webop.operation.action.JsonpReturnAction;
import cn.kanejin.webop.operation.action.NextReturnAction;
import cn.kanejin.webop.operation.action.OperationReturnAction;
import cn.kanejin.webop.operation.action.RedirectReturnAction;
import cn.kanejin.webop.operation.action.ResponseReturnAction;
import cn.kanejin.webop.operation.action.ReturnAction;
import cn.kanejin.webop.operation.action.ScriptReturnAction;
import cn.kanejin.webop.operation.action.StepReturnAction;
import cn.kanejin.webop.operation.action.TextReturnAction;
import cn.kanejin.webop.operation.action.XmlReturnAction;

/**
 * @version $Id: OperationXmlLoader.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class ConfigXmlLoader {
	private static Logger log = LoggerFactory.getLogger(ConfigXmlLoader.class);
	
	private static ConfigXmlLoader loader;
	
	public static ConfigXmlLoader getInstance() {
		if (loader == null)
			loader = new ConfigXmlLoader();
		return loader;
	}
	
	public void load(String[] files) {

		if (files == null || files.length == 0)
			return;
		
		for (String file : files) {
			loadConfig(file);
		}
	}
	
	private void loadConfig(String fileName) {
		log.debug("Loading configurations from {}", fileName);

		Document doc = loadDocFromFile(fileName);
		
		NodeList nodes = doc.getDocumentElement().getChildNodes();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			if (node.getNodeName().equals("operation")) {
				loadOperations(node);
			} else if (node.getNodeName().equals("interceptor")) {
				loadInterceptors(node);
			}
		}
	}
	
	private void loadOperations(Node opNode) {
		NamedNodeMap opAttr = opNode.getAttributes();
		
		String opUri = null;
		
		if (opAttr.getNamedItem("uri") != null) {
			opUri = opAttr.getNamedItem("uri").getNodeValue();
		}
		
		// FIXME 过渡，如果没有配置uri，以前的id仍然有效，迁移完成后删除这段
		if (isNull(opUri)) {
			String opId = opAttr.getNamedItem("id").getNodeValue();
			
			opUri = "/" + opId.replaceAll("\\.", "/");
		}
		
		if (isNull(opUri))
			throw new OperationException("Operation's attribute uri is required");
		
		if (OperationMapping.getInstance().get(opUri) != null)
			throw new OperationException("Operation [" + opUri + "] is defined more than once");

		String opName = "";
		if (opAttr.getNamedItem("name") != null) {
			opName = opAttr.getNamedItem("name").getNodeValue();
		}
		
		if (log.isInfoEnabled()) {
			String opLog = String.format("URI=[%-30s] NAME=[%s]", opUri, opName);
			log.info("Loading Operation {}", opLog);
		}
		

		Operation op = new Operation(opUri, opName, parseInterceptors(opNode), parseSteps(opNode));

		// TODO 这里可以添加operation的检验

		OperationMapping.getInstance().put(op.getUri(), op);
	}
	
	private void loadInterceptors(Node itNode) {
		NamedNodeMap itAttr = itNode.getAttributes();
		
		String itId = itAttr.getNamedItem("id").getNodeValue();
		String itClass = itAttr.getNamedItem("class").getNodeValue();
		
		if (isNull(itId))
			throw new OperationException("Interceptor's attribute id is required");
		
		if (isNull(itClass))
			throw new OperationException("Interceptor's attribute class is required");

		if (InterceptorMapping.getInstance().get(itId) != null)
			throw new OperationException("Interceptor [" + itId + "] is defined more than once");

		log.info("Loading Interceptor [{}] for class [{}]", itId, itClass);
		
		NodeList childNodes = itNode.getChildNodes();
		
		Map<String, String> params = new HashMap<String, String>();
		for (int j = 0; j < childNodes.getLength(); j++) {
			if (childNodes.item(j).getNodeName().equals("init-params")) {
				
				NodeList paramNodes = childNodes.item(j).getChildNodes();
				
				for (int m = 0; m < paramNodes.getLength(); m++) {
					NamedNodeMap paramAttr = paramNodes.item(m).getAttributes();
					params.put(
							paramAttr.getNamedItem("name").getNodeValue(),
							paramAttr.getNamedItem("value").getNodeValue());
				}
			}
		}
		
		try {
			Interceptor it = (Interceptor) Class.forName(itClass).newInstance();
			it.init(params);
			InterceptorMapping.getInstance().put(itId, it);
		} catch (Exception e) {
			throw new RuntimeException("Initializing Interceptor Exception", e);
		}
	}

	private Document loadDocFromFile(String fileName) {
		Document doc = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setNamespaceAware(true);
		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
							 "http://www.w3.org/2001/XMLSchema");
		
	    if (log.isDebugEnabled()) {
	      log.debug("Using JAXP provider [{}]", factory.getClass().getName());
	    }
	    
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			builder.setEntityResolver(ConfigEntityResolver.getInstance());
			
			builder.setErrorHandler(new ErrorHandler() {
				
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					log.warn(exception.getMessage());
				}
				
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw new OperationException("A fatal error occurs when parsing operation config file", exception);
				}
				
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw new OperationException("An error occurs when parsing operation config file", exception);
				}
			});
			doc = builder.parse(fileName);
			doc.normalize();
		} catch (Exception e) {
			throw new OperationException("Unable to load xml file [" + fileName + "]", e);
		}
	    
		return doc;
	}
	
	private List<String> parseInterceptors(Node opNode) {
		List<String> itRefs = new ArrayList<String>();
		
		NodeList stepNodes = opNode.getChildNodes();
		
		for (int i = 0; i < stepNodes.getLength(); i++) {
			Node stepNode = stepNodes.item(i);
			
			if (stepNode.getNodeName().equals("interceptor")) {
				NamedNodeMap paramAttr = stepNode.getAttributes();
				itRefs.add(paramAttr.getNamedItem("ref").getNodeValue());
			}
		}
		
		return itRefs;
	}
	
	private List<OperationStepSpec> parseSteps(Node opNode) {
		List<OperationStepSpec> opDefs = new ArrayList<OperationStepSpec>();
		
		NodeList stepNodes = opNode.getChildNodes();
		
		for (int i = 0; i < stepNodes.getLength(); i++) {
			Node stepNode = stepNodes.item(i);
			
			if (stepNode.getNodeName().equals("step"))
				opDefs.add(parseStep(stepNode));
		}
		
		return opDefs;
	}

	private OperationStepSpec parseStep(Node stepNode) {
		NamedNodeMap stepAttr = stepNode.getAttributes();

		String stepId = stepAttr.getNamedItem("id").getNodeValue();
		if (isNull(stepId))
			throw new OperationException("Step's id is required");

		String stepClass = stepAttr.getNamedItem("class").getNodeValue();
		if (isNull(stepClass))
			throw new OperationException("Step[" + stepId + "]'s class is required");
		
		OperationStepSpec opStepDef = new OperationStepSpec(stepId, stepClass);
		
		NodeList stepChildNodes = stepNode.getChildNodes();
		
		for (int i = 0; i < stepChildNodes.getLength(); i++) {
			String nodeName = stepChildNodes.item(i).getNodeName();
			if (nodeName.equals("init-params")) {
				parseInitParam(stepChildNodes.item(i), opStepDef);
			} else if (nodeName.equals("return-action")) {
				parseReturnAction(stepChildNodes.item(i), opStepDef);
			}
		}
		return opStepDef;
	}
	
	private void parseInitParam(Node initParamNode, OperationStepSpec opStepDef) {
		NodeList paramNodes = initParamNode.getChildNodes();
		
		for (int j = 0; j < paramNodes.getLength(); j++) {
			NamedNodeMap paramAttr = paramNodes.item(j).getAttributes();
			opStepDef.addInitParam(
					paramAttr.getNamedItem("name").getNodeValue(),
					paramAttr.getNamedItem("value").getNodeValue());
		}
	}

	private void parseReturnAction(Node returnActionNode, OperationStepSpec opStepDef) {
		NodeList actionNodes = returnActionNode.getChildNodes();

		for (int j = 0; j < actionNodes.getLength(); j++) {
			Node actionNode = actionNodes.item(j);
			
			String returnValue = null;
			String actionValue = null;
			ReturnAction action = null;
			
			if (actionNode.getNodeName().equals("if")) {
				returnValue = actionNode.getAttributes()
										.getNamedItem("return").getNodeValue();
			} else if (actionNode.getNodeName().equals("else")) {
				returnValue = "-";
			}

			actionValue = actionNode.getFirstChild().getNodeName();
			NamedNodeMap actionAttr = actionNode.getFirstChild().getAttributes();

			if (actionValue.equals("next")) {
				action = NextReturnAction.getInstance();
			} else if (actionValue.equals("step")) {
				action = StepReturnAction.getInstance(actionAttr.getNamedItem("id").getNodeValue());
			} else if (actionValue.equals("forward")) {
				action = ForwardReturnAction.getInstance(actionAttr.getNamedItem("page").getNodeValue());
			} else if (actionValue.equals("redirect")) {
				action = RedirectReturnAction.getInstance(actionAttr.getNamedItem("page").getNodeValue());
			} else if (actionValue.equals("operation")) {
				
				// FIXME 过渡，如果没有配置uri，以前的id仍然有效，迁移完成后删除这段
				Node uriNode = actionAttr.getNamedItem("uri");
				String uri = "";
				if (uriNode != null) {
					uri = uriNode.getNodeValue();
				} else {
					Node idNode = actionAttr.getNamedItem("id");
					
					uri = "/" + idNode.getNodeValue().replaceAll("\\.", "/");
				}

				action = OperationReturnAction.getInstance(
						uri,
						actionAttr.getNamedItem("params") != null ? actionAttr.getNamedItem("params").getNodeValue() : null);
			} else if (actionValue.equals("script")) {
				action = ScriptReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionValue.equals("attribute")) {
				action = AttributeReturnAction.getInstance(actionAttr.getNamedItem("attr").getNodeValue());
			} else if (actionValue.equals("text")) {
				action = TextReturnAction.getInstance(actionAttr.getNamedItem("value").getNodeValue());
			} else if (actionValue.equals("json")) {
				action = JsonReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionValue.equals("jsonp")) {
				action = JsonpReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("callback") != null ? actionAttr.getNamedItem("callback").getNodeValue() : null,
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionValue.equals("xml")) {
				action = XmlReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter").getNodeValue()
				);
			} else if (actionValue.equals("response")) {
				action = ResponseReturnAction.getInstance(actionAttr.getNamedItem("status").getNodeValue());
			} else if (actionValue.equals("back")) {
				action = BackReturnAction.getInstance();
			} else if (actionValue.equals("error")) {
				action = ErrorReturnAction.getInstance();
			}
			
			opStepDef.addReturnAction(returnValue, action);
		}
	}
	
	private static boolean isNull(String str) {
		return (str == null) || (str.trim().equals(""));
	}
}
