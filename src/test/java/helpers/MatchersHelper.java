package helpers;

import lombok.experimental.UtilityClass;
import org.mockito.AdditionalMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;

@UtilityClass
public class MatchersHelper {

    public static <T> T anyOrNull(Class<T> clazz) {
        return AdditionalMatchers.or(any(clazz), isNull());
    }

    public static <T> T anyOrNull() {
        return AdditionalMatchers.or(any(), isNull());
    }
}
