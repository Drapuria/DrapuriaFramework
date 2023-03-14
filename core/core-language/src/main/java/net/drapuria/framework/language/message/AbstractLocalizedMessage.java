package net.drapuria.framework.language.message;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.drapuria.framework.language.LanguageService;
import net.drapuria.framework.language.message.exception.LocalizedMessageClassNotFoundException;
import net.drapuria.framework.language.message.placeholder.PlaceholderTransformer;
import net.drapuria.framework.language.message.prefix.PrefixData;
import net.drapuria.framework.language.message.placeholder.IPlaceholderValue;
import net.drapuria.framework.language.message.placeholder.PlaceholderValue;
import net.drapuria.framework.language.message.placeholder.SimplePlaceholderValue;
import net.drapuria.framework.language.message.placeholder.TranslateFormat;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public abstract class AbstractLocalizedMessage<R, C, T, S> {

    private static final LanguageService service = LanguageService.getService;
    private static Constructor<?> constructor;
    @Setter
    private static TranslateFormat usedTranslateFormat = TranslateFormat.FORMAT;

    @Setter(AccessLevel.NONE)
    protected final List<IPlaceholderValue> placeholderValues = Lists.newArrayList();

    protected PrefixData<?> prefixData;
    private final String key;
    protected C color;
    protected T showType;
    protected S sound;
    protected Locale locale = null;
    protected Function<R, Locale> localeGetFunction;
    protected TranslateFormat translateFormat = usedTranslateFormat;


    public AbstractLocalizedMessage(final String key) {
        this.key = key;
    }

    public String getMessage() {
        final Locale locale = this.locale == null ? service.getDefaultLocale() : this.locale;
        return getMessage(locale);
    }

    public String getMessage(final Locale locale) {
        String message = service.getTranslatedString(locale, key);
        if (this.translateFormat == TranslateFormat.FORMAT)
            return String.format(locale, message, placeholderValues.stream().map(IPlaceholderValue::getValue).toArray());
        else {
            for (IPlaceholderValue placeholderValue : placeholderValues)
                message = message.replace("{" + placeholderValue.getPlaceholder() + "}", placeholderValue.getValue());
            return message;
        }
    }

    protected Locale getLocaleOf(R receiver) {
        if (this.localeGetFunction == null)
            return this.locale == null ? service.getDefaultLocale() : locale;
        return this.localeGetFunction.apply(receiver);
    }

    public abstract void send(R receiver);

    public abstract void broadcast();

    @SneakyThrows
    public static <R, C, T, S, E extends AbstractLocalizedMessage<R, C, T, S>> E of(final String key) {
        if (constructor != null)
            return (E) constructor.newInstance(key);
        if (service.getLocalizedMessageClass() == null)
            throw new LocalizedMessageClassNotFoundException("Cannot create constructor instance! Please an LocalizedMessage implementation.");
        constructor = service.getLocalizedMessageClass().getConstructor(String.class);
        return of(key);
    }
}