package su.dkzde.awb.fc;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public final class CatalogPage {

    private @JsonProperty int page;
    private @JsonProperty List<CatalogThread> threads;

    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return page;
    }

    public void setThreads(List<CatalogThread> threads) {
        this.threads = threads;
    }
    public List<CatalogThread> getThreads() {
        return threads;
    }
}
