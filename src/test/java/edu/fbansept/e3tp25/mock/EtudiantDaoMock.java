package edu.fbansept.e3tp25.mock;

import edu.fbansept.e3tp25.dao.EtudiantDao;
import edu.fbansept.e3tp25.model.Etudiant;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EtudiantDaoMock implements EtudiantDao {
    @Override
    public void flush() {

    }

    @Override
    public <S extends Etudiant> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Etudiant> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Etudiant> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Etudiant getOne(Integer integer) {
        return null;
    }

    @Override
    public Etudiant getById(Integer integer) {
        return null;
    }

    @Override
    public Etudiant getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends Etudiant> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Etudiant> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Etudiant> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Etudiant> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Etudiant> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Etudiant> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Etudiant, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Etudiant> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Etudiant> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Etudiant> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<Etudiant> findAll() {
        return List.of();
    }

    @Override
    public List<Etudiant> findAllById(Iterable<Integer> integers) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(Etudiant entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends Etudiant> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Etudiant> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Etudiant> findAll(Pageable pageable) {
        return null;
    }
}
