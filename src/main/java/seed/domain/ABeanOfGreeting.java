package seed.domain;

/**
 * Created by Macsnow on 2017/2/24.
 */

public class ABeanOfGreeting {

    private final long id;
    private final String content;

    public ABeanOfGreeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}

