package net.drapuria.framework.bukkit.impl;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.MessageShowType;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import net.drapuria.framework.bukkit.sound.SoundData;
import net.drapuria.framework.language.LanguageService;
import net.drapuria.framework.language.message.AbstractLocalizedMessage;
import net.drapuria.framework.language.message.placeholder.PlaceholderTransformer;
import net.drapuria.framework.language.message.placeholder.PlaceholderValue;
import net.drapuria.framework.language.message.placeholder.SimplePlaceholderValue;
import net.drapuria.framework.language.message.placeholder.TranslateFormat;
import net.drapuria.framework.language.message.prefix.PrefixData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public class LocalizedMessage extends AbstractLocalizedMessage<Player, ChatColor, MessageShowType, SoundData> {

    private static final Function<Player, Locale> DEFAULT_LOCALE_FUNCTION = player -> {
        if (player instanceof DrapuriaPlayer)
            return ((DrapuriaPlayer) player).getLocalization();
        final Optional<DrapuriaPlayer> optional = PlayerRepository.getRepository.findById(player.getUniqueId());
        if (optional.isPresent())
            return optional.get().getLocalization();
        return LanguageService.getService.getDefaultLocale();
    };

    private int titleFadeIn = 1, titleShow = 5, titleFadeOut = 1;
    private String broadcastPermission = null;

    public LocalizedMessage(String key) {
        super(key);
        super.localeGetFunction(DEFAULT_LOCALE_FUNCTION);
        super.showType = MessageShowType.CHAT;
        super.color = ChatColor.RESET;
    }

    @Override
    public void send(Player receiver) {
        final Locale locale = super.getLocaleOf(receiver);
        final PrefixData<?> prefixData = super.prefixData();
        final String message = (prefixData != null ? prefixData.getAsString(locale) : "") + super.color + " " + this.getMessage(locale);
        switch (showType()) {
            case ACTION_BAR:
                Drapuria.IMPLEMENTATION.sendActionBar(receiver, message);
                break;
            case TITLE:
                Drapuria.IMPLEMENTATION.sendTitle(receiver, message, titleFadeIn, titleShow, titleFadeOut);
                break;
            case SUBTITLE:
                Drapuria.IMPLEMENTATION.sendSubTitle(receiver, message, titleFadeIn, titleShow, titleFadeOut);
                break;
            default:
                receiver.sendMessage(message);
                break;
        }
        if (super.sound() != null)
            super.sound().play(receiver);
    }

    @Override
    public void send(Player... receiver) {
        for (Player player : receiver) {
            this.send(player);
        }
    }

    @Override
    public void send(Collection<Player> receiver) {
        for (Player player : receiver) {
            this.send(player);
        }
    }


    public LocalizedMessage titleFadeIn(final int titleFadeIn) {
        this.titleFadeIn = titleFadeIn;
        return this;
    }

    public LocalizedMessage titleShow(final int titleShow) {
        this.titleShow = titleShow;
        return this;
    }

    public LocalizedMessage titleFadeOut(final int titleFadeOut) {
        this.titleFadeOut = titleFadeOut;
        return this;
    }

    public LocalizedMessage broadcastPermission(final String broadcastPermission) {
        this.broadcastPermission = broadcastPermission;
        return this;
    }

    public LocalizedMessage prefixData(PrefixData<?> prefixData) {
        super.prefixData = prefixData;
        return this;
    }

    public LocalizedMessage color(ChatColor color) {
        super.color = color;
        return this;
    }

    public LocalizedMessage showType(MessageShowType showType) {
        super.showType = showType;
        return this;
    }

    public LocalizedMessage sound(SoundData sound) {
        super.sound = sound;
        return this;
    }

    public LocalizedMessage locale(Locale locale) {
        super.locale = locale;
        return this;
    }

    public LocalizedMessage localeGetFunction(Function<Player, Locale> localeGetFunction) {
        super.localeGetFunction = localeGetFunction;
        return this;
    }

    public LocalizedMessage translateFormat(TranslateFormat translateFormat) {
        super.translateFormat = translateFormat;
        return this;
    }

    public LocalizedMessage placeholder(final String value) {
        super.placeholderValues.add(new SimplePlaceholderValue(value));
        return this;
    }

    public LocalizedMessage placeholder(final String placeholder, final String value) {
        super.placeholderValues.add(new SimplePlaceholderValue(placeholder, value));
        return this;
    }

    public LocalizedMessage placeholder(final PlaceholderValue<?> placeholder) {
        this.placeholderValues.add(placeholder);
        return this;
    }

    public <O> LocalizedMessage placeholder(final O object, final PlaceholderTransformer<O> transformer) {
        return this.placeholder(PlaceholderValue.of(object, transformer));
    }

    public <O> LocalizedMessage placeholder(final String placeholder, final O object, final PlaceholderTransformer<O> transformer) {
        return this.placeholder(PlaceholderValue.of(placeholder, object, transformer));
    }

    @Override
    public void broadcast() {
        for (Player receiver : Bukkit.getOnlinePlayers()) {
            if (broadcastPermission == null || receiver.hasPermission(broadcastPermission)) {
                send(receiver);
            }
        }
    }

    public static LocalizedMessage of(final String key) {
        return new LocalizedMessage(key);
    }
}