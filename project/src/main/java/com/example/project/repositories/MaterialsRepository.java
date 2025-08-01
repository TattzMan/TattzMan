package com.example.project.repositories;

import com.example.project.entities.Courses;
import com.example.project.entities.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialsRepository  extends JpaRepository<Materials, Long>{
    static List<Materials> findByCourse(Courses course) {
        return null;
    }

}
