package cn.kanejin.webop;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.operation.Operation;
import cn.kanejin.webop.operation.OperationContext;
import cn.kanejin.webop.operation.OperationContextImpl;
import cn.kanejin.webop.operation.OperationMapping;

/**
 * @version $Id: Controller.java 166 2017-09-13 08:54:09Z Kane $
 * @author Kane Jin
 */
public class Controller extends HttpServlet {
	
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	
	private static final long serialVersionUID = -3440262882601905792L;
	
	@Override
    public void init() throws ServletException {
        super.init();
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		
		Operation op = OperationMapping.getInstance().getOperation(req);

		if(op == null) {
			log.warn("No operation found for HTTP request with URI [{}]", req.getRequestURI());
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return ;
		}
		
		log.info("URI = [{}], Operation URI = [{}]", req.getRequestURI(), op.getUri());
		log.info("User-Agent = [{}]", req.getHeader("User-Agent"));
		
		OperationContext ctx = new OperationContextImpl(req, res, getServletContext());
		
		op.operate(ctx);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		doGet(req, res);
	}
}
