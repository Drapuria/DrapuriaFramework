package net.drapuria.framework.bukkit.fake.entity.npc.modifier;

import com.comphenix.protocol.PacketType;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import org.jetbrains.annotations.NotNull;

public class NPCAnimationModifier extends FakeEntityModifier<NPC> {
    public NPCAnimationModifier(@NotNull NPC fakeEntity) {
        super(fakeEntity);
    }


    @NotNull
    public NPCAnimationModifier queue(@NotNull EntityAnimation entityAnimation) {
        return this.queue(entityAnimation.id);
    }

    @NotNull
    public NPCAnimationModifier queue(int animationId) {
        super.newContainer(PacketType.Play.Server.ANIMATION).getIntegers().write(1, animationId);
        return this;
    }


    public enum EntityAnimation {
        /**
         * Swings the main hand (hitting).
         */
        SWING_MAIN_ARM(0),
        /**
         * The damage effect.
         */
        TAKE_DAMAGE(1),
        /**
         * When a player enters a bed.
         */
        LEAVE_BED(2),
        /**
         * Swings the off hand (1.13+).
         */
        SWING_OFF_HAND(3),
        /**
         * When a player takes a critical effect.
         */
        CRITICAL_EFFECT(4),
        /**
         * When a player takes a critical effect caused by magic.
         */
        MAGIC_CRITICAL_EFFECT(5);

        /**
         * The id of the effect.
         */
        private final int id;

        EntityAnimation(int id) {
            this.id = id;
        }
    }

}
