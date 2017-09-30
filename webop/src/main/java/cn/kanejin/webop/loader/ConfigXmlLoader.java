package cn.kanejin.webop.loader;

import cn.kanejin.commons.util.NumberUtils;
import cn.kanejin.webop.core.*;
import cn.kanejin.webop.core.action.*;
import cn.kanejin.webop.core.def.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

import static cn.kanejin.commons.util.StringUtils.isBlank;
import static cn.kanejin.commons.util.StringUtils.isNotBlank;

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
		String[] opMethods = null;
		
		if (opAttr.getNamedItem("uri") != null) {
			opUri = opAttr.getNamedItem("uri").getNodeValue();
		}

		if (opAttr.getNamedItem("method") != null) {
			String methodValue = opAttr.getNamedItem("method").getNodeValue();
			if (isNotBlank(methodValue)) {
				opMethods = methodValue.trim().split("\\s+");
			}
		}

		// FIXME 过渡，如果没有配置uri，以前的id仍然有效，迁移完成后删除这段
		if (isBlank(opUri)) {
			String opId = opAttr.getNamedItem("id").getNodeValue();
			
			opUri = "/" + opId.replaceAll("\\.", "/");
		}
		
		if (isBlank(opUri))
			throw new OperationException("Operation's attribute uri is required");

		OperationMapping operationMapping = WebopContext.get().getOperationMapping();

		if (opMethods == null) {
			if (operationMapping.exists(opUri, null))
				throw new OperationException(
						"Operation [" + opUri + "] is defined more than once");
		} else {
			for (String method : opMethods) {
				if (operationMapping.exists(opUri, method))
					throw new OperationException(
							"Operation [" + opUri + "], Method [" + method + "] is defined more than once");
			}
		}

		String opName = "";
		if (opAttr.getNamedItem("name") != null) {
			opName = opAttr.getNamedItem("name").getNodeValue();
		}
		
		if (log.isInfoEnabled()) {
			String methodString = opMethods == null || opMethods.length == 0 ? "[ALL]" : Arrays.toString(opMethods);
			String opLog = String.format("URI=[%-30s] METHOD=%s NAME=[%s]", opUri, methodString, opName);
			log.info("Loading Operation {}", opLog);
		}
		
		// TODO 这里可以添加operation的检验

		operationMapping.put(opUri, opMethods,
				new OperationDef(opUri, opName, parseCache(opNode), parseInterceptors(opNode), parseSteps(opNode)));
	}

	private void loadInterceptors(Node itNode) {
		NamedNodeMap itAttr = itNode.getAttributes();
		
		String itId = itAttr.getNamedItem("id").getNodeValue();
		String itClass = itAttr.getNamedItem("class").getNodeValue();
		
		if (isBlank(itId))
			throw new OperationException("Interceptor's attribute id is required");
		
		if (isBlank(itClass))
			throw new OperationException("Interceptor's attribute class is required");

		InterceptorMapping interceptorMapping = WebopContext.get().getInterceptorMapping();

		if (interceptorMapping.exists(itId))
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

		interceptorMapping.put(
				itId, new InterceptorDef(itId, itClass, params));
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

	private CacheDef parseCache(Node opNode) {

		NodeList childNodes = opNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("cache")) {

				return new CacheDef(parseExpiry(childNode), parseKeyFields(childNode));
			}
		}

		return null;
	}

	private CacheExpiryDef parseExpiry(Node cacheNode) {
		NodeList childNodes = cacheNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("expiry")) {

				Node expiryNode = childNode.getFirstChild();

				String type = expiryNode.getNodeName();

				String unit = null;
				Long time = null;
				if (type.equals("ttl") || type.equals("tti")) {
					unit = expiryNode.getAttributes().getNamedItem("unit").getNodeValue();
					time = NumberUtils.toLong(expiryNode.getTextContent(), 5L);
				}

				return new CacheExpiryDef(type, unit, time);
			}
		}

		return new CacheExpiryDef("ttl", "minutes", 5L);
	}

	private String[] parseKeyFields(Node cacheNode) {
		List<String> fields = new ArrayList<String>();

		NodeList childNodes = cacheNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("key-field")) {
				fields.add(childNode.getTextContent());
			}
		}
		return fields.toArray(new String[0]);

	}
	
	private List<String> parseInterceptors(Node opNode) {
		List<String> itRefs = new ArrayList<String>();
		
		NodeList childNodes = opNode.getChildNodes();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			
			if (childNode.getNodeName().equals("interceptor")) {
				NamedNodeMap paramAttr = childNode.getAttributes();
				itRefs.add(paramAttr.getNamedItem("ref").getNodeValue());
			}
		}
		
		return itRefs;
	}
	
	private List<OperationStepDef> parseSteps(Node opNode) {
		List<OperationStepDef> opDefs = new ArrayList<OperationStepDef>();
		
		NodeList stepNodes = opNode.getChildNodes();
		
		for (int i = 0; i < stepNodes.getLength(); i++) {
			Node stepNode = stepNodes.item(i);
			
			if (stepNode.getNodeName().equals("step"))
				opDefs.add(parseStep(stepNode));
		}
		
		return opDefs;
	}

	private OperationStepDef parseStep(Node stepNode) {
		NamedNodeMap stepAttr = stepNode.getAttributes();

		String stepId = stepAttr.getNamedItem("id").getNodeValue();
		if (isBlank(stepId))
			throw new OperationException("Step's id is required");

		String stepClass = stepAttr.getNamedItem("class").getNodeValue();
		if (isBlank(stepClass))
			throw new OperationException("Step[" + stepId + "]'s class is required");
		
		OperationStepDef opStepDef = new OperationStepDef(stepId, stepClass);
		
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
	
	private void parseInitParam(Node initParamNode, OperationStepDef opStepDef) {
		NodeList paramNodes = initParamNode.getChildNodes();
		
		for (int j = 0; j < paramNodes.getLength(); j++) {
			NamedNodeMap paramAttr = paramNodes.item(j).getAttributes();
			opStepDef.addInitParam(
					paramAttr.getNamedItem("name").getNodeValue(),
					paramAttr.getNamedItem("value").getNodeValue());
		}
	}

	private void parseReturnAction(Node returnActionNode, OperationStepDef opStepDef) {
		NodeList actionNodes = returnActionNode.getChildNodes();

		for (int j = 0; j < actionNodes.getLength(); j++) {
			Node actionNode = actionNodes.item(j);
			
			String returnValueKey = null;
			String actionType = null;
			ReturnAction action = null;
			NamedNodeMap actionAttr = null;
			
			if (actionNode.getNodeName().equals("if")) {
				returnValueKey = actionNode.getAttributes()
										.getNamedItem("return").getNodeValue();

				actionType = actionNode.getFirstChild().getNodeName();
				actionAttr = actionNode.getFirstChild().getAttributes();

			} else if (actionNode.getNodeName().equals("else")) {
				returnValueKey = "else";
				actionType = actionNode.getFirstChild().getNodeName();
				actionAttr = actionNode.getFirstChild().getAttributes();
			} else {
				returnValueKey = "always";
				actionType = actionNode.getNodeName();
				actionAttr = actionNode.getAttributes();
			}

			if (actionType.equals("next")) {
				action = NextReturnAction.getInstance();
			} else if (actionType.equals("step")) {
				action = StepReturnAction.getInstance(actionAttr.getNamedItem("id").getNodeValue());
			} else if (actionType.equals("forward")) {
				action = ForwardReturnAction.getInstance(actionAttr.getNamedItem("page").getNodeValue());
			} else if (actionType.equals("redirect")) {
				action = RedirectReturnAction.getInstance(actionAttr.getNamedItem("page").getNodeValue());
			} else if (actionType.equals("operation")) {
				String uri = actionAttr.getNamedItem("uri").getNodeValue();
				if (!uri.startsWith("/")) {
					throw new OperationException("Operation Definition Error: Uri of operation must start with '/'.");
				}

				action = OperationReturnAction.getInstance(
						uri,
						actionAttr.getNamedItem("params") != null ? actionAttr.getNamedItem("params").getNodeValue() : null);
			} else if (actionType.equals("script")) {
				action = ScriptReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("attribute")) {
				action = AttributeReturnAction.getInstance(actionAttr.getNamedItem("attr").getNodeValue());
			} else if (actionType.equals("text")) {
				action = TextReturnAction.getInstance(actionAttr.getNamedItem("value").getNodeValue());
			} else if (actionType.equals("json")) {
				action = JsonReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("jsonp")) {
				action = JsonpReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("callback") != null ? actionAttr.getNamedItem("callback").getNodeValue() : null,
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("xml")) {
				action = XmlReturnAction.getInstance(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter").getNodeValue()
				);
			} else if (actionType.equals("response")) {
				action = ResponseReturnAction.getInstance(actionAttr.getNamedItem("status").getNodeValue());
			} else if (actionType.equals("back")) {
				action = BackReturnAction.getInstance();
			} else if (actionType.equals("error")) {
				action = ErrorReturnAction.getInstance();
			}
			
			opStepDef.addReturnAction(returnValueKey, action);
		}
	}
}
