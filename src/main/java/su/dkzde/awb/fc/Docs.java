package su.dkzde.awb.fc;

import reactor.core.publisher.Mono;

public final class Docs {
    private Docs() {}

    public static Mono<ThreadDocument> asDocuments(String board, Mono<Thread> source) {
        return source.map(thread -> new ThreadDocument(board, thread));
    }

    public static PostDocument asDocument(String board, Post post) {
        return new PostDocument(board, post);
    }
}
