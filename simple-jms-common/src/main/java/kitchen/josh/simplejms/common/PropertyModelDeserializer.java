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
    public PropertyModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            ObjectNode node = p.readValueAsTree();
            Map<String, JsonNode> properties = toMap(node.fields());
            if (properties.size() != 3) {
                throw new JsonParseException(p, "Expected 3 properties");
            }

            String name = Optional.ofNullable(properties.get("name")).filter(JsonNode::isTextual).map(JsonNode::asText).orElseThrow(() -> new JsonParseException(p, "Expected field 'name'"));
            String type = Optional.ofNullable(properties.get("type")).filter(JsonNode::isTextual).map(JsonNode::asText).orElseThrow(() -> new JsonParseException(p, "Expected field 'type'"));
            JsonNode valueNode = Optional.ofNullable(properties.get("value")).orElseThrow(() -> new JsonParseException(p, "Expected field 'value'"));
            Object value = parseValue(valueNode, type).orElseThrow(() -> new JsonParseException(p, "Expected field 'type' to have a Java primitive value"));

            return new PropertyModel(name, type, value);
        } catch (IllegalArgumentException iae) {
            throw new JsonParseException(p, iae.getMessage());
        }
    }

    private static <K, V> Map<K, V> toMap(Iterator<Map.Entry<K, V>> iterator) {
        Map<K, V> map = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static Optional<Object> parseValue(JsonNode valueNode, String type) {
        switch (type) {
            case "Boolean":
                return toBoolean(valueNode);
            case "Byte":
                return toByte(valueNode);
            case "Short":
                return toShort(valueNode);
            case "Integer":
                return toInt(valueNode);
            case "Long":
                return toLong(valueNode);
            case "Float":
                return toFloat(valueNode);
            case "Double":
                return toDouble(valueNode);
            case "String":
                return toString(valueNode);
            default:
                return Optional.empty();
        }
    }

    private static Optional<Object> toLong(JsonNode node) {
        if (node.isInt()) {
            return Optional.of((long) node.asInt());
        }
        if (node.isLong()) {
            return Optional.of(node.asLong());
        }
        return Optional.empty();
    }

    private static Optional<Object> toBoolean(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isBoolean)
                .map(JsonNode::asBoolean);
    }

    private static Optional<Object> toByte(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(n -> convertNumberToTargetClass(n.asInt(), Byte.class));
    }

    private static Optional<Object> toShort(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(n -> convertNumberToTargetClass(n.asInt(), Short.class));
    }

    private static Optional<Object> toInt(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isInt)
                .map(JsonNode::asInt);
    }

    private static Optional<Object> toFloat(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isDouble)
                .map(JsonNode::asDouble)
                .map(PropertyModelDeserializer::doubleToFloat);
    }

    private static Optional<Object> toDouble(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isDouble)
                .map(JsonNode::asDouble);
    }

    private static Optional<Object> toString(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText);
    }

    private static float doubleToFloat(double d) {
        float f = Float.parseFloat(Double.toString(d));
        if (!Float.toString(f).equals(Double.toString(d))) {
            throw new IllegalArgumentException("loss of precision casting double to float");
        }
        return f;
    }
}
