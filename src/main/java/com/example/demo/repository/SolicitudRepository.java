package com.example.demo.repository;

import com.example.demo.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEstado(Solicitud.Estado estado);

    boolean existsByClienteId(Long clienteId);
}
