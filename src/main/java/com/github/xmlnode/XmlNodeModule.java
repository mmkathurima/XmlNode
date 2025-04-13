package com.github.xmlnode;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class XmlNodeModule extends SimpleModule {
    public XmlNodeModule() {
        this.addDeserializer(XmlNode.class, new XmlNode.XmlNodeDeserializer());
    }
}
