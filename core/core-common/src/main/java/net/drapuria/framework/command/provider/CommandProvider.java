package net.drapuria.framework.command.provider;

import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.parser.CommandTypeParameterParser;
import net.drapuria.framework.command.repository.CommandRepository;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.util.ClasspathScanner;
import net.drapuria.framework.util.TypeAnnotationScanner;

import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class CommandProvider<C extends FrameworkCommand<?>, P extends CommandTypeParameterParser<?, ?>> {

    private final CommandRepository<C> commandRepository;
    private final Class<C> type;
    private final Map<Class<?>, P> commandTypeParameterParser;

    public CommandProvider(CommandRepository<C> commandRepository, Class<C> type) {
        this.commandRepository = commandRepository;
        this.type = type;
        this.commandTypeParameterParser = new HashMap<>();
        registerDefaults();
    }

    public void registerCommand(Object source, C command) {
        this.commandRepository.registerCommand(source, command);
    }

    public void unregisterCommand(C command) {
        this.commandRepository.unregisterCommand(command);
    }

    public void registerTypeParameterParser(P parser) {
        this.commandTypeParameterParser.put(parser.getType(), parser);
    }

    public void unregisterTypeParameterParser(P parser) {
        this.commandTypeParameterParser.remove(parser.getType());
    }

    public abstract void registerDefaults();

    public abstract void shutdown();

    protected List<Class<C>> loadCommandClasses() {
        final CommandService commandService = (CommandService) DrapuriaCommon.BEAN_CONTEXT.getBean(CommandService.class);
        TypeAnnotationScanner annotationScanner = new TypeAnnotationScanner(ClasspathScanner.getCodeSourceOf(this),
                commandService.getCommandAnnotation());

        return annotationScanner.getResult()
                .stream()
                .filter(type::isAssignableFrom)
                .map(aClass -> (Class<C>)aClass)
                .collect(Collectors.toList());
    }

    public List<Class<C>> loadCommandClasses(CodeSource codeSource, String packageName) {
        final CommandService commandService = (CommandService) DrapuriaCommon.BEAN_CONTEXT.getBean(CommandService.class);
        TypeAnnotationScanner annotationScanner = new TypeAnnotationScanner(codeSource,
                packageName,
                commandService.getCommandAnnotation());

        return annotationScanner.getResult()
                .stream()
                .filter(type::isAssignableFrom)
                .map(aClass -> (Class<C>)aClass)
                .collect(Collectors.toList());
    }

}
