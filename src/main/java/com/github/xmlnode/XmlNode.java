package com.github.xmlnode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class XmlNode extends ObjectNode {
    protected List<ChildNode> _children = new ArrayList<>();
    protected final Map<String, Object> _attributes = new LinkedHashMap<>();
    protected final Map<String, String> _namespacePrefixURIMapping = new HashMap<>();
    protected final Map<String, List<String>> _namespacePrefixElementMapping = new HashMap<>();
    protected final Map<String, String> _prefixAttributeMapping = new HashMap<>();

    public XmlNode(JsonNodeFactory nc, Map<String, JsonNode> children) {
        super(nc);
        this._children = children.entrySet().stream()
                .map(kv -> new ChildNode(kv.getKey(), kv.getValue()))
                .collect(Collectors.toList());
    }

    public XmlNode(JsonNodeFactory nc) {
        super(nc);
    }

    @Override
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        this.serialize(g, provider);
    }

    @Override
    public XmlNode putObject(String propertyName) {
        XmlNode n = new XmlNode(this._nodeFactory);
        this.addChild(propertyName, n);
        return n;
    }


    public XmlNode putObject(String propertyName, String prefix) {
        XmlNode n = new XmlNode(this._nodeFactory);
        this.addChild(propertyName, n);
        this.addNamespace(prefix, propertyName);
        return n;
    }

    @Override
    public XmlNode putPOJO(String fieldName, Object pojo) {
        JsonNode n = this.pojoNode(pojo);
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode putRawValue(String fieldName, RawValue raw) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.rawValueNode(raw));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, short v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, int v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, long v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, float v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, double v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, BigDecimal v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, BigInteger v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.numberNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, String v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.textNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, boolean v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.booleanNode(v));
        this.addChild(fieldName, n);
        return n;
    }

    @Override
    public XmlNode put(String fieldName, byte[] v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(this.binaryNode(v));
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Short v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.numberNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Integer v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.numberNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Long v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.numberNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Float v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.numberNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Double v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.numberNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public XmlNode put(String fieldName, Boolean v) {
        XmlNode n = new XmlNode(this._nodeFactory);
        n.setValue(v != null ? this.booleanNode(v) : this.nullNode());
        this.addChild(fieldName, n);
        return this;
    }

    @Override
    public JsonNode get(int index) {
        if (index < 0 || index >= this._children.size())
            return null;
        return this._children.get(index).getNode();
    }

    @Override
    public JsonNode get(String propertyName) {
        return this._children.stream()
                .filter(node -> node.getAttributeName().equals(propertyName))
                .map(ChildNode::getNode)
                .findFirst()
                .orElse(null);
    }

    @Override
    public XmlNode deepCopy() {
        XmlNode ret = new XmlNode(_nodeFactory);

        for (ChildNode entry : _children)
            ret.addChild(entry.getAttributeName(), entry.getNode().deepCopy());

        return ret;
    }

    @Override
    public int size() {
        return this._children.size();
    }

    @Override
    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    @Override
    public Iterator<JsonNode> elements() {
        return this._children.stream().map(ChildNode::getNode).iterator();
    }

    @Override
    public Iterator<String> fieldNames() {
        return this._children.stream().map(ChildNode::getAttributeName).iterator();
    }

    @Override
    public JsonNode path(int index) {
        JsonNode node = this.get(index);
        if (node == null)
            return this.missingNode();
        return node;
    }

    @Override
    public JsonNode path(String propertyName) {
        JsonNode node = this.get(propertyName);
        if (node == null)
            return this.missingNode();
        return node;
    }

    @Override
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return this.properties().iterator();
    }

    @Override
    public <T extends JsonNode> T set(String propertyName, JsonNode value) {
        this.addChild(propertyName, value);
        return (T) this;
    }

    @Override
    public JsonNode replace(String propertyName, JsonNode value) {
        for (ChildNode childNode : this._children) {
            if (childNode.getAttributeName().equals(propertyName)) {
                JsonNode node = childNode.getNode();
                childNode.setNode(value);
                return node;
            }
        }
        this.addChild(propertyName, value);
        return null;
    }

    @Override
    public <T extends JsonNode> T without(String propertyName) {
        this._children.removeIf(node -> node.getAttributeName().equals(propertyName));
        return (T) this;
    }

    @Override
    public <T extends JsonNode> T without(Collection<String> propertyNames) {
        this._children.removeIf(node -> propertyNames.contains(node.getAttributeName()));
        return (T) this;
    }

    @Override
    public JsonNode remove(String propertyName) {
        JsonNode toRemove = this.get(propertyName);
        this.without(propertyName);
        return toRemove;
    }

    public JsonNode remove(int index) {
        JsonNode toRemove = this.get(index);
        int i = 0;
        for (Iterator<ChildNode> iterator = this._children.iterator(); iterator.hasNext(); i++) {
            iterator.next();
            if (index == i)
                iterator.remove();
        }
        return toRemove;
    }

    @Override
    public JsonNode putIfAbsent(String propertyName, JsonNode value) {
        if (this.get(propertyName) == null) {
            this.set(propertyName, value);
            return null;
        }
        return this.get(propertyName);
    }

    @Override
    public XmlNode remove(Collection<String> propertyNames) {
        this.without(propertyNames);
        return this;
    }

    @Override
    public XmlNode removeAll() {
        this._children.clear();
        return this;
    }

    @Override
    public XmlNode retain(Collection<String> propertyNames) {
        this._children.removeIf(node -> !propertyNames.contains(node.getAttributeName()));
        return this;
    }

    @Override
    public XmlNode retain(String... propertyNames) {
        return this.retain(Arrays.asList(propertyNames));
    }

    @Override
    public XmlNode putNull(String propertyName) {
        this.addChild(propertyName, this.nullNode());
        return this;
    }

    @Override
    public String toPrettyString() {
        try {
            return XmlMapper.xmlBuilder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        try {
            return XmlMapper.xmlBuilder().findAndAddModules().build().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean has(String fieldName) {
        return this.get(fieldName) != null;
    }

    @Override
    public boolean has(int index) {
        return this.get(index) != null;
    }

    @Override
    public boolean hasNonNull(String fieldName) {
        JsonNode n = this.get(fieldName);
        return n != null && !n.isNull();
    }

    @Override
    public boolean hasNonNull(int index) {
        JsonNode n = this.get(index);
        return n != null && !n.isNull();
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> properties() {
        return this._children.stream()
                .collect(Collectors.toMap(ChildNode::getAttributeName, ChildNode::getNode))
                .entrySet();
    }

    @Override
    public JsonNode findValue(String propertyName) {
        JsonNode jsonNode = this.get(propertyName);
        if (jsonNode != null)
            return jsonNode;

        return this._children.stream()
                .map(child -> child.getNode().findValue(propertyName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ObjectNode findParent(String propertyName) {
        JsonNode jsonNode = this.get(propertyName);
        if (jsonNode != null)
            return this;
        return (ObjectNode) this._children.stream()
                .map(child -> child.getNode().findParent(propertyName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends JsonNode> T setAll(Map<String, ? extends JsonNode> properties) {
        for (Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = en.getValue();
            if (n == null)
                n = nullNode();
            this.addChild(en.getKey(), n);
        }
        return (T) this;
    }

    @Override
    public <T extends JsonNode> T setAll(ObjectNode other) {
        for (Map.Entry<String, JsonNode> property : other.properties())
            this.addChild(property.getKey(), property.getValue());
        return (T) this;
    }

    @Override
    public <T extends JsonNode> T require() throws IllegalArgumentException {
        return (T) this;
    }

    @Override
    public <T extends JsonNode> T requireNonNull() throws IllegalArgumentException {
        return (T) this;
    }


    public XmlNode putAttribute(String attributeName, Object v) {
        this._attributes.put(attributeName, v);
        return this;
    }

    public XmlNode putAttribute(String prefix, String attributeName, Object v) {
        this._attributes.put(attributeName, v);
        this._prefixAttributeMapping.put(prefix, attributeName);
        return this;
    }

    public Object getAttribute(String attributeName) {
        return this._attributes.get(attributeName);
    }

    private void addChild(String propertyName, JsonNode value) {
        this._children.add(new ChildNode(propertyName, value));
    }

    private List<String> addNamespace(String prefix, String elementName) {
        List<String> tempList;
        if (this._namespacePrefixElementMapping.containsKey(prefix)) {
            tempList = this._namespacePrefixElementMapping.get(prefix);
            if (tempList == null)
                tempList = new ArrayList<>();
            tempList.add(elementName);
        } else {
            tempList = new ArrayList<>();
            tempList.add(elementName);
        }
        return this._namespacePrefixElementMapping.put(prefix, tempList);
    }

    public List<String> getNamespaceElements(String prefix) {
        return this._namespacePrefixElementMapping.get(prefix);
    }

    public XmlNode putNamespaceURI(String prefix, String namespaceURI) {
        this._namespacePrefixURIMapping.put(prefix, namespaceURI);
        return this;
    }

    public String getNamespaceURI(String prefix) {
        return this._namespacePrefixURIMapping.get(prefix);
    }

    public List<JsonNode> getValues() {
        return this._children.stream().map(ChildNode::getNode).collect(Collectors.toList());
    }

    public void setValue(JsonNode _value) {
        this._children.add(new ChildNode("@text", _value, true));
    }

    public void setValue(Object value) {
        this.setValue(this.pojoNode(value));
    }

    public void setValue(RawValue value) {
        this.setValue(this.rawValueNode(value));
    }

    public void setValue(short value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(int value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(long value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(float value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(double value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(BigDecimal value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(BigInteger value) {
        this.setValue(this.numberNode(value));
    }

    public void setValue(String value) {
        this.setValue(this.textNode(value));
    }

    public void setValue(boolean value) {
        this.setValue(this.booleanNode(value));
    }

    public void setValue(byte[] value) {
        this.setValue(this.binaryNode(value));
    }

    public void setValue(Short value) {
        this.setValue(value != null ? this.numberNode(value) : this.nullNode());
    }

    public void setValue(Integer value) {
        this.setValue(value != null ? this.numberNode(value) : this.nullNode());
    }

    public void setValue(Long value) {
        this.setValue(value != null ? this.numberNode(value) : this.nullNode());
    }

    public void setValue(Float value) {
        this.setValue(value != null ? this.numberNode(value) : this.nullNode());
    }

    public void setValue(Double value) {
        this.setValue(value != null ? this.numberNode(value) : this.nullNode());
    }

    public void setValue(Boolean value) {
        this.setValue(value != null ? this.booleanNode(value) : this.nullNode());
    }

    public Map<String, Object> getAttributes() {
        return this._attributes;
    }

    public Object getAttributeValue(String attributeName) {
        return this._attributes.get(attributeName);
    }

    public List<String> getPrefixes() {
        return new ArrayList<>(this._namespacePrefixURIMapping.keySet());
    }

    public List<String> getNamespaceURIs() {
        return new ArrayList<>(this._namespacePrefixURIMapping.values());
    }

    @Override
    public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
        if (g instanceof ToXmlGenerator) {
            ToXmlGenerator xmlGenerator = (ToXmlGenerator) g;
            xmlGenerator.writeStartObject();

            // Write namespace declarations
            for (Map.Entry<String, String> entry : this._namespacePrefixURIMapping.entrySet()) {
                xmlGenerator.setNextIsAttribute(true);
                xmlGenerator.writeFieldName(String.format("xmlns:%s", entry.getKey()));
                xmlGenerator.writeString(entry.getValue());
                xmlGenerator.setNextIsAttribute(false);
            }

            for (Map.Entry<String, Object> entry : this._attributes.entrySet()) {
                xmlGenerator.setNextIsAttribute(true);
                if (this._prefixAttributeMapping.containsValue(entry.getKey())) {
                    Map.Entry<String, String> prefixAttributeEntry = this._prefixAttributeMapping.entrySet()
                            .stream()
                            .filter(p -> p.getValue().equals(entry.getKey()))
                            .findFirst()
                            .get();
                    xmlGenerator.writeFieldName(String.format("%s:%s", prefixAttributeEntry.getKey(), prefixAttributeEntry.getValue()));
                } else xmlGenerator.writeFieldName(entry.getKey());
                xmlGenerator.writeObject(entry.getValue());
                //System.out.printf("Writing attribute %s: %s\n", entry.getKey(), entry.getValue());
                xmlGenerator.setNextIsAttribute(false);
            }

            for (ChildNode entry : this._children) {
                String fieldName = entry.getAttributeName();
                JsonNode entryNode = entry.getNode();
                if (entryNode instanceof XmlNode) {
                    if (!entry.isValueNode()) {
                        Map.Entry<String, List<String>> prefixEntry = this._namespacePrefixElementMapping.entrySet()
                                .stream()
                                .filter(e -> e.getValue().contains(fieldName))
                                .findFirst()
                                .orElse(null);
                        if (prefixEntry != null && !prefixEntry.getKey().trim().isEmpty()) {
                            //System.out.printf("Writing field %s:%s\n", prefixEntry.getKey(), fieldName);
                            xmlGenerator.writeFieldName(String.format("%s:%s", prefixEntry.getKey(), fieldName));
                        } else {
                            //System.out.printf("Writing field name: %s\n", fieldName);
                            xmlGenerator.writeFieldName(fieldName);
                        }
                        entryNode.serialize(xmlGenerator, provider); // Let child nodes handle their own serialization
                    }
                    // Write text content of the current node (if any)
                } else {
                    xmlGenerator.setNextIsUnwrapped(true);
                    xmlGenerator.writeFieldName(""); // Key change: empty field name for text
                    xmlGenerator.writeObject(entryNode);
                    //System.out.printf("Writing value %s\n", this._value.asText());
                    xmlGenerator.setNextIsUnwrapped(false);
                }
            }

            xmlGenerator.writeEndObject();
        }
    }

    static class XmlNodeDeserializer extends JsonDeserializer<XmlNode> {

        @Override
        public XmlNode deserialize(JsonParser p, DeserializationContext ctxt) {
            XmlNode xmlNode = new XmlNode(JsonNodeFactory.instance);
            if (p instanceof FromXmlParser) {
                Deque<XmlNode> nodeStack = new ArrayDeque<>();
                XMLStreamReader reader = ((FromXmlParser) p).getStaxReader();
                String rootName = reader.getLocalName();
                //System.out.printf("%s is standalone: %s%n", rootName, reader.isStandalone());
                try {
                    nodeStack.push(xmlNode);
                    //System.out.printf("Start %s%n", rootName);
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        //System.out.printf("%s=%s%n", reader.getAttributeName(i), reader.getAttributeValue(i));
                        xmlNode.putAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                    for (int i = 0; i < reader.getNamespaceCount(); i++) {
                        xmlNode._namespacePrefixURIMapping.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                        //System.out.printf("%s:%s -> %s%n", reader.getNamespacePrefix(i), reader.getLocalName(), reader.getNamespaceURI(i));
                    }
                    xmlNode.addNamespace(reader.getPrefix(), reader.getLocalName());
                    //System.out.printf("%s:%s%n", reader.getPrefix(), reader.getLocalName());
                    while (reader.hasNext()) {
                        switch (reader.next()) {
                            case XMLStreamConstants.START_ELEMENT:
                                XmlNode parentNode = nodeStack.peek();
                                XmlNode childNode = parentNode.putObject(reader.getLocalName());
                                //System.out.printf("Start %s%n", reader.getName());
                                for (int i = 0; i < reader.getAttributeCount(); i++) {
                                    String prefix = reader.getAttributeName(i).getPrefix();
                                    //System.out.printf("%s=%s%n", reader.getAttributeName(i), reader.getAttributeValue(i));
                                    String attributeLocalName = reader.getAttributeLocalName(i);
                                    String attributeValue = reader.getAttributeValue(i);
                                    if (prefix != null && !prefix.trim().isEmpty())
                                        childNode.putAttribute(prefix, attributeLocalName, attributeValue);
                                    else
                                        childNode.putAttribute(attributeLocalName, attributeValue);
                                }
                                for (int i = 0; i < reader.getNamespaceCount(); i++) {
                                    parentNode._namespacePrefixURIMapping.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                                    //System.out.printf("%s:%s -> %s%n", reader.getNamespacePrefix(i), reader.getLocalName(), reader.getNamespaceURI(i));
                                }
                                parentNode.addNamespace(reader.getPrefix(), reader.getLocalName());
                                //System.out.printf("%s:%s%n", reader.getPrefix(), reader.getLocalName());
                                nodeStack.push(childNode);
                                break;
                            case XMLStreamConstants.END_ELEMENT:
                                //System.out.printf("End %s%n", reader.getName());
                                nodeStack.pop();
                                break;
                            case XMLStreamConstants.CHARACTERS:
                            case XMLStreamConstants.SPACE:
                                String text = reader.getText().trim();
                                if (!text.isEmpty()) {
                                    //System.out.printf("text: %s%n", text);
                                    XmlNode currentNode = nodeStack.peek();
                                    currentNode.setValue(TextNode.valueOf(text));
                                }
                                break;
                        }
                    }
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
            return xmlNode;
        }
    }

}

