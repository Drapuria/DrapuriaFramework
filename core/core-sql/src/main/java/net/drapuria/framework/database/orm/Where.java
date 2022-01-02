package net.drapuria.framework.database.orm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Where {

    private final String property;
    private final Object value;

}
