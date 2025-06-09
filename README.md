# XML Node Handling Library

A Java library based on Jackson's ObjectNode for handling XML nodes with enhanced support for namespaces, attributes, and mixed content using Jackson's XML module.

## Features

- **XML Node Management**: Create and manipulate XML nodes with attributes and namespaces.
- **Serialization/Deserialization**: Convert between XML and `XmlNode` objects.
- **Namespace Support**: Manage namespace declarations and prefixes.
- **Mixed Content Handling**: Support for elements with both text and child nodes.

## Installation

1. **Dependencies**:
    - Jackson Dataformat XML
    - Jackson Databind
    - Woodstox (recommended for better CDATA handling)

   Add to your `pom.xml`:
   ```xml
   <dependencies>
       <dependency>
           <groupId>com.fasterxml.jackson.dataformat</groupId>
           <artifactId>jackson-dataformat-xml</artifactId>
           <version>2.15.0</version>
       </dependency>
       <dependency>
           <groupId>com.fasterxml.woodstox</groupId>
           <artifactId>woodstox-core</artifactId>
           <version>6.5.0</version>
       </dependency>
   </dependencies>
   ```
2. Clone the Repository:
```bash
git clone https://github.com/your-repo/xml-node-handler.git
```
## Usage
### Creating an XML Node
```java
XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
node.setValue("Hello, World!");
```
### Serializing to XML
```java
XmlMapper xmlMapper = new XmlMapper();
xmlMapper.registerModule(new XmlNodeModule()); //Required to enable deserialization.
//Root name must be set to prevent default XmlNode root name.
String xml = xmlMapper.writerWithDefaultPrettyPrinter().withRootName("greeting").writeValueAsString(node); 
```
Result: `<greeting>Hello, World!</greeting>`
### Deserializing from XML
```java
//Ensure XmlNodeModule is registered.
XmlNode parsedNode = xmlMapper.readValue(xml, XmlNode.class);
```
More test cases are in the test directory.

## Known Issues
### CDATA Handling
#### Problem:
CDATA sections are not preserved during round-trip serialization/deserialization.

#### Example:
Input: `<data><![CDATA[Some <CDATA> content]]></data>`

Output after round-trip: `<data>Some &lt;CDATA> content</data>`

#### Cause:
The underlying XML parser (default Woodstox StAX implementation) may normalize CDATA into text nodes.

#### Workaround:
Passing an instance of `XMLInputFactory` with `IS_COALESCING` flag disabled and `P_REPORT_CDATA` flag enabled:
```java
try (StringReader reader = new StringReader(inputXml)) {
    XMLInputFactory xmlInputFactory = new WstxInputFactory();
    xmlInputFactory.setProperty(XMLInputFactory2.P_REPORT_CDATA, Boolean.TRUE);
    xmlInputFactory.setProperty(XMLInputFactory2.IS_COALESCING, Boolean.FALSE);
    XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(reader);
    try (FromXmlParser parser = xmlMapper.getFactory().createParser(streamReader)) {
        //...pass the parser instance to XmlMapper.readValue()
    }
}
```
Refer to [GitHub issue](https://github.com/FasterXML/jackson-dataformat-xml/issues/746).

## Contributing
Pull requests are welcome! For major changes, open an issue first to discuss the proposed changes.