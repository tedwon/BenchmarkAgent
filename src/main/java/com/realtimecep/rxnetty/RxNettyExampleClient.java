package com.realtimecep.rxnetty;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.functions.Action1;

public final class RxNettyExampleClient {

    static class WordCountAction implements Action1<HttpClientResponse<ByteBuf>> {

        public volatile int wordCount;

        @Override
        public void call(HttpClientResponse<ByteBuf> response) {
            response.getContent().forEach(content -> wordCount = Integer.parseInt(content.toString(Charset.defaultCharset())));
        }
    }

    public static void main(String... args) throws Exception {

        long start = System.currentTimeMillis();

//        WordCountAction wAction = new WordCountAction();
//        RxNetty.createHttpGet("http://localhost:8080/data").forEach(content -> {});

//        RxNetty.createHttpGet("http://localhost:8080/data")
//            .flatMap(response -> response.getContent())
//            .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
//            .toBlocking().forEach(System.out::println);

        RxNetty.createHttpGet("http://localhost:8080/data")
            .map(response -> response.getStatus().code())
            .toBlocking().forEach(System.out::println);

//        PipelineConfigurator<HttpClientResponse<ByteBuf>, HttpClientRequest<String>> pipelineConfigurator
//            = PipelineConfigurators.httpClientConfigurator();
//
//        HttpClient<String, ByteBuf> client = RxNetty.createHttpClient("localhost", 8080, pipelineConfigurator);
//        HttpClientRequest<String> request = HttpClientRequest.create(HttpMethod.POST, "test/post");
////        client.submit(request).toBlocking().forEach(System.out::println);
//        client.submit(request).forEach(System.out::println);

//        RxNetty.createHttpGet("http://localhost:8080/data");

//            .toBlocking()
//            .toFuture()
//            .get(1, TimeUnit.MINUTES);

//        RxNetty.createHttpGet("http://localhost:8080/data")
//            .flatMap(response -> response.getContent())
//            .map(data -> "Client => " + data.toString(Charset.defaultCharset()));
//            .toBlocking().forEach(System.out::println);

//        sendHelloRequest();

        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms");

        Thread.sleep(10000);
    }

    public static String sendHelloRequest() throws InterruptedException, ExecutionException, TimeoutException {
        return RxNetty.createHttpGet("http://localhost:8080/data")
            .flatMap(response -> {
                printResponseHeader(response);
                return response.getContent().<String>map(content -> content.toString(Charset.defaultCharset()));
            })
            .toBlocking()
            .toFuture().get(1, TimeUnit.MINUTES);
    }

    public static void printResponseHeader(HttpClientResponse<ByteBuf> response) {
        System.out.println("New response received.");
        System.out.println("========================");
        System.out.println(response.getHttpVersion().text() + ' ' + response.getStatus().code()
                           + ' ' + response.getStatus().reasonPhrase());
        for (Map.Entry<String, String> header : response.getHeaders().entries()) {
            System.out.println(header.getKey() + ": " + header.getValue());
        }
    }
}