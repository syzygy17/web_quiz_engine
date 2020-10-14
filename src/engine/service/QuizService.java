package engine.service;

import engine.quiz.CompletionRepository;
import engine.quiz.Quiz;
import engine.quiz.QuizCompletion;
import engine.quiz.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    CompletionRepository completionRepository;

    public List<Quiz> getAllQuizzes(int page, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy));

        Page<Quiz> pagedResult = quizRepository.findAll(paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        }

        return new ArrayList<>();
    }

    public List<QuizCompletion> getCompletedQuizzes(int page, int pageSize, String sortBy, long userId) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy));

        Page<QuizCompletion> pagedResult = completionRepository.findAllByUser_IdOrderByCompletedAtDesc(paging, userId);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        }

        return new ArrayList<>();
    }

}
