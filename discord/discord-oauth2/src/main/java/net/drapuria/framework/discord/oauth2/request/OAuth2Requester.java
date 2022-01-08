package net.drapuria.framework.discord.oauth2.request;

import lombok.RequiredArgsConstructor;
import okhttp3.*;

import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class OAuth2Requester {

    protected static final String USER_AGENT = "Drapuria-Discord Oauth2(1.0.0)";
    protected static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);

    private final OkHttpClient httpClient;

    <T> void submitAsync(OAuth2Action<T> request, Consumer<T> success, Consumer<Throwable> failure) {
        httpClient.newCall(request.buildRequest()).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    T value = request.handle(response);

                    try {
                        if (value != null)
                            success.accept(value);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } catch (Throwable t) {
                    try {
                        failure.accept(t);
                    } catch (Throwable t1) {
                        t1.printStackTrace();
                    }
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    <T> T submitSync(OAuth2Action<T> request) {
        try (Response response = httpClient.newCall(request.buildRequest()).execute()) {
            T value = request.handle(response);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
