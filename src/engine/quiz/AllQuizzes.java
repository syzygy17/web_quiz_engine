package engine.quiz;

import java.util.List;

public class AllQuizzes<T> {
    List<T> content;

    public AllQuizzes(List<T> content) {
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
