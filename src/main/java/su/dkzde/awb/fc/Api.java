package su.dkzde.awb.fc;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Singleton
public final class Api {

    @Inject
    @Client("https://a.4cdn.org/")
    private HttpClient client;

    public Mono<List<CatalogPage>> loadCatalogPages(String board) {
        return Mono.from(client.retrieve(
                HttpRequest.GET(UriBuilder.of("/{board}/catalog.json")
                        .expand(Map.of("board", board))
                        .toString()),
                Argument.listOf(CatalogPage.class)));
    }

    public Mono<Thread> loadThread(String board, long number) {
        return Mono.from(client.retrieve(
                HttpRequest.GET(UriBuilder.of("/{board}/thread/{number}.json")
                        .expand(Map.of("board", board, "number", number))
                        .toString()),
                Thread.class));
    }

    public Flux<CatalogThread> loadCatalog(String board) {
        return loadCatalogPages(board)
                .flatMapMany(Flux::fromIterable)
                .flatMap(page -> Flux.fromIterable(page.getThreads()));
    }
}
