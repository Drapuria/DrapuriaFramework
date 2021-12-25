package net.drapuria.framework.bukkit.util.option.holder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class HolderOptionDisplayAdapter<C extends AbstractHolderOption<?, H>, H> {

    private final C option;
    private final String displayName;
    private final String prefix;
    private final ItemStack displayItem;
    private final String[] description;
    private final String inputPromptText;

    public HolderOptionDisplayAdapter(C option, String displayName, String prefix, ItemStack displayItem, String[] description, String inputPromptText) {
        this.option = option;
        this.prefix = prefix;
        this.displayItem = displayItem;
        this.displayName = displayName;
        this.description = description;
        this.inputPromptText = inputPromptText;
    }

    public abstract ItemStack generateDisplayItem(H requested, Player displayedFor);

    public ItemStack generateDisplayItem(Player displayedFor) {
        return this.generateDisplayItem(this.option.getHolderFromPlayer(displayedFor), displayedFor);
    }
}
