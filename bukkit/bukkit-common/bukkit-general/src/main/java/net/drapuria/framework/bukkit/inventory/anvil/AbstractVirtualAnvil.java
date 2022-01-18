package net.drapuria.framework.bukkit.inventory.anvil;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.util.MethodAnnotationScanner;
import net.drapuria.framework.util.Utility;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractVirtualAnvil {

    private ConfirmAction confirmAction = null;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static Class<AbstractVirtualAnvil> load() {

        MethodAnnotationScanner methodScanner = new MethodAnnotationScanner(AnvilImpl.class);
        Map.Entry<Class<?>, Method[]> possibleMethods = methodScanner.getResults().entrySet()
                .stream().findFirst().orElse(null);
        if (possibleMethods == null) {
            DrapuriaCommon.PLATFORM.getLogger().warn("No implementation for IVirtualAnvil found!");
            return null;
        }
        if (!Utility.getSuperAndInterfaces(possibleMethods.getKey()).contains(AbstractVirtualAnvil.class)) {
            DrapuriaCommon.PLATFORM.getLogger().warn("No implementation for IVirtualAnvil found!");
            return null;
        }
        Method enableMethod = Arrays.stream(possibleMethods.getValue()).findFirst().orElse(null);
        if (enableMethod == null) {
            DrapuriaCommon.PLATFORM.getLogger().warn("There is no method annotated with @AnvilImpl in the IVirtualAnvil implementation!");
            return null;
        }
        enableMethod.invoke(null);
        return (Class<AbstractVirtualAnvil>) possibleMethods.getKey();
    }

    public Inventory getAnvilInventory() {
        return null;
    }

    public ConfirmAction getConfirmAction() {
        return confirmAction;
    }

    public void setConfirmAction(ConfirmAction confirmAction) {
        this.confirmAction = confirmAction;
    }

    public void openAnvil() {

    }

    public abstract void onConfirm(String text);

    public abstract void onCancel();



}
