package su.dkzde.awb.fc;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public final class CatalogThread extends Post {

    private long modified;

    @JsonProperty("last_modified")
    public long getModified() {
        return modified;
    }
    public void setModified(long modified) {
        this.modified = modified;
    }
}
