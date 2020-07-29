package com.emu.backend.repository;

import com.emu.backend.entities.Messdaten;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface MessdatenRepository extends MongoRepository<Messdaten, String> {
    public Messdaten[] findAllByGeraetId(String geraeteId);
    public List<Messdaten> findAll();
}
