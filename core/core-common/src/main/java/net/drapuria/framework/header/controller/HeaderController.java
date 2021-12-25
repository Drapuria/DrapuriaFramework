package net.drapuria.framework.header.controller;


/**
 * @param <H> type to control
 */
public abstract class HeaderController<H> {

    /**
     *  type to control
     */
    private H selected;

    /**
     * total list of headers
     */
    private final H[] headers;

    /**
     * @param headers list of headers managed by this controller
     */
    protected HeaderController(H[] headers) {
        this(headers, headers.length == 0 ? null : headers[0]);
    }

    /**
     * @param headers list of headers managed by this controller
     * @param defaultValue the default header
     */
    protected HeaderController(H[] headers, H defaultValue) {
        this.headers = headers;
        this.selected = defaultValue;
    }

    /**
     * @param selected set the selected type
     */
    public void setSelected(H selected) {
        this.selected = selected;
        onChangeSelected(selected);
    }

    /**
     * @return the selected header
     */
    public H getSelected() {
        return selected;
    }

    /**
     * @return possible headers
     */
    public H[] getHeaders() {
        return headers;
    }

    /**
     * fires when we change the selected header
     * @param selected the new selected header
     */
    public abstract void onChangeSelected(H selected);

}
