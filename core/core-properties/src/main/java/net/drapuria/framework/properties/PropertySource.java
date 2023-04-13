package net.drapuria.framework.properties;

import java.io.File;

public interface PropertySource {

    PropertyAdapter getAdapter();

    File getFile();

}