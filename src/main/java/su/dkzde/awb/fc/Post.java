package su.dkzde.awb.fc;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Post {

    /** Note that this property is always empty for non-op post. */
    private @Nullable String subject;

    private long time;
    private long number;
    private @Nullable String comment;

    /** Attachment identifier: attachment can be accessed via cdn url composed of {@code aid} and extension. */
    private @Nullable Long aid;
    private @Nullable String ext;

    @JsonProperty("sub")
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("no")
    public long getNumber() {
        return number;
    }
    public void setNumber(long number) {
        this.number = number;
    }

    @JsonProperty("com")
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("time")
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    @JsonProperty("tim")
    public Long getAttachmentId() {
        return aid;
    }
    public void setAttachmentId(Long aid) {
        this.aid = aid;
    }

    @JsonProperty("ext")
    public String getAttachmentExtension() {
        return ext;
    }
    public void setAttachmentExtension(String ext) {
        this.ext = ext;
    }
}
