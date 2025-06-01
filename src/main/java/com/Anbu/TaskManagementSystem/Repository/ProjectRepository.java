package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByAcronym(String acronym);

    boolean existsByAcronym(String acronym);

    boolean existsByProjectName(String projectName);
}
