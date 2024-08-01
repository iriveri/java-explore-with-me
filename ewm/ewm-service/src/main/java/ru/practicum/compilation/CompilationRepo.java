package ru.practicum.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.category.Category;


@Repository
public interface CompilationRepo extends JpaRepository<Compilation, Long> {

}
