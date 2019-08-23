package sselab.switchy.testapp;

import sselab.switchy.api.API;

public class APICallingCase {
	String code = "";
	public APICallingCase() {
		
	}
	
	public String getCode(){
		return this.code;
	}
	
	public void generateCalls(int taskID, int length){
		for(int i=0; i < length; i++){
			code += "Label" + taskID + (i+1) + ":\n";
			code += "pc[tid]++;\n";
			code += "api_kind = tinfo[tid-1][current_pc[tid]-1].api_kind;\n";
			code += "reftask = tinfo[tid-1][current_pc[tid]-1].reftask;\n";
			code += "resid = tinfo[tid-1][current_pc[tid]-1].resid;\n";
			code += "eventid = tinfo[tid-1][current_pc[tid]-1].eventid;\n";
			code += "current_pc[tid]++;\n";
			
			code += "switch(api_kind){\n";
			
			for(int j = 0; j < API.values().length; j++){
				API api = API.values()[j];
				code += "case " + "API_" + api.name() + ":\n";
				code += "flag = " + api.name() + "(";
				if(api == API.Schedule || api == API.TerminateTask)
					code += "tid";
				else if(api == API.GetResource || api == API.ReleaseResource)
					code += "tid, resid";
				else if(api == API.ActivateTask || api == API.ChainTask)
					code += "tid, reftask";
				else if(api == API.ClearEvent || api == API.WaitEvent)
					code += "tid, eventid";
				else if(api == API.SetEvent)
					code += "tid, reftask, eventid";
				
				code += ");\n break;\n";
			}
			code += "}";
			if(i == length - 1)
				code += "pc[tid] = 1;\n";
			else
				code += "if(current_tid == 0 || flag) return;\n";
					
		}
	}

}
