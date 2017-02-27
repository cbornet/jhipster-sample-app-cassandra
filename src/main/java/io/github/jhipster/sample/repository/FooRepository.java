package io.github.jhipster.sample.repository;

import io.github.jhipster.sample.domain.Foo;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cassandra repository for the Foo entity.
 */
@Repository
public class FooRepository {

    private final Session session;

    private Mapper<Foo> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    public FooRepository(Session session) {
        this.session = session;
        this.mapper = new MappingManager(session).mapper(Foo.class);
        this.findAllStmt = session.prepare("SELECT * FROM foo");
        this.truncateStmt = session.prepare("TRUNCATE foo");
    }

    public List<Foo> findAll() {
        List<Foo> foosList = new ArrayList<>();
        BoundStatement stmt = findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                Foo foo = new Foo();
                foo.setId(row.getUUID("id"));
                foo.setBar(row.getBytes("bar"));
                foo.setBarContentType(row.getString("bar_content_type"));

                return foo;
            }
        ).forEach(foosList::add);
        return foosList;
    }

    public Foo findOne(UUID id) {
        return mapper.get(id);
    }

    public Foo save(Foo foo) {
        if (foo.getId() == null) {
            foo.setId(UUID.randomUUID());
        }
        mapper.save(foo);
        return foo;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt = truncateStmt.bind();
        session.execute(stmt);
    }
}
