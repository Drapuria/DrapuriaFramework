package net.drapuria.framework.bukkit.reflection.resolver.wrapper;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.ComponentParser;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import lombok.Getter;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.ConstructorResolver;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import net.drapuria.framework.bukkit.reflection.resolver.ResolverQuery;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.OBCClassResolver;
import org.bukkit.ChatColor;


import java.io.StringReader;
import java.util.Iterator;

/**
 * Represents a chat component added in Minecraft 1.7.2
 *
 * @credit ProtoclLib
 * @author Kristian
 *
 * @modified LeeGod
 *
 */
public class ChatComponentWrapper extends WrapperAbstract implements Cloneable {

    private static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
    private static final OBCClassResolver OBC_CLASS_RESOLVER = new OBCClassResolver();

    private static final Class<?> SERIALIZER = NMS_CLASS_RESOLVER.resolveSilent("ChatSerializer", "IChatBaseComponent$ChatSerializer");

    public static Class<?> GENERIC_TYPE;

    private static final Gson GSON;
    private static MethodWrapper<?> DESERIALIZE;

    private static final MethodWrapper<?> GET_CHAT_MODIFIER;
    private static final MethodWrapper<String> GET_TEXT;

    private static final MethodWrapper<?> SERIALIZE_COMPONENT;
    private static final MethodWrapper<?> CONSTRUCT_COMPONENT;
    private static final ConstructorWrapper<?> CONSTRUCT_TEXT_COMPONENT;

    static {
        // Get a component from a standard Minecraft message
        try {
            CONSTRUCT_COMPONENT = new MethodWrapper<>(OBC_CLASS_RESOLVER
                    .resolve("util.CraftChatMessage")
                    .getMethod("fromString", String.class));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        GENERIC_TYPE = Minecraft.getIChatBaseComponentClass();
        Class<?> CHAT_MODIFIER_TYPE = Minecraft.getChatModifierClass();

        GET_TEXT = new MethodResolver(GENERIC_TYPE).resolveWrapper("getText");

        try {
            GET_CHAT_MODIFIER = new MethodResolver(GENERIC_TYPE).resolve(CHAT_MODIFIER_TYPE, 0);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        Class<?> chatDeserializerClass;

        try {
            chatDeserializerClass = NMS_CLASS_RESOLVER.resolve("ChatDeserializer");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        MethodResolver methodResolver = new MethodResolver(SERIALIZER);
        FieldResolver fieldResolver = new FieldResolver(SERIALIZER);

        // Retrieve the correct methods
        try {
            SERIALIZE_COMPONENT = methodResolver.resolve(String.class, 0, GENERIC_TYPE);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        try {
            GSON = (Gson) fieldResolver.resolveByFirstType(Gson.class).get(null);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to obtain GSON field", ex);
        }

        DESERIALIZE = new MethodResolver(chatDeserializerClass)
                .resolveWrapper(new ResolverQuery("deserialize", Gson.class, String.class, Class.class, boolean.class));

        if (!DESERIALIZE.exists()) {
            // We'll handle it in the ComponentParser
            DESERIALIZE = null;
        }

        Class<?> chatComponentText;

        try {
            chatComponentText = NMS_CLASS_RESOLVER.resolve("ChatComponentText");
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        // And the component text constructor
        CONSTRUCT_TEXT_COMPONENT = new ConstructorResolver(chatComponentText).resolveWrapper(new Class[] {String.class});
    }

    private static Object deserialize(String json) {
        // Should be non-null on 1.9 and up
        if (DESERIALIZE != null) {
            return DESERIALIZE.invoke(null, GSON, json, GENERIC_TYPE, true);
        }

        // Mock leniency behavior in 1.8
        StringReader str = new StringReader(json);
        return ComponentParser.deserialize(GSON, GENERIC_TYPE, str);
    }

    @Getter
    private Object handle;
    private transient String cache;

    private ChatComponentWrapper(Object handle, String cache) {
        this.handle = handle;
        this.cache = cache;
    }

    @Override
    public boolean exists() {
        return handle != null;
    }

    /**
     * Construct a new chat component wrapper around the given NMS object.
     * @param handle - the NMS object.
     * @return The wrapper.
     */
    public static ChatComponentWrapper fromHandle(Object handle) {
        return new ChatComponentWrapper(handle, null);
    }

    /**
     * Construct a new chat component wrapper from the given JSON string.
     * @param json - the json.
     * @return The chat component wrapper.
     */
    public static ChatComponentWrapper fromJson(String json) {
        return new ChatComponentWrapper(deserialize(json), json);
    }

    /**
     * Construct a wrapper around a new text chat component with the given text.
     * @param text - the text of the text chat component.
     * @return The wrapper around the new chat component.
     */
    public static ChatComponentWrapper fromText(String text) {
        Preconditions.checkNotNull(text, "text cannot be NULL.");
        return fromHandle(CONSTRUCT_TEXT_COMPONENT.newInstance(text));
    }

    /**
     * Construct an array of chat components from a standard Minecraft message.
     * <p>
     * This uses {@link ChatColor} for formating.
     * @param message - the message.
     * @return The equivalent chat components.
     */
    public static ChatComponentWrapper[] fromChatMessage(String message) {
        Object[] components = (Object[]) CONSTRUCT_COMPONENT.invoke(null, message);
        ChatComponentWrapper[] result = new ChatComponentWrapper[components.length];

        for (int i = 0; i < components.length; i++) {
            result[i] = fromHandle(components[i]);
        }
        return result;
    }

    /**
     * Retrieve a copy of this component as a JSON string.
     * <p>
     * Note that any modifications to this JSON string will not update the current component.
     * @return The JSON representation of this object.
     */
    public String getJson() {
        if (cache == null) {
            cache = (String) SERIALIZE_COMPONENT.invoke(null, handle);
        }
        return cache;
    }

    /**
     * Set the content of this component using a JSON object.
     * @param obj - the JSON that represents the new component.
     */
    public void setJson(String obj) {
        this.handle = deserialize(obj);
        this.cache = obj;
    }

    public String getText() {
        return GET_TEXT.invoke(this.handle);
    }

    public ChatModifierWrapper getChatModifier() {
        return new ChatModifierWrapper(GET_CHAT_MODIFIER.invoke(handle));
    }

    public String toLegacyText() {
        if (this.handle == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();

        Iterator iterator = ((Iterable) this.handle).iterator();
        while (iterator.hasNext()) {

            ChatComponentWrapper component = ChatComponentWrapper.fromHandle(iterator.next());
            ChatModifierWrapper modifier = component.getChatModifier();

            if (modifier.getColor() != null) {
                out.append(modifier.getColor());
            } else {
                if (out.length() != 0) {
                    out.append(ChatColor.RESET);
                }
            }

            if (modifier.isBold()) {
                out.append(ChatColor.BOLD);
            }

            if (modifier.isItalic()) {
                out.append(ChatColor.ITALIC);
            }

            if (modifier.isStrikethrough()) {
                out.append(ChatColor.STRIKETHROUGH);
            }

            if (modifier.isUnderlined()) {
                out.append(ChatColor.UNDERLINE);
            }

            if (modifier.isRandom()) {
                out.append(ChatColor.MAGIC);
            }

            out.append(component.getText());
        }

        return out.toString();
    }

    /**
     * Retrieve a deep copy of the current chat component.
     * @return A copy of the current component.
     */
    @Override
    public ChatComponentWrapper clone() {
        return fromJson(getJson());
    }

    @Override
    public String toString() {
        return "WrappedChatComponent[json=" + getJson() + "]";
    }
}
