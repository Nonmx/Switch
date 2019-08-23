package sselab.switchy.property;

import sselab.switchy.api.API;

public class MonitoringCode extends Property{

	private CSL csl;
	private String statement;
	private String caller;
	private String callee;
	private String param;
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	private API api;

	public API getApi() {
		return api;
	}

	public void setApi(API api) {
		this.api = api;
	}

	public MonitoringCode() {
	}
	
	public MonitoringCode(CSL csl){
		this.csl = csl;
	}

	public CSL getCsl() {
		return csl;
	}

	public void setCsl(CSL csl) {
		this.csl = csl;
	}
		
	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	@Override
	public String getStatement() {
		this.statement = generateCode();
		return this.statement;
	}
	
	private String generateCode(){
		String temp = "";
		switch(csl){
		case MEW:
			temp = "mustEndWith(&STATE_MEW["+getCaller()+"], API_"+ getApi().name()+");\n";
			break;
		case IN_PAIRS_GR_RR:
			temp = "inpairs_GR_RR(&STATE_INP_GR_RR["+getCaller()+"], "
					+ "API_" + getApi().name() + ", " + getParam() + ");\n";
			break;
		case IN_PAIRS_WE_SE:
			temp = "inpairs_global_WE_SE(&STATE_INP_WE_SE["+getParam()+"], "
					+ "&COUNT_INP_WE_SE["+getParam()+"], "+getCaller()+", " + getParam() + ");\n";
			break;
		case IN_PAIRS_AT_TT:
			temp = "inpairs_global_AT_TT(&STATE_INP_AT_TT["+getCaller()+"], "
					+ "&STATE_INP_AT_TT[" + getCallee() + "], " + "&COUNT_INP_AT_TT["+getCaller()+"], "
							+ "&COUNT_INP_AT_TT[" + getCallee() + "], " + "API_"+api.name()+");\n";
			break;
		case IN_PAIRS_WE_CE:
			temp = "inpairs_WE_CE(&STATE_INP_WE_CE["+getCaller()+"], "
					+"&COUNT_INP_WE_CE["+ getCaller()+"], API_" + getApi().name() +");\n";
			break;
		case NIB_GR_RR:
			temp = "notInBetween(&STATE_NIB_GR_RR["+getCaller()+"], " + "&COUNT_NIB_GR_RR["+getCaller()+"],"
					+ "API_"+getApi().name() + ");\n";
			break;
			default:
				temp = "";
		}
		
		return temp;
	}
	
	public String getCode(){
		this.statement = generateCode();
		return this.statement;
	}

}
