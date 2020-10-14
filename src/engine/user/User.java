package engine.user;

import engine.quiz.Quiz;
import engine.quiz.QuizCompletion;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email(regexp = ".+@.+\\..+")
    private String email;
    @NotEmpty
    @Length(min = 5)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuizCompletion> quizCompletions = new ArrayList<>();

    public User() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public long getId() {
        return id;
    }

    public List<QuizCompletion> getQuizCompletions() {
        return quizCompletions;
    }

    public void setQuizCompletions(List<QuizCompletion> quizCompletions) {
        this.quizCompletions = quizCompletions;
    }
}
