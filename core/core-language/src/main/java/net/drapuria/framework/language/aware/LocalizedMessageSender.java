package net.drapuria.framework.language.aware;

import net.drapuria.framework.language.message.AbstractLocalizedMessage;

public interface LocalizedMessageSender<R, T extends Enum<?>> extends LanguageAware {

    void sendLocalizedMessage(AbstractLocalizedMessage<R, ?, T, ?> localizedMessage);

}