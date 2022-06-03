package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Singleton
public final class ThreadLoader {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLoader.class);

    @Inject
    private Api api;

    /** If cacheable rules aren't installed, cache all threads. */
    private @Nullable Predicate<PostDocument> cacheable = null;
    private @Nullable ThreadWatcher watcher = null;

    private record CacheKey(String board, long thread) {}
    private final ConcurrentHashMap<CacheKey, ThreadDocument> cache = new ConcurrentHashMap<>();

    public void setCacheable(Predicate<PostDocument> cacheable) {
        this.cacheable = cacheable;
    }

    public void setThreadWatcher(ThreadWatcher watcher) {
        this.watcher = watcher;
    }

    private boolean cacheable(PostDocument post) {
        return cacheable == null
            || cacheable.test(post);
    }

    /** @return mono to force fetch the thread contents and load it into cache if cacheable */
    public Mono<ThreadDocument> fetchThread(String board, long number) {
        return Docs.asDocuments(board, api.loadThread(board, number).publishOn(Schedulers.boundedElastic()))
                .doOnNext(document -> conditionalUpdate(board, document))
                .doOnError(throwable -> {
                    logger.error("Loader failed for /{}/ thread {}: {}", board, number, throwable.getMessage());
                })
                .onErrorResume(throwable -> Mono.empty());
    }

    /** @return flux of updated cacheable threads */
    public Flux<ThreadDocument> fetchCatalog(String board) {
        return api.loadCatalog(board)
                .doOnError(throwable -> {
                    logger.error("Loader failed to fetch /{}/ catalog: {}", board, throwable.getMessage());
                })
                .onErrorResume(throwable -> Flux.empty())
                .flatMap(ct -> {
                    if (!cacheable(Docs.asDocument(board, ct))) {
                        return Flux.empty();
                    }
                    ThreadDocument existing = cache.get(new CacheKey(board, ct.getNumber()));
                    if (existing == null) {
                        return fetchThread(board, ct.getNumber());
                    }
                    if (ct.getModified() > existing.getLastPostTime()) {
                        return fetchThread(board, ct.getNumber());
                    }
                    return Flux.empty();
                });
    }

    private void conditionalUpdate(String board, ThreadDocument document) {
        if (cacheable(document.getOp())) {
            CacheKey key = new CacheKey(board, document.getNumber());
            if (watcher != null) {
                watcher.onUpdated(cache.put(key, document), document);
            }
        }
    }
}
