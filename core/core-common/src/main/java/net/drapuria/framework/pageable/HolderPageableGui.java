package net.drapuria.framework.pageable;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <H> page holder
 * @param <G> gui
 * @param <I> item zp nr
 * @param <D> display adapter
 */
public abstract class HolderPageableGui<H, G, I, D> implements HolderPageable<H, I> {

    private final Map<H, Integer> pages = new HashMap<>();
    private final Map<H, List<I>> items = new HashMap<>();

    protected final Map<H, Map<I, D>> cachedDisplayAdapters = new HashMap<>();


    private final G gui;

    protected HolderPageableGui(G gui) {
        this.gui = gui;
    }

    public G getGui() {
        return gui;
    }

    /**
     * @param holder The page holder
     * @param item The item
     * @return Cached Displayadapter of item
     */
    public D getDisplayAdapter(@NotNull H holder, @NotNull I item) {
        D adapter;
        if (!cachedDisplayAdapters.containsKey(holder))
            cachedDisplayAdapters.put(holder, new HashMap<>());
        if (!cachedDisplayAdapters.get(holder).containsKey(item))
            cachedDisplayAdapters.get(holder).put(item, adapter = getDisplay(holder, item));
        else
            adapter = cachedDisplayAdapters.get(holder).get(item);
        return adapter;
    }


    /**
     * @param holder The page holder
     * @param item the item to display
     * @return Displayadapter of item
     */
    public abstract D getDisplay(H holder, @NotNull I item);

    @Override
    public int getPage(H holder) {
        return pages.get(holder);
    }

    @Override
    public void setPage(H holder, int page) {
        this.pages.put(holder, page);
    }

    public void addItem(H holder, I item) {
        this.items.get(holder).add(item);
    }

    public void removeItem(H holder, I item) {
        this.items.get(holder).remove(item);
    }

    public List<I> getItems(H holder) {
        return this.items.get(holder);
    }

    @Override
    public void initHolder(H holder, List<I> items) {
        this.items.put(holder, items);
        this.pages.put(holder, getDefaultPage());
    }

    @Override
    public void clearHolder(H holder) {
        this.items.remove(holder);
        this.pages.remove(holder);
    }
}
