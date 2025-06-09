package com.github.xmlnode;

import com.fasterxml.jackson.databind.node.TextNode;

public class CDATANode extends TextNode {
    public CDATANode(String v) {
        super(v);
    }
}
