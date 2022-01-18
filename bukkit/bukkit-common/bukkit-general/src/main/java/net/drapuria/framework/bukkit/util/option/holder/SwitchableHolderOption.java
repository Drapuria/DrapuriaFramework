package net.drapuria.framework.bukkit.util.option.holder;

public interface SwitchableHolderOption<T, H> {

    T switchValues(H holder);

    T getSelected(H holder);

}
