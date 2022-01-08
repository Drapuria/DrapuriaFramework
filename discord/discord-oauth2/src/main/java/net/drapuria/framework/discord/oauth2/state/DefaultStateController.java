package net.drapuria.framework.discord.oauth2.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultStateController implements StateController {

    private static final Random RANDOM = new Random();

    private static final String CHARSTRING = "ABCDEFGHIJKLMNQPURSTUVWXYZabcdefghijklmnqppurstuvwxyz0123456789";

    private final Map<String, String> states = new HashMap<>();

    @Override
    public String generateNewState(String redirectUri) {
        final String state = generateRandomState();
        this.states.put(state, redirectUri);
        return null;
    }

    @Override
    public String consumeState(String state) {
        return states.remove(state);
    }

    private static String generateRandomState() {
        return IntStream.range(0, 10).mapToObj(i -> String.valueOf(CHARSTRING.charAt(RANDOM.nextInt(CHARSTRING.length())))).collect(Collectors.joining());
    }

}
