package su.dkzde.awb.fc;

import io.micronaut.core.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public final class ThreadDocument {

    private final String board;
    private final List<Post> posts;

    /**
     * Post numbers are always growing, thus index array is always sorted.
     * Because of that binary search on post number can be used to avoid allocating hash maps.
     */
    private final long[] index;
    private final PostDocument[] docs;

    ThreadDocument(String board, Thread thread) {

        this.board = board;
        this.posts = thread.getPosts();

        int replies = posts.size();
        this.docs = new PostDocument[replies];
        this.index = new long[replies];

        for (int j = 0; j < replies; j++) {
            Post post = posts.get(j);
            index[j] = post.getNumber();
            docs[j] = new PostDocument(board, post);
        }
    }

    public PostDocument getOp() {
        return docs[0];
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

    public @Nullable PostDocument getPost(long number) {
        int p = Arrays.binarySearch(index, number);
        if (p < 0) {
            return null;
        } else {
            return docs[p];
        }
    }
}
