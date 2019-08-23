package sselab.switchy.simplenode;

import java.util.ArrayList;
import java.util.List;

import sselab.cfg.node.specifiednode.HeadNode;
import sselab.cfg.node.specifiednode.LoopNode;
import sselab.cfg.parser.CFGNode;
import sselab.cfg.parser.FunctionCFG;
import sselab.switchy.simplenode.factory.ConcreteSimpleNodeFactory;

public class Task {
	private SimpleNode cfg;
	private String taskID;
	private int switchingPointNum;
	
	public Task() {
		ProgramCounter.maxPCVal = 0;
	}
	
	public Task(String taskID){
		ProgramCounter.maxPCVal = 0;
		this.taskID = taskID;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public int getSwitchingPointNum() {
		return switchingPointNum;
	}

	public void setSwitchingPointNum(int switchingPointNum) {
		this.switchingPointNum = switchingPointNum;
	}
	
	/**
	 * Generate modifiable CFG(new one) from the original functionCFG of CodeAnt.
	 * @param f
	 */
	public void generateCFG(FunctionCFG f){
		List<SimpleNode> queue = new ArrayList<SimpleNode>();
		ConcreteSimpleNodeFactory factory = new ConcreteSimpleNodeFactory();
		CFGNode curNode = f.getHeadNode();
		if(getTaskID() == null)
			this.setTaskID(f.getFunctionName().substring(5).replace(")", ""));
		SimpleNode headNode = factory.createNode(curNode, getTaskID());
		queue.add(factory.createNode(curNode, headNode.getTaskID()));
		
		while(!queue.isEmpty()){
			SimpleNode temp = queue.get(0);
			queue.remove(0);
			if(temp.node instanceof HeadNode)
				headNode = temp;
			for(CFGNode n : temp.node.getNextNodes()){
				if(contains(queue, n)){
					temp.addAfterNodes(queue.get(indexOf(queue, n)));
					queue.get(indexOf(queue, n)).addBeforeNode(temp);
				}
				else{
					SimpleNode nextTemp = factory.createNode(n, headNode.getTaskID());
					if(!(temp.node.isEndOfBlock() && nextTemp instanceof SimpleLoopNode)){
					//nextTemp.node.setAnnotation(true);
					nextTemp.addBeforeNode(temp);
					temp.addAfterNodes(nextTemp);
					queue.add(nextTemp);
					}
				}
			}
		}
		this.cfg = headNode;
	}
	
	private boolean contains(List<SimpleNode> list, CFGNode n){
		for(SimpleNode l : list){
			if(l.node.equals(n))
				return true;
		}
		return false;
	}
	
	
	private int indexOf(List<SimpleNode> list, CFGNode n){
		for(SimpleNode l : list){
			if(l.node.equals(n))
				return list.indexOf(l);
		}
		return -1;
	}

	/**
	 * Return the control flow graph of this task function.
	 * @return cfg
	 */
	public SimpleNode getCFG(){
		return this.cfg;
	}
	
	/**
	 * Generate the string of jump operation macro of the task.
	 * @return String macro
	 */
	public String getJumpMacro(){
		String macro = "";
		macro += "#define jump_" + this.taskID + "(){\\\n";
		macro += "\tswitch(current_pc["+this.taskID+"]){\\\n";
		for(int i = 0; i < this.switchingPointNum; i++){
			macro += "\tcase " + i + ": \\\n";
			macro += "\t\tgoto L_" +this.taskID + "_"+ i + "; break;\\\n";
		}
		macro += "\t}\\\n}\n\n";
		return macro;
	}

}
