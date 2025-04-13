package com.github.xmlnode;

import com.fasterxml.jackson.databind.JsonNode;

class ChildNode {
    private final String attributeName;
    private JsonNode node;
    private boolean isValueNode = false;

    public ChildNode(String attributeName, JsonNode node, boolean isValueNode) {
        this.attributeName = attributeName;
        this.node = node;
        this.isValueNode = isValueNode;
    }

    public ChildNode(String attributeName, JsonNode node) {
        this.attributeName = attributeName;
        this.node = node;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public JsonNode getNode() {
        return this.node;
    }

    public void setNode(JsonNode node) {
        this.node = node;
    }

    public boolean isValueNode() {
        return this.isValueNode;
    }
}
