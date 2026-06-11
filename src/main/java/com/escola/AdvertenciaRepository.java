package com.escola;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdvertenciaRepository extends JpaRepository<Advertencia, Long> {
    List<Advertencia> findByAlunoId(Long alunoId);
    List<Advertencia> findByAlunoIdOrderByDataDesc(Long alunoId);
}
