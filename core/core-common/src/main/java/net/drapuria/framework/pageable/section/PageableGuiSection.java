package net.drapuria.framework.pageable.section;

import net.drapuria.framework.pageable.Pageable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <I> items to be pageified
 * @param <G> for the gui we work in
 * @param <D> display adapter of item (for example ItemStack, Button)
 */
public abstract class PageableGuiSection<I, G, D> implements Pageable {

    private int page;
    private final G gui;
    private final List<I> items = new ArrayList<>();

    protected final Map<I, D> cachedDisplayAdapters = new HashMap<>();
    protected boolean emptyIfNoItem = false;

    /**
     * @param gui the gui we are working in
     */
    protected PageableGuiSection(@NotNull G gui) {
        this.gui = gui;
    }

    /**
     * @return the gui we are working in
     */
    public G getGui() {
        return gui;
    }

    /**
     * @param item The item we want to display
     * @return The (cached) display adapter for this item
     */
    public D getDisplayAdapter(@NotNull I item) {
        D adapter;
        if (!cachedDisplayAdapters.containsKey(item))
            cachedDisplayAdapters.put(item, adapter = getDisplay(item));
        else
            adapter = cachedDisplayAdapters.get(item);
        return adapter;
    }


    /**
     * @param item The item we want to displayitem The item we want to display
     * @return The display adapter we build
     */
    public abstract D getDisplay(@NotNull I item);

    /**
     * @param item The item we want to add
     */
    public void addItem(@NotNull I item) {
        items.add(item);
    }

    /**
     * @param items A list of items we want to add
     */
    public void addItems(@NotNull List<I> items) {
        this.items.addAll(items);
    }

    /**
     * @param index Position of the item in the collection
     * @return The item on this position
     */
    public I get(final int index) {
        return this.items.get(index);
    }

    /**
     * @return The collection of items
     */
    public List<I> getItems() {
        return items;
    }

    /**
     * @return A collection of all the items on current page
     */
    public List<I> getCurrentItems() {
        final List<I> items = new ArrayList<>();
        int index = getPageSize() * this.getPage();
        for (int i = 0; i < getPageSize(); i++) {
            if (this.items.size() <= index)
                break;
            items.add(this.items.get(index));
            index++;
        }
        return items;
    }

    /**
     * Clears the items and cache
     */
    public void clear() {
        items.clear();
        cachedDisplayAdapters.clear();
    }


    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getMaxPages() {
        return this.items.size() / getPageSize();
    }

    @Override
    public int getPageSize() {
        return 7;
    }

    /**
     * @param emptyIfNoItem If set to false we´ll add placeholder items
     */
    public void setEmptyIfNoItem(boolean emptyIfNoItem) {
        this.emptyIfNoItem = emptyIfNoItem;
    }
}
