package ozone.owf.grails.controllers

import ozone.owf.grails.OwfException
import grails.converters.JSON
import org.apache.commons.lang.time.StopWatch
import javax.servlet.http.HttpServletResponse

class StackController extends BaseOwfRestController {
    
    def stackService
    def marketplaceService

    def list = {
        def statusCode
        def jsonResult
        StopWatch stopWatch = null;
        
        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: list - params:$params");
        }
        try {
            def result = stackService.list(params)
            statusCode = HttpServletResponse.SC_OK
            jsonResult = result as JSON
        }
        catch (OwfException owe) {
            handleError(owe)
            statusCode = owe.exceptionType.normalReturnCode
            jsonResult = "Error during list: " + owe.exceptionType.generalMessage + " " + owe.message
        }
        
        renderResult(jsonResult, statusCode)
        
        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: list in " + stopWatch);
        }
    }
    
    def createOrUpdate = {
        def jsonResult
        StopWatch stopWatch = null;
        
        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: createOrUpdate");
        }
        try {
            def result
            if (params?.addExternalStackToUser) {
                result = marketplaceService.addExternalStackToUser(params)
            } else {
                result = stackService.createOrUpdate(params)
            }
            jsonResult = [msg: result as JSON, status: HttpServletResponse.SC_OK ]
        }
        catch (def e) {
            jsonResult = handleError(e)
        }
        
        renderResult(jsonResult)
        
        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: createOrUpdate in " + stopWatch);
        }
        
    }

    def addPage = {
        def result
        int statusCode
        try {
            result = stackService.addPage(params)
            result = getJsonResult(result, 'dashboard', params)
            statusCode = HttpServletResponse.SC_OK
        }
        catch (OwfException owe) {
            handleError(owe)
            statusCode = owe.exceptionType.normalReturnCode
            result = "Error during create: " + owe.exceptionType.generalMessage + " " + owe.message
        }
        renderResult(result, statusCode)
    }
    
    def delete = {
        def jsonResult
        StopWatch stopWatch = null;
        
        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: delete");
        }
        try {
            def result = stackService.delete(params)
            jsonResult = [msg: result as JSON, status: HttpServletResponse.SC_OK]
        }
        catch (def e) {
            jsonResult = handleError(e)
        }
        
        renderResult(jsonResult)
        
        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: delete in " + stopWatch);
        }
    }

    def share = {

        def jsonResult

        StopWatch stopWatch = null;

        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: share");
        }

        try {

            jsonResult =  stackService.share(params)

        } catch (def e) {

            jsonResult = handleError(e)

        }

        renderResult(jsonResult, HttpServletResponse.SC_OK)

        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: share in " + stopWatch);
        }


    }
    
    def export = {
        def result
        StopWatch stopWatch = null;
        
        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: export");
        }
        try {
            def filename = params.filename ? params.filename :"stack_descriptor"

            // Set filename/ID for audit logging
            request.setAttribute("fileName", filename+".html")

            // Set content type for audit logging
            response.setContentType("text/json")

            //Set content-disposition so browser is expecting a file
            response.setHeader("Content-disposition", "attachment; filename=" + filename + ".html")

            def stackDescriptor = stackService.export(params)

            // Set fileSize for audit logging
            request.setAttribute("fileSize", (stackDescriptor.getBytes("UTF-8")).length)

            response.outputStream.write(stackDescriptor.getBytes("UTF-8"))
            response.outputStream.flush()

        }
        catch (def e) {
            //Set content-disposition back to text to relay the error
            response.setHeader("Content-disposition", "")

            result = handleError(e)
            renderResult(result)
        }
        
        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: export in " + stopWatch);
        }
    }

    def importStack = {
        def jsonResult
        StopWatch stopWatch = null;
        
        if (log.isInfoEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
            log.info("Executing stackService: import");
        }
        try {
            def result = stackService.importStack(params)
            jsonResult = [msg: result as JSON, status: HttpServletResponse.SC_OK]
        }
        catch (def e) {
            jsonResult = handleError(e)
        }

        renderResult(jsonResult)
        
        if (log.isInfoEnabled()) {
            stopWatch.stop();
            log.info("Executed stackService: import in " + stopWatch);
        }
    }
	
	def restore = {
		def jsonResult
		StopWatch stopWatch = null;
		
		if (log.isInfoEnabled()) {
			stopWatch = new StopWatch();
			stopWatch.start();
			log.info("Executing stackService: restore");
		}
		try {
			def result = stackService.restore(params)
			jsonResult = [msg: result as JSON, status: HttpServletResponse.SC_OK]
		}
		catch (def e) {
			jsonResult = handleError(e)
		}
		
		renderResult(jsonResult)
		
		if (log.isInfoEnabled()) {
			stopWatch.stop();
			log.info("Executed stackService: restore in " + stopWatch);
		}
	}

    def listGroups = {
        def jsonResult

        if (params.containsKey('id')) {
            ozone.owf.grails.domain.Stack stack = ozone.owf.grails.domain.Stack.get(params.id)
            jsonResult = [msg: stack.groups as JSON, status: HttpServletResponse.SC_OK]
        }
        else {
            jsonResult = [msg: "Missing stack id", status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR]
        }
        renderResult(jsonResult)
    }
}
