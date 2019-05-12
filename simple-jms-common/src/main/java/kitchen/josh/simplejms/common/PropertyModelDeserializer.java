package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.NumberUtils.convertNumberToTargetClass;

public class PropertyModelDeserializer extends JsonDeserializer<PropertyModel> {

    @Override
    public PropertyModel deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        try {
            Map<String, JsonNode> properties = parseProperties(parser);
            String name = parseName(parser, properties);
            String type = parseType(parser, properties);
            Object value = parseValue(parser, properties, type);
            return new PropertyModel(name, type, value);
        } catch (IllegalArgumentException iae) {
            throw new JsonParseException(parser, iae.getMessage());
        }
    }

    private static Map<String, JsonNode> parseProperties(JsonParser parser) throws IOException {
        ObjectNode node = parser.readValueAsTree();
        Map<String, JsonNode> properties = toMap(node.fields());
        if (properties.size() != 3) {
            throw new JsonParseException(parser, "Expected 3 properties");
        }
        return properties;
    }

    private static String parseName(JsonParser parser, Map<String, JsonNode> properties) throws JsonParseException {
        return (String) parsePropertyByNameAndType(parser, properties, "name", "String");
    }

    private static String parseType(JsonParser parser, Map<String, JsonNode> properties) throws JsonParseException {
        return (String) parsePropertyByNameAndType(parser, properties, "type", "String");
    }

    private static Object parseValue(JsonParser parser, Map<String, JsonNode> properties, String type) throws JsonParseException {
        return parsePropertyByNameAndType(parser, properties, "value", type);
    }

    private static Object parsePropertyByNameAndType(JsonParser parser, Map<String, JsonNode> properties, String name, String type) throws JsonParseException {
        return parseValueWithType(properties.get(name), type)
                .orElseThrow(() -> new JsonParseException(parser, "Expected field name'" + type + "'"));
    }

    private static Optional<?> parseValueWithType(JsonNode valueNode, String type) {
        switch (type) {
            case "Boolean":
                return parseBooleanValue(valueNode);
            case "Byte":
                return parseByteValue(valueNode);
            case "Short":
                return parseShortValue(valueNode);
            case "Integer":
                return parseIntValue(valueNode);
            case "Long":
                return parseLongValue(valueNode);
            case "Float":
                return parseFloatValue(valueNode);
            case "Double":
                return parseDoubleValue(valueNode);
            case "String":
                return parseStringValue(valueNode);
            default:
                return Optional.empty();
        }
    }

    private static Optional<Boolean> parseBooleanValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isBoolean)
                .map(JsonNode::asBoolean);
    }

    private static Optional<Byte> parseByteValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(n -> convertNumberToTargetClass(n.asInt(), Byte.class));
    }

    private static Optional<Short> parseShortValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(n -> convertNumberToTargetClass(n.asInt(), Short.class));
    }

    private static Optional<Integer> parseIntValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(JsonNode::asInt);
    }

    private static Optional<Long> parseLongValue(JsonNode node) {
        if (node.isInt()) {
            return Optional.of((long) node.asInt());
        }
        if (node.isLong()) {
            return Optional.of(node.asLong());
        }
        return Optional.empty();
    }

    private static Optional<Float> parseFloatValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isDouble)
                .map(JsonNode::asDouble)
                .map(PropertyModelDeserializer::doubleToFloat);
    }

    private static Optional<Double> parseDoubleValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isDouble)
                .map(JsonNode::asDouble);
    }

    private static Optional<String> parseStringValue(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText);
    }

    private static <K, V> Map<K, V> toMap(Iterator<Map.Entry<K, V>> iterator) {
        Map<K, V> map = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static float doubleToFloat(double d) {
        float f = Float.parseFloat(Double.toString(d));
        if (!Float.toString(f).equals(Double.toString(d))) {
            throw new IllegalArgumentException("loss of precision casting double to float");
        }
        return f;
    }
}
