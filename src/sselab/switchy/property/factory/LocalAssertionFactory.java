package sselab.switchy.property.factory;

import java.util.List;

import sselab.nusek.commons.OsekApi;
import sselab.nusek.csl.Csl;
import sselab.nusek.csl.CslInPairs;
import sselab.nusek.csl.CslMustEndWith;
import sselab.nusek.csl.CslNotInBetween;
import sselab.switchy.property.Assertion;
import sselab.switchy.property.CSL;
import sselab.switchy.property.MonitoringCode;
import sselab.switchy.property.Property;

/**
 * This class is a factory class for local call constraint assertions.
 * @author SSELAB-129
 *
 */
public class LocalAssertionFactory implements PropertyFactory{

	private String ID;
	public LocalAssertionFactory() {
		// TODO Auto-generated constructor stub
	}

	public void setID(String ID){
		this.ID = ID;
	}
	
		
	@Override
	public Property createProperty(Csl csl) {
		Assertion code = null;
		if(csl instanceof CslMustEndWith){
			//MustEndWith(TerminateTask, ChainTask)
			List<OsekApi> api = ((CslMustEndWith) csl).getEndSet();
			if(api.contains(OsekApi.CHAIN_TASK) && api.contains(OsekApi.TERMINATE_TASK)){
				code = new Assertion("STATE_MEW["+ID+"] == MUST_SAFE");
			}
		}else if(csl instanceof CslInPairs){
			//InPairs(GR, RR), InPairs(WE, CE) <-- local InPairs
			OsekApi first = ((CslInPairs)csl).getFirst();
			OsekApi second = ((CslInPairs) csl).getSecond();
			if(first.equals(OsekApi.GET_RESOURCE) && second.equals(OsekApi.RELEASE_RESOURCE)){
				code = new Assertion("STATE_INP_GR_RR["+ID+"] == NEWINP_SAFE");
			}else if(first.equals(OsekApi.WAIT_EVENT) && second.equals(OsekApi.CLEAR_EVENT)){
				code = new Assertion("STATE_INP_WE_CE["+ID+"] == EVENTINP_SAFE");
			}
		}else if(csl instanceof CslNotInBetween){
			//NotInBetween(GR, {Scheduling APIs}, RR)
			OsekApi first = ((CslNotInBetween) csl).getFirst();
			OsekApi second = ((CslNotInBetween) csl).getSecond();
			List<OsekApi> exclusive = ((CslNotInBetween) csl).getExclusiveSet();
			OsekApi[] apis = {OsekApi.ACTIVATE_TASK, OsekApi.SCHEDULE, OsekApi.TERMINATE_TASK,
					OsekApi.CHAIN_TASK, OsekApi.WAIT_EVENT, OsekApi.SET_EVENT};
			if(exclusive.contains(apis)){
				if(first.equals(OsekApi.GET_RESOURCE) && second.equals(OsekApi.RELEASE_RESOURCE)){
					code = new Assertion("STATE_NIB_GR_RR["+ID+"] == NIB_SAFE");
				}
			}
		}
		return code;
	}

}
