package net.drapuria.framework.bukkit.reflection.resolver.wrapper;

import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import org.bukkit.ChatColor;


public class ChatModifierWrapper extends WrapperAbstract {

    private static final Class<?> CHAT_MODIFIER_TYPE;
    private static final Class<?> ENUM_CHAT_FORMAT_TYPE;

    private static final MethodWrapper<?> GET_CHAT_FORMAT_METHOD;
    private static final MethodWrapper<Boolean> IS_BOLD_METHOD;
    private static final MethodWrapper<Boolean> IS_ITALIC_METHOD;
    private static final MethodWrapper<Boolean> IS_STRIKETHROUGH_METHOD;
    private static final MethodWrapper<Boolean> IS_UNDERLINED_METHOD;
    private static final MethodWrapper<Boolean> IS_RANDOM_METHOD;

    static {

        try {
            CHAT_MODIFIER_TYPE = Minecraft.getChatModifierClass();

            ENUM_CHAT_FORMAT_TYPE = Minecraft.getEnumChatFormatClass();

            MethodResolver methodResolver = new MethodResolver(CHAT_MODIFIER_TYPE);

            GET_CHAT_FORMAT_METHOD = methodResolver.resolve(ENUM_CHAT_FORMAT_TYPE, 0);
            IS_BOLD_METHOD = methodResolver.resolveWrapper("isBold");
            IS_ITALIC_METHOD = methodResolver.resolveWrapper("isItalic");
            IS_STRIKETHROUGH_METHOD = methodResolver.resolveWrapper("isStrikethrough");
            IS_UNDERLINED_METHOD = methodResolver.resolveWrapper("isUnderlined");
            IS_RANDOM_METHOD = methodResolver.resolveWrapper("isRandom");
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

    }

    private final Object handle;

    ChatModifierWrapper(Object handle) {
        this.handle = handle;
    }

    public ChatColor getColor() {
        return Minecraft.getChatColorConverter().getSpecific(GET_CHAT_FORMAT_METHOD.invoke(this.handle));
    }

    public boolean isBold() {
        return IS_BOLD_METHOD.invoke(this.handle);
    }

    public boolean isStrikethrough() {
        return IS_STRIKETHROUGH_METHOD.invoke(this.handle);
    }

    public boolean isUnderlined() {
        return IS_UNDERLINED_METHOD.invoke(this.handle);
    }

    public boolean isRandom() {
        return IS_RANDOM_METHOD.invoke(this.handle);
    }

    public boolean isItalic() {
        return IS_ITALIC_METHOD.invoke(this.handle);
    }

    @Override
    public boolean exists() {
        return handle != null;
    }
}
