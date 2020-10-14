package engine.controllers;

import engine.quiz.*;
import engine.service.QuizService;
import engine.user.User;
import engine.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class QuizController {

    private final String NO_USER_FOUND = "No user found";
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final CompletionRepository completionRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizRepository quizRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, QuizService quizService,
                          CompletionRepository completionRepository) {
        this.quizRepository = quizRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.quizService = quizService;
        this.completionRepository = completionRepository;
    }

    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz, Principal principal) {
        User findUser = userRepository.findByEmail(principal.getName());
        if (findUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_USER_FOUND);
        }
        quiz.setUser(findUser);
        quizRepository.save(quiz);
        return quiz;
    }

    @GetMapping(value = "/api/quizzes/{id}")
    public Quiz getQuizById(@PathVariable int id) {
        Optional<Quiz> findQuiz = quizRepository.findById(id);
        if (findQuiz.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return findQuiz.get();
    }

    @GetMapping(value = "/api/quizzes")
    public AllQuizzes<Quiz> getAllQuizes(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(defaultValue = "id") String sortBy) {
        List<Quiz> list = quizService.getAllQuizzes(page, pageSize, sortBy);
        return new AllQuizzes<>(list);
    }

    @GetMapping(value = "/api/quizzes/completed")
    public AllQuizzes<QuizCompletion> getAllCompletedQuizes(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(defaultValue = "id") String sortBy,
                                                            Principal principal) {

        User findUser = userRepository.findByEmail(principal.getName());
        if (findUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_USER_FOUND);
        }

        List<QuizCompletion> list = quizService.getCompletedQuizzes(page, pageSize, sortBy, findUser.getId());
        return new AllQuizzes<>(list);
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public QuizResult solveQuiz(@PathVariable int id, @RequestBody QuizAnswer quizAnswer, Principal principal) {

        Optional<Quiz> findQuiz = quizRepository.findById(id);
        if (findQuiz.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found.");
        }

        User findUser = userRepository.findByEmail(principal.getName());
        if (findUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_USER_FOUND);
        }

        List<Integer> rightAnswers = findQuiz.get().getAnswer();
        if (rightAnswers.size() == quizAnswer.getAnswer().size()
                && rightAnswers.containsAll(quizAnswer.getAnswer())) {
            var quizCompletion = getQuizCompletion(findUser);
            quizCompletion.setQuiz(findQuiz.get());
            completionRepository.save(quizCompletion);
            quizRepository.save(findQuiz.get());
            return new QuizResult(true, "Right!");
        }

        return new QuizResult(false, "Wrong.");
    }

    private QuizCompletion getQuizCompletion(User findUser) {
        QuizCompletion quizCompletion = new QuizCompletion();
        quizCompletion.setUser(findUser);
        quizCompletion.setCompletedAt(LocalDateTime.now());
        return quizCompletion;
    }

    @PostMapping(path = "/api/register")
    public void registerUser(@Valid @RequestBody User user) {
        User existedUser = userRepository.findByEmail(user.getEmail());
        if (existedUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already registered");
        }


        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/api/quizzes/{id}")
    public void deleteQuiz(@PathVariable int id, Principal principal) {
        User findUser = userRepository.findByEmail(principal.getName());
        if (findUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_USER_FOUND);
        }

        Optional<Quiz> findQuiz = quizRepository.findById(id);
        if (findQuiz.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found.");
        }

        if (!findQuiz.get().getUser().equals(findUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        quizRepository.delete(findQuiz.get());
    }
}

