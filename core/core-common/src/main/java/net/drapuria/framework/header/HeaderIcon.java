package net.drapuria.framework.header;



public abstract class HeaderIcon<H, I> {

    private final H assignedTo;
    private final I icon;


    private final String displayName;
    private final String[] description, focusedDescription, clickDescription;


    private final String viewPermission;

    protected I focusedIcon;
    protected I unfocusedIcon;

    public HeaderIcon(H assignedTo, I icon, String displayName, String[] description, String viewPermission) {
        this(assignedTo, icon, displayName, description, description, getClickDescriptionTemplate, viewPermission);
    }

    public HeaderIcon(H assignedTo, I icon, String displayName, String[] description, String[] focusedDescription, String viewPermission) {
        this(assignedTo, icon, displayName, description, focusedDescription, getClickDescriptionTemplate, viewPermission);
    }

    public HeaderIcon(H assignedTo, I icon, String displayName, String[] description, String[] focusedDescription, String[] clickDescription, String viewPermission) {
        this.icon = icon;
        this.viewPermission = viewPermission;
        this.displayName = displayName;
        this.description = description;
        this.focusedDescription = focusedDescription;
        this.clickDescription = clickDescription;
        this.assignedTo = assignedTo;
        this.focusedIcon = buildFocusedItem();
        this.unfocusedIcon = buildUnfocusedItem();
    }

    public HeaderIcon(H assignedTo, I icon, I focussedIcon, I unfocusedIcon, String displayName, String[] description, String[] focusedDescription, String[] clickDescription, String viewPermission) {
        this.icon = icon;
        this.viewPermission = viewPermission;
        this.displayName = displayName;
        this.description = description;
        this.focusedDescription = focusedDescription;
        this.clickDescription = clickDescription;
        this.assignedTo = assignedTo;
        this.focusedIcon = focussedIcon;
        this.unfocusedIcon = unfocusedIcon;
    }

    public String[] getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getViewPermission() {
        return viewPermission;
    }

    public I getFocusedIcon() {
        return focusedIcon;
    }

    public I getUnfocusedIcon() {
        return unfocusedIcon;
    }

    public String[] getClickDescription() {
        return clickDescription;
    }

    public String[] getFocusedDescription() {
        return focusedDescription;
    }

    public I getIcon() {
        return icon;
    }

    public H getAssignedTo() {
        return assignedTo;
    }

    public I getItem(boolean focused) {
        return focused ? focusedIcon : unfocusedIcon;
    }

    public abstract I buildFocusedItem();

    public abstract I buildUnfocusedItem();

    public static String[] getClickDescriptionTemplate = new String[]{};


}