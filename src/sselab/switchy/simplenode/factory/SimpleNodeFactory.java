package sselab.switchy.simplenode.factory;

import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.SimpleNode;

public interface SimpleNodeFactory {
	public SimpleNode createNode(CFGNode node, String taskID);
}
