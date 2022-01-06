package net.drapuria.framework.util;

import java.util.ArrayList;
import java.util.List;

public final class Pageifier<T> {
    private final int pagesize;
    private final List<List<T>> pages;

    public Pageifier(final int pagesize) {
        this.pagesize = pagesize;
        this.pages = new ArrayList<>();
        this.pages.add(new ArrayList<>());
    }

    public void addItem(final T item) {
        final int pageNum = pages.size() - 1;
        List<T> currentPage = this.pages.get(pageNum);
        if (currentPage.size() >= this.pagesize) {
            currentPage = new ArrayList<>();
            this.pages.add(currentPage);
        }
        currentPage.add(item);
    }

    public List<T> getPage(final int pageNum) {
        if (this.pages.size() == 0 || this.pages.get(pageNum) == null)
            return null;
        return this.pages.get(pageNum);
    }

    public List<List<T>> getPages() {
        return this.pages;
    }

    public int getPageSize() {
        return this.pagesize;
    }
}