package sselab.switchy.simplenode.factory;

import sselab.cfg.node.BranchNode;
import sselab.cfg.node.NormalNode;
import sselab.cfg.node.specifiednode.DeclarationNode;
import sselab.cfg.node.specifiednode.HeadNode;
import sselab.cfg.node.specifiednode.IfNode;
import sselab.cfg.node.specifiednode.LoopNode;
import sselab.cfg.node.specifiednode.SwitchNode;
import sselab.cfg.node.specifiednode.TailNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.api.API;
import sselab.switchy.simplenode.SimpleAPINode;
import sselab.switchy.simplenode.SimpleControlNode;
import sselab.switchy.simplenode.SimpleDeclarationNode;
import sselab.switchy.simplenode.SimpleLoopNode;
import sselab.switchy.simplenode.SimpleNode;
import sselab.switchy.simplenode.SimpleNormalNode;
import sselab.switchy.simplenode.SimplePseudoNode;

public class ConcreteSimpleNodeFactory implements SimpleNodeFactory{

	public ConcreteSimpleNodeFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SimpleNode createNode(CFGNode node, String taskID) {
		if(node instanceof HeadNode || node instanceof TailNode){
			return new SimplePseudoNode(node, taskID);
		} else if(node instanceof LoopNode){
			return new SimpleLoopNode(node, taskID);
		} else if(node instanceof NormalNode || node instanceof BranchNode){
			// The statement is calling function.
			if(node.getCode().endsWith(");")){
				String[] codes = node.getCode().split("\\(");
				String code = codes[0];
				//If the function call is the call of an API of OSEK
				if(API.getAPIByName(code) != null){
					SimpleAPINode apiNode = new SimpleAPINode(node, taskID, API.getAPIByName(code));
					apiNode.setStringVal(node.getCode());
					return apiNode;
				} else{
					return new SimpleNormalNode(node, taskID);
				}
			} else{ //other statements(if, switch, ....)
				//variable declaration.
				if(node instanceof DeclarationNode){
					return new SimpleDeclarationNode(node, taskID);
				} else if(node instanceof IfNode || node instanceof SwitchNode){
					return new SimpleControlNode(node, taskID);
				} else{
					return new SimpleNormalNode(node, taskID);
				}
			}
		} else{
			//return new SimpleNormalNode(node, taskID);
			return null;
		}	
	}
}
