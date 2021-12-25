package net.drapuria.framework.bukkit.util.option.holder.input;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;


public class ChatHolderOptionInput<V, H, O extends AbstractHolderOption<V, H>> extends AbstractHolderOptionInput<V, H, O> {


    public String parseInput(String str) {
        return str;
    }

    @Override
    public <O1 extends AbstractHolderOption<?, ?>> void startInput(Player player, O1 option) {
        if (!player.isConversing()) {
            endInput(player, false, option);
            return;
        }
        ConversationFactory conversationFactory = new ConversationFactory(Drapuria.PLUGIN);
        final Conversation conversation = conversationFactory.withLocalEcho(false)
                .withPrefix(conversationContext -> option.getDisplayAdapter().getPrefix())
                .addConversationAbandonedListener(event -> {
                    endInput(player, false, option);
                })
                .withEscapeSequence("!cancel")
                .withFirstPrompt(new StringPrompt() {

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return option.getDisplayAdapter().getInputPromptText();
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext conversationContext, String s) {
                        s = parseInput(s);
                        if (option.validateInput(s)) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1L, 1L);
                            option.setValueByPlayer(player, s);
                            return END_OF_CONVERSATION;
                        }
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1L, 1L);
                        conversationContext.getForWhom().sendRawMessage("§c§lFalsche Eingabe!");
                        return this;
                    }
                })
                .withTimeout(2)
                .buildConversation(player);
        conversation.begin();
    }
}
