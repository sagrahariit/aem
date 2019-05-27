//https://helpx.adobe.com/experience-manager/using/custom_64step_dialog.html
package com.shailesh.agrahari.core.workflow.touchuidialog;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component (service=WorkflowProcess.class, property= {"process.lable = Shailesh Agrahari Logger Process"})
public class LoggerProcess implements WorkflowProcess {
	
	private static final Logger log = LoggerFactory.getLogger(LoggerProcess.class);

	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
		String singleValue = args.get("argSingle", "not set");
		String date = args.get("startdate", "not set");
		String[] multiValue = args.get("argMulti", new String[] {"not set"});
		
		log.info("---> Single Value: {}", singleValue);
        log.info("---> DATE: {}", date);
		
		
	}
	
	

}
