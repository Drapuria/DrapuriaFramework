package net.drapuria.framework.bukkit.util.option;


import org.bukkit.inventory.ItemStack;

public abstract class DisplayAdapter<O extends OptionContext<?, ?, ?>> {

    private O optionContext;
    private String displayName;
    private ItemStack displayItem;
    private String[] description;

    public DisplayAdapter(O optionContext, String displayName, ItemStack displayItem, String[] description) {
        this.optionContext = optionContext;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.description = description;
    }

    public O getOptionContext() {
        return optionContext;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public abstract ItemStack generateDisplayItem();
}
