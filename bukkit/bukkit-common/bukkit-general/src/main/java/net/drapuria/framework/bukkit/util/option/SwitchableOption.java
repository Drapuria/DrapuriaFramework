package net.drapuria.framework.bukkit.util.option;

public interface SwitchableOption<T> {

    T switchValues();

    T getSelected();

}
