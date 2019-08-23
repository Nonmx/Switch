/**
 * 
 */
package sselab.switchy.simplenode.visitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import sselab.cfg.node.specifiednode.HeadNode;
import sselab.cfg.node.specifiednode.TailNode;
import sselab.switchy.simplenode.Label;
import sselab.switchy.simplenode.ProgramCounter;
import sselab.switchy.simplenode.SimpleAPINode;
import sselab.switchy.simplenode.SimpleControlNode;
import sselab.switchy.simplenode.SimpleDeclarationNode;
import sselab.switchy.simplenode.SimpleLoopNode;
import sselab.switchy.simplenode.SimpleNode;
import sselab.switchy.simplenode.SimpleNormalNode;
import sselab.switchy.simplenode.SimplePseudoNode;

/**
 * The class is a visitor of SimpleNode objects to print the content.
 * @author SSELAB-129
 *
 */
public class SimpleNodePrinter implements SimpleNodeVisitor{

	public static int CONTINUE = 1;
	public static int STOP = 0;
	private BufferedWriter bufferedWriter;
	
	public SimpleNodePrinter() {
	}
	
	public SimpleNodePrinter(BufferedWriter bufferedWriter){
		this.bufferedWriter = bufferedWriter;
	}
	
	@Override
	public int visit(SimpleNode node, Set<SimpleNode> visited) {
		visited.add(node);
		return CONTINUE;
	}

	@Override
	public int visit(SimpleAPINode node, Set<SimpleNode> visited) {
		visited.add(node);
		try {
			if(node.getBeforeNodes().size() > 1 
					&& (node.getBeforeNode(1) instanceof SimpleLoopNode || node.getBeforeNode(0) instanceof SimpleLoopNode)){
				int idx = node.getBeforeNode(0) instanceof SimpleLoopNode ? 0 : 1;
					SimpleLoopNode loopNode = (SimpleLoopNode) node.getBeforeNode(idx);
					bufferedWriter.write(loopNode.getEndPC().getCode());
					bufferedWriter.write(loopNode.getLabel().getCode());
			}
			bufferedWriter.write(node.getCode());
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CONTINUE;
	}

	@Override
	public int visit(SimpleLoopNode node, Set<SimpleNode> visited) {
		visited.add(node);
		try {
			bufferedWriter.write(node.getCode());
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CONTINUE;
	}

	@Override
	public int visit(SimplePseudoNode node, Set<SimpleNode> visited) {
		visited.add(node);
		//End of the function
		if(node.node instanceof TailNode){
			return STOP;
		}else{ //Start of the function
			HeadNode head = (HeadNode)node.node;
			try {
				bufferedWriter.write(head.getFunction().getTypeName() + " ");
				String functionName = head.getFunction().getFunctionName();
				if(node.getTaskID().equals("main"))
					functionName += "()";
				bufferedWriter.write(functionName + "{");
				bufferedWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//A task starts with jump operation.
			if(!node.getTaskID().equals("main") &&
					!(node.getAfterNode(0) instanceof SimpleDeclarationNode)){
				try {
					bufferedWriter.write("jump_" + node.getTaskID() + "();\nL_"+node.getTaskID()+"_0:");
					bufferedWriter.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return CONTINUE;
		}
	}

	@Override
	public int visit(SimpleDeclarationNode node, Set<SimpleNode> visited) {
		visited.add(node);
		try {
			bufferedWriter.write("static " + node.getCode());
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!node.getTaskID().equals("main") && 
				!(node.getAfterNode(0) instanceof SimpleDeclarationNode)){
			try {
				bufferedWriter.write(node.getLabel().getCode());
				bufferedWriter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return CONTINUE;
	}

	@Override
	public int visit(SimpleControlNode node, Set<SimpleNode> visited) {
		visited.add(node);
		try{
			if(node.isEndOfBlock() && node.getBeforeNodes().size() > 1 
					&& (node.getBeforeNode(1) instanceof SimpleLoopNode || node.getBeforeNode(0) instanceof SimpleLoopNode)){
				int idx = node.getBeforeNode(0) instanceof SimpleLoopNode ? 0 : 1;
					SimpleLoopNode loopNode = (SimpleLoopNode) node.getBeforeNode(idx);
				bufferedWriter.write(loopNode.getEndPC().getCode());
				bufferedWriter.write(loopNode.getLabel().getCode());
		}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		if(node.getBeforeNode(0) instanceof SimpleControlNode){
			SimpleControlNode parentBranch = (SimpleControlNode) node.getBeforeNode(0);
			if(parentBranch.getFalseNode().equals(node)){
				try {
					bufferedWriter.write("else ");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			bufferedWriter.write(node.getCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CONTINUE;
	}
	
	public void endOfBlock(){
		try {
			bufferedWriter.write("}");
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int visit(SimpleNormalNode node, Set<SimpleNode> visited) {
		visited.add(node);
		try {
			if(node.isEndOfBlock() && node.getBeforeNodes().size() > 1 
					&& (node.getBeforeNode(1) instanceof SimpleLoopNode || node.getBeforeNode(0) instanceof SimpleLoopNode)){
				int idx = node.getBeforeNode(0) instanceof SimpleLoopNode ? 0 : 1;
					SimpleLoopNode loopNode = (SimpleLoopNode) node.getBeforeNode(idx);
					if(!node.getTaskID().equals("main")){
						bufferedWriter.write(loopNode.getEndPC().getCode());
						bufferedWriter.write(loopNode.getLabel().getCode());
					}
			}
			bufferedWriter.write(node.getCode());
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CONTINUE;
	}

}
