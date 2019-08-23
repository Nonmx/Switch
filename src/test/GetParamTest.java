package test;

import static org.junit.Assert.*;

import org.junit.Test;

import sselab.switchy.api.API;
import sselab.switchy.simplenode.SimpleAPINode;

public class GetParamTest {

	@Test
	public void test() {
		SimpleAPINode node = new SimpleAPINode(API.SetEvent, "r0", "r1");
		assertEquals(node.getParams()[0], "r0");
		assertEquals(node.getParams()[1], "r1");
	}

}
