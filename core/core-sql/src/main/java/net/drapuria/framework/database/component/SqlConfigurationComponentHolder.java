package net.drapuria.framework.database.component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.drapuria.framework.database.SqlService;
import net.drapuria.framework.database.configuration.AbstractSqlConfiguration;
import net.drapuria.framework.database.connection.AbstractConnectionFactory;
import net.drapuria.framework.services.ComponentHolder;

@RequiredArgsConstructor
public class SqlConfigurationComponentHolder extends ComponentHolder {

    private final SqlService service;

    @Override
    public Class<?>[] type() {
        return new Class[]{AbstractSqlConfiguration.class};
    }

    @Override
    @SneakyThrows
    public void onEnable(Object instance) {
        AbstractSqlConfiguration<?> configuration = (AbstractSqlConfiguration) instance;
        if (!configuration.shouldActivate())
            return;
        if (service.getDefaultConfiguration() == null)
            service.setDefaultConfiguration(configuration.getClass());
        AbstractConnectionFactory factory = configuration.factory();
        factory.connect();
        service.addConnectionFactory(configuration.getClass(), factory);
    }

    @Override
    public void onDisable(Object instance) {
        service.shutdownFactory(instance.getClass());
    }
}
