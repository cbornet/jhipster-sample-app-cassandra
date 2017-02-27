package io.github.jhipster.sample.domain;

import com.datastax.driver.mapping.annotations.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

/**
 * A Foo.
 */

@Table(name = "foo")
public class Foo implements Serializable {

    private static final long serialVersionUID = 1L;

    @PartitionKey
    private UUID id;

    private ByteBuffer bar;

    @Column(name = "bar_content_type")
    private String barContentType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ByteBuffer getBar() {
        return bar;
    }

    public Foo bar(ByteBuffer bar) {
        this.bar = bar;
        return this;
    }

    public void setBar(ByteBuffer bar) {
        this.bar = bar;
    }

    public String getBarContentType() {
        return barContentType;
    }

    public Foo barContentType(String barContentType) {
        this.barContentType = barContentType;
        return this;
    }

    public void setBarContentType(String barContentType) {
        this.barContentType = barContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Foo foo = (Foo) o;
        if (foo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, foo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Foo{" +
            "id=" + id +
            ", bar='" + bar + "'" +
            ", barContentType='" + barContentType + "'" +
            '}';
    }
}
