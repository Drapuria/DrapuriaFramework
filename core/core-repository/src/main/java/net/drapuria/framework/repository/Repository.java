package net.drapuria.framework.repository;

import net.drapuria.framework.beans.annotation.DisallowAnnotation;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.ServiceDependency;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@DisallowAnnotation(value = PreInitialize.class)
@ServiceDependency(dependencies = {"mongodb", "sql"})
public interface Repository<T, ID extends Serializable> { }
