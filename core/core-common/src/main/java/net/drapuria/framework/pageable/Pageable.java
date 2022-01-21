/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.pageable;

public interface Pageable {

    /**
     * @return The current page
     */
    int getPage();

    /**
     * @param page The page we want to go to
     */
    void setPage(int page);

    /**
     * @return The amount of pages we have
     */
    int getMaxPages();

    /**
     * @return The size of a page
     */
    int getPageSize();

}
