package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;

public interface ThreadWatcher {
    void onUpdated(@Nullable ThreadDocument previous, ThreadDocument thread);
}
