package cowin.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassAccessor {

  public List<String> getStaticFieldValuesAsString(@NonNull final Class<?> clazz) {
    final List<String> fieldValues = new ArrayList<>();
    Arrays.stream(clazz.getFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .forEach(
            field -> {
              try {
                fieldValues.add(field.get(null).toString());
              } catch (final Exception ignored) {
              }
            });
    return fieldValues;
  }
}
