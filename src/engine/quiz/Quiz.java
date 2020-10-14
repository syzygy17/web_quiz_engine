package engine.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import engine.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    @Size(min = 2)
    @ElementCollection
    private List<String> options;

    @JsonIgnore
    @ElementCollection
    private List<Integer> answer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizCompletion> quizCompletion = new ArrayList<>();

    public Quiz() {
        this.answer = new ArrayList<>();
    }

    public Quiz(String title, String text, List<Integer> answer, List<String> options) {
        this.title = title;
        this.text = text;
        this.options = options;
        if (options == null) {
            this.answer = new ArrayList<>();
        } else {
            this.answer = List.copyOf(answer);
        }
    }

    public Quiz(Quiz other) {
        this.title = other.title;
        this.text = other.text;
        this.options = List.copyOf(other.options);
        if (options == null) {
            this.answer = new ArrayList<>();
        } else {
            this.answer = List.copyOf(other.answer);
        }
    }

    @JsonIgnore
    public List<Integer> getAnswer() {
        return answer;
    }

    @JsonProperty("answer")
    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<QuizCompletion> getQuizCompletion() {
        return quizCompletion;
    }

    public void setQuizCompletion(List<QuizCompletion> quizCompletion) {
        this.quizCompletion = quizCompletion;
    }
}

