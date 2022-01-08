package net.drapuria.framework.discord.oauth2.state;

public interface StateController {

    String generateNewState(String redirectUri);

    String consumeState(String state);

}
