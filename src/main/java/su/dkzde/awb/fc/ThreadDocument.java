package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class ThreadDocument {

    private final String board;
    private final List<Post> posts;

    /**
     * Post numbers are always growing, thus index array is always sorted.
     * Because of that binary search on post number can be used to avoid allocating hash maps.
     */
    private final long[] index;
    private final PostDocument[] docs;
    private final int[] replies;

    ThreadDocument(String board, Thread thread) {

        this.board = board;
        this.posts = thread.getPosts();

        int size = posts.size();
        this.docs = new PostDocument[size];
        this.index = new long[size];
        this.replies = new int[size];

        for (int j = 0; j < size; j++) {
            final int idx = j;

            Post post = posts.get(idx);
            PostDocument document = new PostDocument(board, post);

            docs[idx] = document;
            index[idx] = post.getNumber();
            document.getReplies().forEach(reply -> {
                int p = Arrays.binarySearch(index, 0, idx, reply);
                if (p >= 0) {
                    replies[idx]++;
                }
            });
        }
    }

    public long getNumber() {
        return index[0];
    }

    public @Nullable String getSubject() {
        return docs[0].getSubject();
    }

    public @Nullable String getCommentText() {
        return docs[0].getCommentText();
    }

    public long getLastPostTime() {
        Post post = posts.get(posts.size() - 1);
        return post.getTime();
    }

    public PostDocument getOp() {
        return docs[0];
    }

    public Stream<PostDocument> getPosts() {
        return Stream.of(docs);
    }

    public @Nullable PostDocument getPost(long number) {
        int p = Arrays.binarySearch(index, number);
        if (p < 0) {
            return null;
        } else {
            return docs[p];
        }
    }
}
