package kitchen.josh.simplejms.common;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.list;

public class PropertyModelFactory {

    public List<PropertyModel> create(Properties properties) {
        return list(properties.getPropertyNames()).stream()
                .map(name -> convert(properties, name))
                .collect(Collectors.toList());
    }

    private static PropertyModel convert(Properties properties, String name) {
        Object value = properties.getObjectProperty(name);
        Class<?> type = value.getClass();
        return new PropertyModel(name, type.getSimpleName(), value);
    }
}
