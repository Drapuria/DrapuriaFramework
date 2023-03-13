package net.drapuria.framework.language.aware;

import net.drapuria.framework.language.Translateable;

public interface LocalizedMessageSender extends LanguageAware {

    void sendLocalizedMessage(final String messageKey, Translateable<?>... translateables);

    void sendLoalizedMessage(final String messageKey, final boolean itemBar, Translateable<?>... translateables);

}
