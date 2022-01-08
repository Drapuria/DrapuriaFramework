package net.drapuria.framework.discord.oauth2.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultStateController implements StateController {

    private static final Random RANDOM = new Random();

    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final Map<String, String> states = new HashMap<>();

    @Override
    public String generateNewState(String redirectUri) {
        final String state = generateRandomState();
        this.states.put(state, redirectUri);
        return state;
    }

    @Override
    public String consumeState(String state) {
        return states.remove(state);
    }

    private String generateRandomState() {
        String state;
        do {
            state = IntStream.range(0, 10)
                    .mapToObj(i -> String.valueOf(CHAR_STRING.charAt(RANDOM.nextInt(CHAR_STRING.length()))))
                    .collect(Collectors.joining());
        } while (states.containsKey(state));
        return state;
    }

}
