package sselab.switchy.property.factory;

import java.util.List;

import sselab.nusek.commons.OsekApi;
import sselab.nusek.csl.Csl;
import sselab.nusek.csl.CslInPairs;
import sselab.nusek.csl.CslMustEndWith;
import sselab.nusek.csl.CslNotInBetween;
import sselab.switchy.property.CSL;
import sselab.switchy.property.MonitoringCode;
import sselab.switchy.property.Property;

public class MonitoringCodeFactory implements PropertyFactory{

	public MonitoringCodeFactory() {
		// TODO Auto-generated constructor stub
	}
	
	private String taskID;

	public String getTaskID() {
		return taskID;
	}


	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}


	@Override
	public Property createProperty(Csl csl) {
		MonitoringCode code = null;
		if(csl instanceof CslMustEndWith){
			//Generates MustEndWith(TerminateTask, ChainTask) monitoring code.
			List<OsekApi> api = ((CslMustEndWith) csl).getEndSet();
			if(api.contains(OsekApi.CHAIN_TASK) && api.contains(OsekApi.TERMINATE_TASK)){
				code = new MonitoringCode(CSL.MEW);
			}
		}else if(csl instanceof CslInPairs){
			//InPairs monitoring code.
			OsekApi first = ((CslInPairs)csl).getFirst();
			OsekApi second = ((CslInPairs) csl).getSecond();
			//InPairs(GetResource, ReleaseResource)
			if(first.equals(OsekApi.GET_RESOURCE) && second.equals(OsekApi.RELEASE_RESOURCE)){
				code = new MonitoringCode(CSL.IN_PAIRS_GR_RR);
			} //InPairs(ActivateTask, TerminateTask)
			else if(first.equals(OsekApi.ACTIVATE_TASK) && second.equals(OsekApi.TERMINATE_TASK)){
				code = new MonitoringCode(CSL.IN_PAIRS_AT_TT);
			} //InPairs(WaitEvent, SetEvent)
			else if(first.equals(OsekApi.WAIT_EVENT) && second.equals(OsekApi.SET_EVENT)){
				code = new MonitoringCode(CSL.IN_PAIRS_WE_SE);
			} //InPairs(WaitEvent, ClearEvent)
			else if(first.equals(OsekApi.WAIT_EVENT) && second.equals(OsekApi.CLEAR_EVENT)){
				code = new MonitoringCode(CSL.IN_PAIRS_WE_CE);
			}
		}else if(csl instanceof CslNotInBetween){
			/* NotInBetween(GetResource, 
				{ActivateTask, Schedule, TerminateTask, ChainTask, WaitEvent, SetEvent},
			     	ReleaseResource)
			     	*/
			OsekApi first = ((CslNotInBetween) csl).getFirst();
			OsekApi second = ((CslNotInBetween) csl).getSecond();
			List<OsekApi> exclusive = ((CslNotInBetween) csl).getExclusiveSet();
			OsekApi[] apis = {OsekApi.ACTIVATE_TASK, OsekApi.SCHEDULE, OsekApi.TERMINATE_TASK,
					OsekApi.CHAIN_TASK, OsekApi.WAIT_EVENT, OsekApi.SET_EVENT};
			if(exclusive.contains(apis)){
				if(first.equals(OsekApi.GET_RESOURCE) && second.equals(OsekApi.RELEASE_RESOURCE)){
					code = new MonitoringCode(CSL.NIB_GR_RR);
				}
			}
		}
		//Otherwise, code is still null.
		
		
		return code;
	}

}
