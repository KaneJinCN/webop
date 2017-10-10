package cn.kanejin.webop.loader;

import cn.kanejin.commons.util.NumberUtils;
import cn.kanejin.webop.core.InterceptorMapping;
import cn.kanejin.webop.core.OperationMapping;
import cn.kanejin.webop.core.WebopConfig;
import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.core.action.*;
import cn.kanejin.webop.core.def.*;
import cn.kanejin.webop.core.exception.IllegalConfigException;
import cn.kanejin.webop.support.PathPatternResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
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

	public void load(ServletContext servletContext, String[] locations) {

		if (locations == null || locations.length == 0)
			return;

		for (String location : locations) {
			String[] files = parseXmlLocations(servletContext, location);

			if (files == null || files.length == 0)
				continue;

			// 把全部的config先加载
			for (String file : files) {
				try {
					loadConfig(servletContext, file);
				} catch (IOException | SAXException | ParserConfigurationException e) {
					log.warn("Can't load config file: " + file, e);
				}
			}

			// 然后再加载其它的xml文件
			for (String file : files) {
				try {
					loadXml(servletContext, file);
				} catch (IOException | SAXException | ParserConfigurationException e) {
					log.warn("Can't load xml file: " + file, e);
				}
			}
		}
	}

	/**
	 * 解析通配符路径，把所有匹配的文件全部找出
 	 */
	private String[] parseXmlLocations(ServletContext sc, String configLocation) {
		String locationPath = configLocation.substring(0, configLocation.lastIndexOf("/"));
		while (locationPath.contains("*") || locationPath.contains("?")) {
			locationPath = locationPath.substring(0, locationPath.lastIndexOf("/"));
		}

		String pattern = configLocation.substring(locationPath.length() + 1);
		String basePath = sc.getRealPath(locationPath);

		try {
			return PathPatternResolver.resolve(basePath, pattern);
		} catch (IOException e) {
			log.warn("Fail to parse location [" + basePath + "] : " + e.getMessage());
			return null;
		}
	}

	private void loadConfig(ServletContext sc, String fileName) throws IOException, SAXException, ParserConfigurationException {
		Document doc = loadDocFromFile(fileName);

		NodeList nodes = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals("config")) {
				log.debug("Loading <config> from {}", fileName);

				parseConfig(sc, node);
			}
		}
	}

	private void loadXml(ServletContext sc, String fileName) throws IOException, SAXException, ParserConfigurationException {
		log.debug("Loading webop xml from {}", fileName);

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

	private void parseConfig(ServletContext sc, Node configNode) {
		WebopConfig config = WebopContext.get().getWebopConfig();

		NodeList nodes = configNode.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeName().equals("charset")) {
				config.setCharset(node.getTextContent());
			} else if (node.getNodeName().equals("view-type-default")) {
				config.setDefaultViewType(node.getTextContent());
			} else if (node.getNodeName().equals("jsp-renderer")) {
				parseJspRenderer(sc, config, node);
			} else if (node.getNodeName().equals("freemarker-renderer")) {
				parseFreemarkerRenderer(sc, config, node);
			} else if (node.getNodeName().equals("resource-provider")) {
				config.setResourceProvider(node.getAttributes().getNamedItem("class").getNodeValue());
			}
		}
	}

	private void parseJspRenderer(ServletContext sc, WebopConfig config, Node node) {

		NodeList nodeList = node.getChildNodes();

		String prefix = null;
		String suffix = null;
		String contentType = null;

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (n.getNodeName().equals("prefix")) {
				prefix = n.getTextContent();
			} else if (n.getNodeName().equals("suffix")) {
				suffix = n.getTextContent();
			} else if (n.getNodeName().equals("content-type")) {
				contentType = n.getTextContent();
			}
		}

		config.setJspViewRenderer(sc, prefix, suffix, contentType);
	}

	private void parseFreemarkerRenderer(ServletContext sc, WebopConfig config, Node node) {

		NodeList nodeList = node.getChildNodes();

		String renderClass = node.getAttributes().getNamedItem("class").getNodeValue();

		String prefix = null;
		String suffix = null;
		String contentType = null;
		String templatePath = null;
		boolean noCache = false;
		Integer bufferSize = null;
		boolean exceptionOnMissingTemplate = false;
		String metaInfTldSources = null;
		String classpathTlds = null;

		Properties settings = null;

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (n.getNodeName().equals("prefix")) {
				prefix = n.getTextContent();
			} else if (n.getNodeName().equals("suffix")) {
				suffix = n.getTextContent();
			} else if (n.getNodeName().equals("content-type")) {
				contentType = n.getTextContent();
			} else if (n.getNodeName().equals("template-path")) {
				templatePath = n.getTextContent();
			} else if (n.getNodeName().equals("no-cache")) {
				noCache = Boolean.valueOf(n.getTextContent());
			} else if (n.getNodeName().equals("buffer-size")) {
				bufferSize = NumberUtils.toInt(n.getTextContent(), null);
			} else if (n.getNodeName().equals("exception-on-missing-template")) {
				exceptionOnMissingTemplate = Boolean.valueOf(n.getTextContent());
			} else if (n.getNodeName().equals("meta-inf-tld-sources")) {
				metaInfTldSources = n.getTextContent();
			} else if (n.getNodeName().equals("classpath-tlds")) {
				classpathTlds = n.getTextContent();
			} else if (n.getNodeName().equals("settings")) {
				settings = parseSettings(sc, n);
			}
		}

		config.setFreemarkerViewRenderer(
				sc, renderClass,
				prefix, suffix, contentType,
				templatePath,noCache,bufferSize, exceptionOnMissingTemplate,
				metaInfTldSources, classpathTlds, settings);
	}

	private Properties parseSettings(ServletContext sc, Node node) {
		NodeList nodeList = node.getChildNodes();

		Properties settings = new Properties();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n.getNodeName().equals("props-file")) {
				settings = parseSettingsFromFile(sc, n.getTextContent());
			} else if (n.getNodeName().equals("props")) {
				settings = parseSettingsFromNode(sc, n);
			}
		}

		return settings;
	}

	private Properties parseSettingsFromFile(ServletContext sc, String file) {
		Properties ps = new Properties();

		try {
			ps.load(sc.getResourceAsStream(file));
		} catch (IOException e) {
			throw new IllegalConfigException("Can't load properties file: " + file, e);
		}

		return ps;
	}

	private Properties parseSettingsFromNode(ServletContext sc, Node node) {
		NodeList nodeList = node.getChildNodes();

		Properties ps = new Properties();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (!n.getNodeName().equals("prop")) {
				continue;
			}

			String key = n.getAttributes().getNamedItem("key").getNodeValue();
			String value = n.getTextContent();

			ps.setProperty(key, value);
		}

		return ps;
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

		OperationMapping operationMapping = WebopContext.get().getOperationMapping();

		if (opMethods == null) {
			if (operationMapping.exists(opUri, null)) {
				throw new IllegalConfigException(
						"Operation [" + opUri + "] is defined more than once");
			}
		} else {
			for (String method : opMethods) {
				if (operationMapping.exists(opUri, method)) {
					throw new IllegalConfigException(
							"Operation [" + opUri + "] Method [" + method + "] is defined more than once");
				}
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
		
		InterceptorMapping interceptorMapping = WebopContext.get().getInterceptorMapping();

		if (interceptorMapping.exists(itId))
			throw new IllegalConfigException(
					"Interceptor [" + itId + "] is defined more than once");

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

	private Document loadDocFromFile(String fileName) throws ParserConfigurationException, IOException, SAXException {

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

		DocumentBuilder builder = factory.newDocumentBuilder();

		builder.setEntityResolver(ConfigEntityResolver.getInstance());

		builder.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				log.warn(exception.getMessage());
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}
		});
		Document doc = builder.parse(fileName);
		doc.normalize();

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
		String stepClass = stepAttr.getNamedItem("class").getNodeValue();

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
				action = NextReturnAction.build();
			} else if (actionType.equals("step")) {
				action = StepReturnAction.build(actionAttr.getNamedItem("id").getNodeValue());
			} else if (actionType.equals("forward")) {
				String type = WebopContext.get().getWebopConfig().getDefaultViewType();
				if (actionAttr.getNamedItem("type") != null) {
					type = actionAttr.getNamedItem("type").getNodeValue();
				}
				String page = actionAttr.getNamedItem("page").getNodeValue();

				action = ForwardReturnAction.build(type, page);
			} else if (actionType.equals("redirect")) {
				action = RedirectReturnAction.build(actionAttr.getNamedItem("page").getNodeValue());
			} else if (actionType.equals("operation")) {
				String uri = actionAttr.getNamedItem("uri").getNodeValue();
				if (!uri.startsWith("/")) {
					throw new IllegalConfigException("Operation[" + uri +
							"] Definition Error: Uri of operation must start with '/'.");
				}

				action = OperationReturnAction.build(
						uri,
						actionAttr.getNamedItem("params") != null ? actionAttr.getNamedItem("params").getNodeValue() : null);
			} else if (actionType.equals("script")) {
				action = ScriptReturnAction.build(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("attribute")) {
				action = AttributeReturnAction.build(actionAttr.getNamedItem("attr").getNodeValue());
			} else if (actionType.equals("text")) {
				action = TextReturnAction.build(actionAttr.getNamedItem("value").getNodeValue());
			} else if (actionType.equals("json")) {
				action = JsonReturnAction.build(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("jsonp")) {
				action = JsonpReturnAction.build(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("callback") != null ? actionAttr.getNamedItem("callback").getNodeValue() : null,
					actionAttr.getNamedItem("converter") != null ? actionAttr.getNamedItem("converter").getNodeValue() : null
				);
			} else if (actionType.equals("xml")) {
				action = XmlReturnAction.build(
					actionAttr.getNamedItem("attr").getNodeValue(),
					actionAttr.getNamedItem("converter").getNodeValue()
				);
			} else if (actionType.equals("response")) {
				action = ResponseReturnAction.build(actionAttr.getNamedItem("status").getNodeValue());
			} else if (actionType.equals("back")) {
				action = BackReturnAction.build();
			} else if (actionType.equals("error")) {
				action = ErrorReturnAction.build();
			}
			
			opStepDef.addReturnAction(returnValueKey, action);
		}
	}
}
