package net.drapuria.framework.module.scanner.data;

import lombok.Data;

@Data
public class MissingDependencyData {

    private final String type;
    private final String name;

}
