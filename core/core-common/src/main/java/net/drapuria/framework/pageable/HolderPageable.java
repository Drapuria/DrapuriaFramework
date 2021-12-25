package net.drapuria.framework.pageable;

public interface HolderPageable<H> {

    int getPage(H holder);

    void setPage(H holder, int page);

    int getMaxPage(H holder);

    int getPageSize(H holder);

}
