package net.drapuria.framework.jackson.libraries.relocate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Relocate {

    private String pattern;
    private String shadedPattern;

}
