package sselab.switchy.simplenode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sselab.cfg.parser.CFGNode;
import sselab.switchy.api.API;
import sselab.switchy.property.Assertion;
import sselab.switchy.property.MonitoringCode;
import sselab.switchy.property.Property;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

/**
 * The class of API call statement.
 * @author SSELAB-129
 *
 */
public class SimpleAPINode extends SimpleNormalNode{
	private List<Property> preConditions;
	private List<Property> postConditions;
	private API apiKind;
	private String stringVal;
	private SwitchingPoint switchingPoint;
	
	public SimpleAPINode() {
		super();
		preConditions = new ArrayList<Property>();
		postConditions = new ArrayList<Property>();
	}
	
	public SimpleAPINode(API kind, String ...params){
		super();
		//generate call expression
		String call = kind.name() + "(";
		for(int i = 0; i < params.length; i++){
			call += params[i];
			if(i != params.length - 1){
				call += ", ";
			}
		}
		call += ");";
		//set as a string value.
		this.setStringVal(call);
		this.apiKind = kind;
		preConditions = new ArrayList<Property>();
		postConditions = new ArrayList<Property>();
	}
	
	public SimpleAPINode(CFGNode node, String taskID){
		super(node, taskID);
		preConditions = new ArrayList<Property>();
		postConditions = new ArrayList<Property>();
	}
	
	public SimpleAPINode(CFGNode node, String taskID, API apiKind){
		super(node, taskID);
		this.apiKind = apiKind;
		this.stringVal = apiKind.name();
		preConditions = new ArrayList<Property>();
		postConditions = new ArrayList<Property>();
	}
	
	/**
	 * The method sets the string value of the node.
	 * @param call
	 */
	public void setStringVal(String call) {
		this.stringVal = call;
	}
	
	/**
	 * The method returns the string value of the node.
	 * @return
	 */
	public String getStringVal(){
		if(this.stringVal == null)
			return "";
		else
			return this.stringVal;
	}

	/**
	 * The method adds precondition of the API call.
	 * @param pre
	 */
	public void addPreCondition(Property pre){
		this.preConditions.add(pre);
	}
	
	/**
	 * The method adds post condition of the API call.
	 * @param post
	 */
	public void addPostCondition(Property post){
		this.postConditions.add(post);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Property> getPreConditions(){
		return this.preConditions;
	}
	
	public List<Property> getPostConditions(){
		return this.postConditions;
	}
	
	public Property getPreCondition(int idx){
		return this.preConditions.get(idx);
	}
	
	public Property getPostCondition(int idx){
		return this.postConditions.get(idx);
	}
	
	public API getAPIKind(){
		return this.apiKind;
	}
	
	public String getAPIName(){
		return this.apiKind.name();
	}
	
	
	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		if(this.isEndOfBlock()){
			if(visitor instanceof SimpleNodePrinter){
				((SimpleNodePrinter)visitor).endOfBlock();
			}
		} else{
			getAfterNode(0).accept(visitor, visited);
		}
	}
	
	public void setSwitchingPoint(SwitchingPoint point){
		this.switchingPoint = point;
	}
	
	public void setSwitchingPoint(){
		SwitchingPoint point = new SwitchingPoint();
		point.setTaskID(this.getTaskID());
		point.setPcVal(ProgramCounter.maxPCVal+1);
		ProgramCounter.maxPCVal++;
		this.switchingPoint = point;
	}
	public SwitchingPoint getSwitchingPoint(){
		return this.switchingPoint;
	}
	
	public String getCode(){
		String code = "";
		if(this.getBeforeNode(0) instanceof SimpleControlNode){
			SimpleControlNode parentBranch = (SimpleControlNode) getBeforeNode(0);
			if(parentBranch.getFalseNode().equals(this))
				code += "\n";
		}
		if(preConditions != null && !preConditions.isEmpty()){
			for(Property p : preConditions){
				code += p.getStatement();
			}
		}
		code += "flag = " + getStringVal() + "\n";
		if(postConditions != null && !postConditions.isEmpty()){
			for(Property p : postConditions){
				if(p instanceof MonitoringCode){
					((MonitoringCode) p).setCaller(this.getTaskID());
					((MonitoringCode) p).setApi(this.apiKind);
					if(getParams().length > 1){
						((MonitoringCode) p).setCallee(getParams()[0]);
						((MonitoringCode) p).setParam(getParams()[1]);
					}
					else if(getParams().length == 1){
						switch(this.getAPIKind()){
						case GetResource:
						case ReleaseResource:
						case WaitEvent:
						case ClearEvent:
							((MonitoringCode) p).setParam(getParams()[0]);
							break;
							default:
								((MonitoringCode) p).setCallee(getParams()[0]);
						}
					}
					code += ((MonitoringCode) p).getCode();
				}
				else
					code += p.getStatement();
			}
		}
		if(!this.getTaskID().equals("main")){
			code += getSwitchingPoint().getCode();
		//no more label after TT/CT.
			if(!(this.apiKind.equals(API.TerminateTask) || this.apiKind.equals(API.ChainTask)) ){
				code += "L_"+this.getTaskID()+"_" + this.getSwitchingPoint().getPcVal()+ ":";
			}
		}
		return code;
	}
	
	public String[] getParams(){
		String temp = this.getStringVal().substring(this.getStringVal().indexOf('(')+1);
		temp = temp.replace(");", "");
		System.out.println(temp);
		String[] result = temp.split(",[ ]*");
		return result;
	}

}
