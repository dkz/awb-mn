package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.uri.UriBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Wraps around json data from {@link Post} class. */
public final class PostDocument {

    private static final Pattern p_reply_link = Pattern.compile("#p(?<post>\\d+)");

    private final String board;
    private final Post post;
    private final @Nullable Document document;

    PostDocument(String board, Post post) {
        this.board = board;
        this.post = post;
        if (post.getComment() != null) {
            this.document = Jsoup.parseBodyFragment(post.getComment());
        } else {
            this.document = null;
        }
    }

    public Stream<Long> getReplies() {
        if (document == null) {
            return Stream.empty();
        } else {
            Stream.Builder<Long> replies = Stream.builder();
            Elements elements = document.select(".quotelink");
            for (Element element : elements) {
                String link = element.attr("href");
                if (link.startsWith("#p")) {
                    Matcher matcher = p_reply_link.matcher(link);
                    if (matcher.matches()) {
                        replies.add(Long.parseLong(matcher.group("post")));
                    }
                }
            }
            return replies.build();
        }
    }

    /** Only applicable to op posts. */
    public @Nullable String getSubject() {
        return post.getSubject();
    }

    public @Nullable String getCommentText() {
        if (document == null) {
            return null;
        } else {
            StringBuilder target = new StringBuilder();
            NodeTraversor.traverse(new PlainTextFormatter(target), document);
            return target.toString();
        }
    }

    public @Nullable String getAttachmentLocation() {
        if (post.getAttachmentId() == null || post.getAttachmentExtension() == null) {
            return null;
        } else return UriBuilder.of("https://i.4cdn.org/{board}/{id}{ext}")
                .expand(Map.of(
                        "board", board,
                        "id", post.getAttachmentId(),
                        "ext", post.getAttachmentExtension()))
                .toString();
    }

    private record PlainTextFormatter(StringBuilder target) implements NodeVisitor {

        @Override
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode t) {
                target.append(t.text());
            } else switch (name) {
                case "p" -> {
                    target.append("\n");
                }
            }
        }

        @Override
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            switch (name) {
                case "br", "p" -> {
                    target.append("\n");
                }
            }
        }
    }
}
