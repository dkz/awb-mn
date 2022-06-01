package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Singleton
public final class ThreadLoader {

    @Inject
    private Api api;

    /** If cacheable rules aren't installed, cache all threads. */
    private @Nullable Predicate<ThreadDocument> cacheable = null;

    private record CacheKey(String board, long thread) {}
    private final ConcurrentHashMap<CacheKey, ThreadDocument> cache = new ConcurrentHashMap<>();

    public void setCacheable(Predicate<ThreadDocument> cacheable) {
        this.cacheable = cacheable;
    }

    private void conditionalUpdate(String board, ThreadDocument document) {
        if (cacheable == null || cacheable.test(document)) {
            CacheKey key = new CacheKey(board, document.getNumber());
            ThreadDocument existing = cache.put(key, document);
        }
    }

    public Mono<ThreadDocument> fetchThread(String board, long number) {
        return Docs.asDocuments(board, api.loadThread(board, number))
                .doOnNext(document -> conditionalUpdate(board, document));
    }
}
