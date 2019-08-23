package sselab.switchy.simplenode;

import java.util.ArrayList;
import java.util.List;

import sselab.cfg.node.specifiednode.HeadNode;
import sselab.cfg.node.specifiednode.IfNode;
import sselab.cfg.node.specifiednode.LoopNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.Element;

public abstract class SimpleNode implements Element{
	public CFGNode node;
	private String taskID;
	private Label label;

	private List<SimpleNode> beforeNodes;
	private List<SimpleNode> afterNodes;

	public SimpleNode() {
	}
	
	public SimpleNode(CFGNode node, String taskID){
		this.node = node;
		this.taskID = taskID;
	}

	
	public SimpleNode getBeforeNode(int idx){
		return beforeNodes.get(idx);
	}
	
	public SimpleNode getAfterNode(int idx){
		if(afterNodes == null){
			System.out.println(this.getStringVal());
		}
		return afterNodes.get(idx);
	}
	
	public List<SimpleNode> getBeforeNodes(){
		return beforeNodes;
	}
	
	public List<SimpleNode> getAfterNodes(){
		return afterNodes;
	}
	
	public void addBeforeNode(SimpleNode node){
		if(this.beforeNodes == null){
			this.beforeNodes = new ArrayList<SimpleNode>(); 
		}
		this.beforeNodes.add(node);
	}
	
	public void addAfterNodes(SimpleNode node){
		if(this.afterNodes == null){
			this.afterNodes = new ArrayList<SimpleNode>(); 
		}
		this.afterNodes.add(node);
	}
	
	@SuppressWarnings("rawtypes")
	public Class getTypeOfNode(){
		return this.node.getClass();
	}
	
	public String getTypeNameOfNode(){
		return this.node.getClass().getName();
	}
	
	public boolean isEndOfBlock(){
		return this.node.isEndOfBlock();
	}
	
	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		if(taskID.startsWith("TASK_")){
			taskID = taskID.substring(5);
		}
		this.taskID = taskID;
	}
	
	
	public abstract String getStringVal();

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
	
	
}
