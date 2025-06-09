package com.github.xmlnode;

import com.ctc.wstx.stax.WstxInputFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.codehaus.stax2.XMLInputFactory2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlNodeTest {
    private static final XmlMapper xmlMapper = getXmlMapper();

    public static void main(String[] args) throws JsonProcessingException {
        List<Owner> owners = Arrays.asList(
                new Owner("Joe", 36, "M"), new Owner("Lisa", 42, "F"),
                new Owner("Jake", 29, "M")
        );

        XmlNode xmlNode = new XmlNode(xmlMapper.getNodeFactory());
        xmlNode.putAttribute("basic", true).putAttribute("i", 500f);
        XmlNode examples = xmlNode.putObject("Examples");
        examples.putAttribute("overlyComplicated", "yes");
        XmlNode puppy = examples.put("Puppy", true);
        puppy.putAttribute("lovable", 10);
        puppy.put("breed", "Dachshund");
        examples.put("Apple", 2d);
        examples.putPOJO("Jet", new Jet("Gulfstream", "G280", BigInteger.valueOf(1037), owners));
        XmlNode single = xmlNode.putObject("Single");
        single.put("One", 1);
        String xml = xmlMapper.writerWithDefaultPrettyPrinter().withRootName("Random")
                .withFeatures(ToXmlGenerator.Feature.WRITE_XML_DECLARATION).writeValueAsString(xmlNode);
        System.out.println(xml);

        xmlNode = xmlMapper.readValue(xml, XmlNode.class);
        System.out.println(xmlMapper.writerWithDefaultPrettyPrinter()
                .withFeatures(ToXmlGenerator.Feature.UNWRAP_ROOT_OBJECT_NODE)
                .withFeatures(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withRootName("RandomReserialized").writeValueAsString(xmlNode));


        xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<animal>\n" +
                "  <dog name=\"tucker\" age=\"6\">woof woof!</dog>\n" +
                "  <cat name=\"cody\" age=\"2\">meowwwwwww!</cat>\n" +
                "  <chicken name=\"browny\"><hen age=\"3\"></hen><rooster age=\"4\">cluck cluck!</rooster><![CDATA[" +
                "<description>We are in the chicken element</description>" +
                "]]></chicken>\n" +
                "  <pig name=\"porky\" age=\"4\">oink oink!</pig>\n" +
                "</animal>";
        xmlNode = xmlMapper.readValue(xml, XmlNode.class);
        System.out.println(xmlMapper.writerWithDefaultPrettyPrinter()
                .withFeatures(ToXmlGenerator.Feature.UNWRAP_ROOT_OBJECT_NODE)
                .withFeatures(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withRootName("animalReserialized").writeValueAsString(xmlNode));

        xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Body>\n" +
                "    <ns1:GetWeatherResponse xmlns:ns1=\"http://example.com/weather\">\n" +
                "      <ns1:Forecast>\n" +
                "        <ns1:Day date=\"2023-10-01\">\n" +
                "          <ns1:Temperature>72</ns1:Temperature>\n" +
                "        </ns1:Day>\n" +
                "        <ns1:Day date=\"2023-10-02\">\n" +
                "          <ns1:Temperature>68</ns1:Temperature>\n" +
                "        </ns1:Day>\n" +
                "      </ns1:Forecast>\n" +
                "    </ns1:GetWeatherResponse>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        xmlNode = xmlMapper.readValue(xml, XmlNode.class);
        System.out.println(xmlMapper.writerWithDefaultPrettyPrinter()
                .withFeatures(ToXmlGenerator.Feature.UNWRAP_ROOT_OBJECT_NODE)
                .withFeatures(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withRootName("soapenv:Envelope").writeValueAsString(xmlNode));
    }

    private static XmlMapper getXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.findAndRegisterModules();
        //xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        xmlMapper.registerModule(new XmlNodeModule());
        return xmlMapper;
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testDeserializationSerialization(String inputXml, String rootName) throws IOException, XMLStreamException {
        try (StringReader reader = new StringReader(inputXml)) {
            XMLInputFactory xmlInputFactory = new WstxInputFactory();
            xmlInputFactory.setProperty(XMLInputFactory2.P_REPORT_CDATA, Boolean.TRUE);
            xmlInputFactory.setProperty(XMLInputFactory2.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(reader);
            try (FromXmlParser parser = xmlMapper.getFactory().createParser(streamReader)) {
                XmlNode node = xmlMapper.readValue(parser, XmlNode.class);
                String outputXml = getXmlMapper().writer().withRootName(rootName).writeValueAsString(node);
                System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
                assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
            }
        }
    }

    public static Stream<Arguments> getParameters() {
        return Stream.of(
                Arguments.argumentSet("Basic element", "<dog name=\"Buddy\" age=\"5\"/>", "dog"),
                Arguments.argumentSet("Text content", "<greeting>Hello, World!</greeting>", "greeting"),
                Arguments.argumentSet("Child element", "<parent>\n" +
                        "  <child id=\"1\"/>\n" +
                        "</parent>", "parent"),
                Arguments.argumentSet(
                        "Mixed content", "<message><header>Alert</header>This is important!</message>",
                        "message"
                ),
                Arguments.argumentSet("3 Levels", "<root>\n" +
                        "  <level1>\n" +
                        "    <level2 attr=\"A\">\n" +
                        "      <level3>Text</level3>\n" +
                        "    </level2>\n" +
                        "  </level1>\n" +
                        "</root>", "root"),
                Arguments.argumentSet(
                        "Special characters", "<content attr=\"A &amp; B\">&lt;Hello&gt; Bob &amp; Alice</content>",
                        "content"
                ),
                Arguments.argumentSet("Empty element", "<empty/>", "empty"),
                Arguments.argumentSet("SOAP Message", "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <ns1:GetWeatherResponse xmlns:ns1=\"http://example.com/weather\">\n" +
                        "      <ns1:Forecast>\n" +
                        "        <ns1:Day date=\"2023-10-01\">\n" +
                        "          <ns1:Temperature>72</ns1:Temperature>\n" +
                        "        </ns1:Day>\n" +
                        "        <ns1:Day date=\"2023-10-02\">\n" +
                        "          <ns1:Temperature>68</ns1:Temperature>\n" +
                        "        </ns1:Day>\n" +
                        "      </ns1:Forecast>\n" +
                        "    </ns1:GetWeatherResponse>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("Basic SOAP Message", "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "                  xmlns:ns1=\"http://example.com/ns\">\n" +
                        "  <soapenv:Header>\n" +
                        "    <ns1:AuthToken>ABC123</ns1:AuthToken>\n" +
                        "  </soapenv:Header>\n" +
                        "  <soapenv:Body>\n" +
                        "    <ns1:GetPrice>\n" +
                        "      <ns1:ItemID>12345</ns1:ItemID>\n" +
                        "    </ns1:GetPrice>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("SOAP Message with nested elements and attributes", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "                  xmlns:order=\"http://example.com/order\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <order:PlaceOrder>\n" +
                        "      <order:Item order:ID=\"987\" currency=\"USD\">\n" +
                        "        <order:Name>Laptop</order:Name>\n" +
                        "        <order:Quantity>2</order:Quantity>\n" +
                        "      </order:Item>\n" +
                        "    </order:PlaceOrder>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("SOAP Fault with mixed namespaces and attributes", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <soapenv:Fault>\n" +
                        "      <faultcode>soapenv:Server</faultcode>\n" +
                        "      <faultstring>Internal Error</faultstring>\n" +
                        "      <detail>\n" +
                        "        <ns2:Error xmlns:ns2=\"http://example.com/error\">\n" +
                        "          <ns2:Code>500</ns2:Code>\n" +
                        "          <ns2:Message>Database timeout</ns2:Message>\n" +
                        "        </ns2:Error>\n" +
                        "      </detail>\n" +
                        "    </soapenv:Fault>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("SOAP WS-Security headers", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "                  xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n" +
                        "  <soapenv:Header>\n" +
                        "    <wsse:Security>\n" +
                        "      <wsse:UsernameToken>\n" +
                        "        <wsse:Username>user1</wsse:Username>\n" +
                        "        <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">\n" +
                        "          secret\n" +
                        "        </wsse:Password>\n" +
                        "      </wsse:UsernameToken>\n" +
                        "    </wsse:Security>\n" +
                        "  </soapenv:Header>\n" +
                        "  <soapenv:Body/>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("SOAP Mixed Content", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <message>Warning: <alert level=\"high\">System Overload</alert>. Please close any intensive processes.</message>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("Namespace redefinition", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<root xmlns:ns=\"http://example.com/ns1\">\n" +
                        "  <child xmlns:ns=\"http://example.com/ns2\">\n" +
                        "    <ns:Element>Value</ns:Element>\n" +
                        "  </child>\n" +
                        "</root>", "root"),
                Arguments.argumentSet("SOAP Message with binary data", "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <ns1:UploadDocument xmlns:ns1=\"http://example.com/docs\">\n" +
                        "      <ns1:Content>U3VwZXIgU2VjcmV0IERvY3VtZW50</ns1:Content>\n" +
                        "    </ns1:UploadDocument>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>", "soapenv:Envelope"),
                Arguments.argumentSet("Element with attributes and text content", "<animal>\n" +
                        "    <dog name=\"tucker\" age=\"6\">woof woof!</dog>\n" +
                        "    <cat name=\"cody\" age=\"2\">meowwwwwww!</cat>\n" +
                        "    <chicken name=\"browny\">\n" +
                        "        <hen age=\"3\"></hen>cluck cluck!\n" +
                        "    </chicken>\n" +
                        "    <pig name=\"porky\" age=\"4\">oink oink!</pig>\n" +
                        "</animal>", "animal"),
                Arguments.argumentSet("Nested elements with mixed content", "<root>\n" +
                        "    <parent>\n" +
                        "        <child>Text content<subchild>Subchild text</subchild>More text\n" +
                        "        </child>\n" +
                        "    </parent>\n" +
                        "</root>", "root"),
                Arguments.argumentSet("Element with CDATA section",
                        "<data><![CDATA[Some <CDATA> &gt;< content]]></data>", "data"),
                Arguments.argumentSet("Namespaces, children and mixed content",
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<parent xmlns:ns1=\"http://example.com/weather\">Parent node\n" +
                                "  <level1>Testing\n" +
                                "    <ns1:level2 attr=\"A\">\n" +
                                "      <level3>Text</level3>\n" +
                                "    </ns1:level2>Children\n" +
                                "  </level1>\n" +
                                "</parent>", "parent")
        );
    }

    @Test
    public void testBuildingBasicElement() throws JsonProcessingException {
        String inputXml = "<dog name=\"Buddy\" age=\"5\"/>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putAttribute("name", "Buddy");
        node.putAttribute("age", 5);
        String outputXml = getXmlMapper().writer().withRootName("dog").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingTextContentElement() throws JsonProcessingException {
        String inputXml = "<greeting>Hello, World!</greeting>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.setValue("Hello, World!");
        String outputXml = getXmlMapper().writer().withRootName("greeting").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingChildElement() throws JsonProcessingException {
        String inputXml = "<parent>\n" +
                "  <child id=\"1\"/>\n" +
                "</parent>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        XmlNode child = node.putObject("child");
        child.putAttribute("id", 1);
        String outputXml = getXmlMapper().writer().withRootName("parent").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testBuildingMixedContent() throws Exception {
        String inputXml = "<message><header>Alert</header>This is important!</message>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.put("header", "Alert");
        node.setValue("This is important!");
        String outputXml = getXmlMapper().writer().withRootName("message").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuilding3Levels() throws JsonProcessingException {
        String inputXml = "<root>\n" +
                "  <level1>\n" +
                "    <level2 attr=\"A\">\n" +
                "      <level3>Text</level3>\n" +
                "    </level2>\n" +
                "  </level1>\n" +
                "</root>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        XmlNode level1 = node.putObject("level1");
        XmlNode level2 = level1.putObject("level2");
        level2.putAttribute("attr", 'A');
        level2.put("level3", "Text");
        String outputXml = getXmlMapper().writer().withRootName("root").withDefaultPrettyPrinter().writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testBuildingSpecialCharacters() throws JsonProcessingException {
        String inputXml = "<content attr=\"A &amp; B\">&lt;Hello&gt; Bob &amp; Alice</content>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putAttribute("attr", "A & B");
        node.setValue("<Hello> Bob & Alice");
        String outputXml = getXmlMapper().writer().withRootName("content").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingEmptyElements() throws JsonProcessingException {
        String inputXml = "<empty/>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        String outputXml = getXmlMapper().writer().withRootName("empty").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingSOAPElement() throws JsonProcessingException {
        String inputXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Body>\n" +
                "    <ns1:GetWeatherResponse xmlns:ns1=\"http://example.com/weather\">\n" +
                "      <ns1:Forecast>\n" +
                "        <ns1:Day date=\"2023-10-01\">\n" +
                "          <ns1:Temperature>72</ns1:Temperature>\n" +
                "        </ns1:Day>\n" +
                "        <ns1:Day date=\"2023-10-02\">\n" +
                "          <ns1:Temperature>80</ns1:Temperature>\n" +
                "        </ns1:Day>\n" +
                "      </ns1:Forecast>\n" +
                "    </ns1:GetWeatherResponse>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode weatherResponse = body.putObject("GetWeatherResponse", "ns1");
        weatherResponse.putNamespaceURI("ns1", "http://example.com/weather");
        XmlNode forecast = weatherResponse.putObject("Forecast", "ns1");
        XmlNode day = forecast.putObject("Day", "ns1");
        day.putAttribute("date", "2023-10-01");
        XmlNode temperature = day.putObject("Temperature", "ns1");
        temperature.setValue(72L);
        day = forecast.putObject("Day", "ns1");
        day.putAttribute("date", "2023-10-02");
        temperature = day.putObject("Temperature", "ns1");
        temperature.setValue(68L);

        temperature = (XmlNode) node.get("Body").get("GetWeatherResponse").get("Forecast").get(1).get("Temperature");
        temperature.remove(0);
        temperature.setValue(80L);

        String outputXml = getXmlMapper().writer().withDefaultPrettyPrinter().withRootName("soapenv:Envelope").writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingBasicSOAPMessage() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                  xmlns:ns1=\"http://example.com/ns\">\n" +
                "  <soapenv:Header>\n" +
                "    <ns1:AuthToken>ABC123</ns1:AuthToken>\n" +
                "  </soapenv:Header>\n" +
                "  <soapenv:Body>\n" +
                "    <ns1:GetPrice>\n" +
                "      <ns1:ItemID>12345</ns1:ItemID>\n" +
                "    </ns1:GetPrice>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        node.putNamespaceURI("ns1", "http://example.com/ns");
        XmlNode header = node.putObject("Header", "soapenv");
        XmlNode authToken = header.putObject("AuthToken", "ns1");
        authToken.setValue("ABC123");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode price = body.putObject("GetPrice", "ns1");
        XmlNode itemID = price.putObject("ItemID", "ns1");
        itemID.setValue("12345");

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingNestedSOAPMessageWithAttributes() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                  xmlns:order=\"http://example.com/order\">\n" +
                "  <soapenv:Body>\n" +
                "    <order:PlaceOrder>\n" +
                "      <order:Item order:ID=\"987\" currency=\"USD\">\n" +
                "        <order:Name>Laptop</order:Name>\n" +
                "        <order:Quantity>2</order:Quantity>\n" +
                "      </order:Item>\n" +
                "    </order:PlaceOrder>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        node.putNamespaceURI("order", "http://example.com/order");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode placeOrder = body.putObject("PlaceOrder", "order");
        XmlNode item = placeOrder.putObject("Item", "order");
        item.putAttribute("order", "ID", 987);
        item.putAttribute("currency", "USD");
        XmlNode name = item.putObject("Name", "order");
        name.setValue("Laptop");
        XmlNode quantity = item.putObject("Quantity", "order");
        quantity.setValue(2);

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingSOAPFaultWithMixedNamespaces() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Body>\n" +
                "    <soapenv:Fault>\n" +
                "      <faultcode>soapenv:Server</faultcode>\n" +
                "      <faultstring>Internal Error</faultstring>\n" +
                "      <detail>\n" +
                "        <ns2:Error xmlns:ns2=\"http://example.com/error\">\n" +
                "          <ns2:Code>500</ns2:Code>\n" +
                "          <ns2:Message>Database timeout</ns2:Message>\n" +
                "        </ns2:Error>\n" +
                "      </detail>\n" +
                "    </soapenv:Fault>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode fault = body.putObject("Fault", "soapenv");
        fault.put("faultcode", "soapenv:Server");
        fault.put("faultstring", "Internal Error");
        XmlNode detail = fault.putObject("detail");
        XmlNode error = detail.putObject("Error", "ns2");
        error.putNamespaceURI("ns2", "http://example.com/error");
        XmlNode code = error.putObject("Code", "ns2");
        code.setValue(500);
        XmlNode message = error.putObject("Message", "ns2");
        message.setValue("Database timeout");

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testBuildingSOAPSecurityHeaders() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                  xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n" +
                "  <soapenv:Header>\n" +
                "    <wsse:Security>\n" +
                "      <wsse:UsernameToken>\n" +
                "        <wsse:Username>user1</wsse:Username>\n" +
                "        <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">\n" +
                "          secret\n" +
                "        </wsse:Password>\n" +
                "      </wsse:UsernameToken>\n" +
                "    </wsse:Security>\n" +
                "  </soapenv:Header>\n" +
                "  <soapenv:Body/>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        node.putNamespaceURI("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        XmlNode header = node.putObject("Header", "soapenv");
        XmlNode security = header.putObject("Security", "wsse");
        XmlNode usernameToken = security.putObject("UsernameToken", "wsse");
        XmlNode username = usernameToken.putObject("Username", "wsse");
        username.setValue("user1");
        XmlNode password = usernameToken.putObject("Password", "wsse");
        password.putAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        password.setValue("secret");
        node.putObject("Body", "soapenv");

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testBuildingSOAPMixedContent() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Body>\n" +
                "    <message>Warning: <alert level=\"high\">System Overload</alert></message>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode message = body.putObject("message");
        message.setValue("Warning: ");
        XmlNode alert = message.putObject("alert");
        alert.putAttribute("level", "high");
        alert.setValue("System Overload");

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testBuildingRedefinedNamespaces() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<root xmlns:ns=\"http://example.com/ns1\">\n" +
                "  <child xmlns:ns=\"http://example.com/ns2\">\n" +
                "    <ns:Element>Value</ns:Element>\n" +
                "  </child>\n" +
                "</root>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("ns", "http://example.com/ns1");
        XmlNode child = node.putObject("child");
        child.putNamespaceURI("ns", "http://example.com/ns2");
        XmlNode element = child.putObject("Element", "ns");
        element.setValue("Value");

        String outputXml = getXmlMapper().writer()
                .withRootName("root")
                .withDefaultPrettyPrinter()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testBuildingSOAPMessageWithBinaryData() throws JsonProcessingException {
        String inputXml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Body>\n" +
                "    <ns1:UploadDocument xmlns:ns1=\"http://example.com/docs\">\n" +
                "      <ns1:Content>U3VwZXIgU2VjcmV0IERvY3VtZW50</ns1:Content>\n" +
                "    </ns1:UploadDocument>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.putNamespaceURI("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        XmlNode body = node.putObject("Body", "soapenv");
        XmlNode uploadDocument = body.putObject("UploadDocument", "ns1");
        uploadDocument.putNamespaceURI("ns1", "http://example.com/docs");
        XmlNode content = uploadDocument.putObject("Content", "ns1");
        content.setValue("Super Secret Document".getBytes(StandardCharsets.UTF_8));

        String outputXml = getXmlMapper().writer()
                .withRootName("soapenv:Envelope")
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .withDefaultPrettyPrinter()
                .writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }

    @Test
    public void testElementWithAttributesAndTextContent() throws JsonProcessingException {
        String inputXml = "<animal>\n" +
                "    <dog name=\"tucker\" age=\"6\">woof woof!</dog>\n" +
                "    <cat name=\"cody\" age=\"2\">meowwwwwww!</cat>\n" +
                "    <chicken name=\"browny\">\n" +
                "        <hen age=\"3\"></hen>cluck cluck!\n" +
                "    </chicken>\n" +
                "    <pig name=\"porky\" age=\"4\">oink oink!</pig>\n" +
                "</animal>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        XmlNode dog = node.putObject("dog");
        dog.putAttribute("name", "tucker");
        dog.putAttribute("age", 6);
        dog.setValue("woof woof!");
        XmlNode cat = node.putObject("cat");
        cat.putAttribute("name", "cody");
        cat.putAttribute("age", 2);
        cat.setValue("meowwwwwww!");
        XmlNode chicken = node.putObject("chicken");
        chicken.putAttribute("name", "browny");
        XmlNode hen = chicken.putObject("hen");
        hen.putAttribute("age", 3);
        chicken.setValue("cluck cluck!");
        XmlNode pig = node.putObject("pig");
        pig.putAttribute("name", "porky");
        pig.putAttribute("age", 4);
        pig.setValue("oink oink!");
        String outputXml = getXmlMapper().writer().withRootName("animal").withDefaultPrettyPrinter().writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }

    @Test
    public void testNestedElementsWithMixedContent() throws JsonProcessingException {
        String inputXml = "<root>\n" +
                "    <parent>\n" +
                "        <child>Text content<subchild>Subchild text</subchild>More text\n" +
                "        </child>\n" +
                "    </parent>\n" +
                "</root>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        XmlNode parent = node.putObject("parent");
        XmlNode child = parent.putObject("child");
        child.setValue("Text content");
        XmlNode subchild = child.putObject("subchild");
        subchild.setValue("Subchild text");
        child.setValue("More text");
        String outputXml = getXmlMapper().writer().withRootName("root").withDefaultPrettyPrinter().writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).ignoreWhitespace().areIdentical();
    }


    @Test
    public void testElementWithCDATASection() throws JsonProcessingException {
        String inputXml = "<data><![CDATA[Some <CDATA> &gt;< content]]></data>";
        XmlNode node = new XmlNode(xmlMapper.getNodeFactory());
        node.setValue(new CDATANode("<![CDATA[Some <CDATA> &gt;< content]]>"));
        String outputXml = getXmlMapper().writer().withRootName("data").withDefaultPrettyPrinter().writeValueAsString(node);
        System.out.printf("%s%n%n%s%n%n", inputXml, outputXml);
        assertThat(outputXml).and(inputXml).areIdentical();
    }
}
