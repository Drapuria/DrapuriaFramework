package net.drapuria.framework.pageable;

import java.util.List;

public interface HolderPageable<H, I> {

    int getPage(H holder);

    void setPage(H holder, int page);

    int getMaxPage(H holder);

    int getPageSize(H holder);

    void initHolder(H holder, List<I> items);

    void clearHolder(H holder);

    int getDefaultPage();

}
