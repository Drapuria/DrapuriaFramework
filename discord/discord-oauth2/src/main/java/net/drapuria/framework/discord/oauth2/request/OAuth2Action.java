/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.request;

import lombok.RequiredArgsConstructor;
import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.entity.impl.OAuth2ClientImpl;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.function.Consumer;


@RequiredArgsConstructor
public abstract class OAuth2Action<T> {

    private static final Consumer DEFAULT_SUCCESS = t -> {};
    private static final Consumer<Throwable> DEFAULT_FAILURE = t -> {
        System.out.println("Request error!");
    };

    private final OAuth2ClientImpl client;
    private final RequestMethod method;
    private final String url;

    protected RequestBody getBody() {
        return OAuth2Requester.EMPTY_BODY;
    }

    protected Request buildRequest() {
        Request.Builder builder = new Request.Builder();

        switch (method) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(getBody());
                break;
            case PUT:
                builder.put(getBody());
                break;
        }
        return builder
                .url(url)
                .header("User-Agent", OAuth2Requester.USER_AGENT)
                .headers(getHeaders()).build();
    }

    protected Headers getHeaders()
    {
        return Headers.of();
    }

    public void queue() {
        queue(DEFAULT_SUCCESS);
    }

    public void queue(Consumer<T> successConsumer) {
        queue(successConsumer, DEFAULT_FAILURE);
    }

    public void queue(Consumer<T> success, Consumer<Throwable> failure)
    {
        client.getRequester().submitAsync(this, success, failure);
    }
    public T complete() throws IOException
    {
        System.out.println("completing..");
        return client.getRequester().submitSync(this);
    }

    public OAuth2Client getClient()
    {
        return client;
    }

    protected abstract T handle(Response response) throws IOException, Exception;

}
