package io.github.jhipster.sample.web.rest;

import io.github.jhipster.sample.AbstractCassandraTest;
import io.github.jhipster.sample.JhipsterCassandraSampleApplicationApp;

import io.github.jhipster.sample.domain.Foo;
import io.github.jhipster.sample.repository.FooRepository;
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FooResource REST controller.
 *
 * @see FooResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterCassandraSampleApplicationApp.class)
public class FooResourceIntTest extends AbstractCassandraTest {

    private static final ByteBuffer DEFAULT_BAR = ByteBuffer.wrap(TestUtil.createByteArray(1, "0"));
    private static final ByteBuffer UPDATED_BAR = ByteBuffer.wrap(TestUtil.createByteArray(2, "1"));
    private static final String DEFAULT_BAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BAR_CONTENT_TYPE = "image/png";

    @Autowired
    private FooRepository fooRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restFooMockMvc;

    private Foo foo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            FooResource fooResource = new FooResource(fooRepository);
        this.restFooMockMvc = MockMvcBuilders.standaloneSetup(fooResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createEntity() {
        Foo foo = new Foo()
                .bar(DEFAULT_BAR)
                .barContentType(DEFAULT_BAR_CONTENT_TYPE);
        return foo;
    }

    @Before
    public void initTest() {
        fooRepository.deleteAll();
        foo = createEntity();
    }

    @Test
    public void createFoo() throws Exception {
        int databaseSizeBeforeCreate = fooRepository.findAll().size();

        // Create the Foo

        restFooMockMvc.perform(post("/api/foos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isCreated());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate + 1);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getBar()).isEqualTo(DEFAULT_BAR);
        assertThat(testFoo.getBarContentType()).isEqualTo(DEFAULT_BAR_CONTENT_TYPE);
    }

    @Test
    public void createFooWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fooRepository.findAll().size();

        // Create the Foo with an existing ID
        Foo existingFoo = new Foo();
        existingFoo.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFooMockMvc.perform(post("/api/foos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingFoo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllFoos() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        // Get all the fooList
        restFooMockMvc.perform(get("/api/foos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(foo.getId().toString())))
            .andExpect(jsonPath("$.[*].barContentType").value(hasItem(DEFAULT_BAR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].bar").value(hasItem(Base64Utils.encodeToString(DEFAULT_BAR.array()))));
    }

    @Test
    public void getFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        // Get the foo
        restFooMockMvc.perform(get("/api/foos/{id}", foo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(foo.getId().toString()))
            .andExpect(jsonPath("$.barContentType").value(DEFAULT_BAR_CONTENT_TYPE))
            .andExpect(jsonPath("$.bar").value(Base64Utils.encodeToString(DEFAULT_BAR.array())));
    }

    @Test
    public void getNonExistingFoo() throws Exception {
        // Get the foo
        restFooMockMvc.perform(get("/api/foos/{id}", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo
        Foo updatedFoo = fooRepository.findOne(foo.getId());
        updatedFoo
                .bar(UPDATED_BAR)
                .barContentType(UPDATED_BAR_CONTENT_TYPE);

        restFooMockMvc.perform(put("/api/foos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFoo)))
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getBar()).isEqualTo(UPDATED_BAR);
        assertThat(testFoo.getBarContentType()).isEqualTo(UPDATED_BAR_CONTENT_TYPE);
    }

    @Test
    public void updateNonExistingFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Create the Foo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFooMockMvc.perform(put("/api/foos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isCreated());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);
        int databaseSizeBeforeDelete = fooRepository.findAll().size();

        // Get the foo
        restFooMockMvc.perform(delete("/api/foos/{id}", foo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Foo.class);
    }
}
