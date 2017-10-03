package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.Converter;
import cn.kanejin.webop.core.ConverterMapping;
import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.WebopContext;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import java.io.IOException;

import static cn.kanejin.commons.util.StringUtils.isNotEmpty;

/**
 * @author Kane Jin
 */
public abstract class JsonConvertAction extends EndReturnAction {

    protected final String attribute;
    protected final String converter;

    public JsonConvertAction(String attribute, String converter) {
        this.attribute = attribute;
        this.converter = converter;
    }

    protected String convertToJson(OperationContext oc) throws ServletException, IOException {

        Object jsonObj = oc.getAttribute(attribute);

        if (jsonObj == null)
            throw new ServletException("Attribute data is required");

        if (isNotEmpty(converter)) {
            Converter c = WebopContext.get().getConverterMapping().get(converter);
            return c.convert(jsonObj);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonObj);
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public String getConverter() {
        return converter;
    }
}
