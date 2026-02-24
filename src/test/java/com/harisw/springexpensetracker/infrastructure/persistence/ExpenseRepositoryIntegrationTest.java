package com.harisw.springexpensetracker.infrastructure.persistence;

import com.harisw.springexpensetracker.domain.auth.AuthProvider;
import com.harisw.springexpensetracker.domain.auth.Role;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseCategory;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaEntity;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseJpaEntity;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseJpaRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/*
 * =============================================================================
 * INTEGRATION TEST ANATOMY - Read this to understand how it works!
 * =============================================================================
 *
 * ANNOTATIONS EXPLAINED:
 *
 * @DataJpaTest
 *   - Loads ONLY JPA-related beans (repositories, EntityManager, etc.)
 *   - Does NOT load controllers, services, or other beans
 *   - Each test runs in a transaction that ROLLS BACK after the test
 *   - Faster than @SpringBootTest because it loads fewer beans
 *
 * @Testcontainers
 *   - Enables Testcontainers support (manages Docker containers lifecycle)
 *   - Starts containers before tests, stops them after
 *
 * @AutoConfigureTestDatabase(replace = NONE)
 *   - Tells Spring NOT to replace our database with an embedded H2
 *   - We want to use the real PostgreSQL from Testcontainers
 *
 * @Import(ExpenseRepositoryImpl.class)
 *   - @DataJpaTest doesn't scan @Repository classes outside JPA
 *   - We manually import our repository implementation
 *
 * =============================================================================
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ExpenseRepositoryImpl.class)
class ExpenseRepositoryIntegrationTest {

    /*
     * =========================================================================
     * TESTCONTAINERS SETUP
     * =========================================================================
     *
     * @Container - Marks this as a container managed by Testcontainers static -
     * Shared across all tests in this class (faster)
     *
     * The container starts a real PostgreSQL database in Docker. Testcontainers
     * automatically: 1. Pulls the postgres:15 image (if not cached) 2. Starts the
     * container before tests 3. Stops and removes the container after tests
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15").withDatabaseName("testdb")
            .withUsername("test").withPassword("test");
    /*
     * =========================================================================
     * DEPENDENCY INJECTION
     * =========================================================================
     *
     * @Autowired injects the real beans from Spring context. - ExpenseRepository:
     * Our domain repository (ExpenseRepositoryImpl) - ExpenseJpaRepository: Spring
     * Data JPA repository (for test setup)
     */
    @Autowired
    private ExpenseRepository repository; // The one we're testing
    @Autowired
    private ExpenseJpaRepository jpaRepository; // For test data setup
    @Autowired
    private UserJpaRepository userJpaRepository; // For creating test users

    private UserJpaEntity testUser;

    /*
     * =========================================================================
     * DYNAMIC PROPERTIES
     * =========================================================================
     *
     * @DynamicPropertySource - Injects properties at runtime
     *
     * Why? Because Testcontainers assigns a RANDOM port to avoid conflicts. We
     * can't hardcode the port in application.properties. This method runs BEFORE
     * Spring context starts and provides the actual connection details from the
     * running container.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        // Clean database before each test for isolation
        jpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // Create a test user that expenses will belong to
        testUser = new UserJpaEntity();
        testUser.setPublicId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);
        testUser.setPasswordHash("hashed");
        testUser.setAuthProvider(AuthProvider.LOCAL);
        testUser.setCreatedAt(Instant.now());
        testUser = userJpaRepository.save(testUser);
    }

    /*
     * =========================================================================
     * TEST: Save and retrieve
     * =========================================================================
     * Verifies the full round-trip: Domain -> JPA Entity -> Database -> Domain
     */
    @Test
    void save_shouldPersistExpenseAndGenerateId() {
        // given - Create a domain expense (id is null, will be generated)
        Expense expense = new Expense(null, testUser.getId(), UUID.randomUUID(), ExpenseCategory.FOOD,
                "Lunch at restaurant", new Money(new BigDecimal("25.50")), LocalDate.of(2024, 1, 15), Instant.now());

        // when - Save through our repository
        Expense saved = repository.save(expense);

        // then - Verify it was persisted correctly
        assertNotNull(saved.id(), "Database should generate an ID");
        assertEquals(testUser.getId(), saved.userId());
        assertEquals(expense.publicId(), saved.publicId());
        assertEquals(expense.category(), saved.category());
        assertEquals(expense.description(), saved.description());
        assertEquals(0, expense.amount().amount().compareTo(saved.amount().amount()));
        assertEquals(expense.date(), saved.date());

        // Verify it's actually in the database
        assertTrue(jpaRepository.findById(saved.id()).isPresent());
    }

    /*
     * =========================================================================
     * TEST: Find by public ID and user ID
     * =========================================================================
     * Verifies the custom query method works correctly
     */
    @Test
    void findByPublicIdAndUserId_shouldReturnExpenseWhenExists() {
        // given - Insert test data
        UUID publicId = UUID.randomUUID();
        ExpenseJpaEntity entity = createEntity(publicId, "Test expense", "30.00");
        jpaRepository.save(entity);

        // when
        Optional<Expense> result = repository.findByPublicIdAndUserId(publicId, testUser.getId());

        // then
        assertTrue(result.isPresent());
        assertEquals(publicId, result.get().publicId());
        assertEquals("Test expense", result.get().description());
    }

    @Test
    void findByPublicIdAndUserId_shouldReturnEmptyWhenNotExists() {
        // given - Random UUID that doesn't exist
        UUID nonExistentId = UUID.randomUUID();

        // when
        Optional<Expense> result = repository.findByPublicIdAndUserId(nonExistentId, testUser.getId());

        // then
        assertTrue(result.isEmpty());
    }

    /*
     * =========================================================================
     * TEST: Find all
     * =========================================================================
     */
    @Test
    void findAll_shouldReturnAllExpenses() {
        // given - Insert multiple test records
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 1", "10.00"));
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 2", "20.00"));
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 3", "30.00"));

        // when
        List<Expense> result = repository.findAll();

        // then
        assertEquals(3, result.size());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoExpenses() {
        // given - Empty database (cleared in @BeforeEach)

        // when
        List<Expense> result = repository.findAll();

        // then
        assertTrue(result.isEmpty());
    }

    /*
     * =========================================================================
     * TEST: Delete by public ID
     * =========================================================================
     */
    @Test
    void deleteByPublicId_shouldRemoveExpense() {
        // given
        UUID publicId = UUID.randomUUID();
        ExpenseJpaEntity entity = createEntity(publicId, "To be deleted", "50.00");
        jpaRepository.save(entity);

        // Verify it exists first
        assertTrue(jpaRepository.findByPublicId(publicId).isPresent());

        // when
        repository.deleteByPublicId(publicId);

        // then
        assertTrue(jpaRepository.findByPublicId(publicId).isEmpty());
    }

    /*
     * =========================================================================
     * TEST: Mapper correctness
     * =========================================================================
     * Verifies that all fields are correctly mapped between Domain and JPA Entity
     */
    @Test
    void mapper_shouldCorrectlyConvertAllFields() {
        // given - Create expense with specific values
        UUID publicId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2024, 6, 15);
        Instant createdAt = Instant.parse("2024-06-15T10:30:00Z");

        Expense original = new Expense(null, testUser.getId(), publicId, ExpenseCategory.ENTERTAINMENT,
                "Concert tickets", new Money(new BigDecimal("150.00")), date, createdAt);

        // when - Save and retrieve
        Expense saved = repository.save(original);
        Expense retrieved = repository.findByPublicIdAndUserId(publicId, testUser.getId()).orElseThrow();

        // then - All fields should match
        assertEquals(publicId, retrieved.publicId());
        assertEquals(testUser.getId(), retrieved.userId());
        assertEquals(original.category(), retrieved.category());
        assertEquals(original.description(), retrieved.description());
        assertEquals(0, new BigDecimal("150.00").compareTo(retrieved.amount().amount()));
        assertEquals(original.date(), retrieved.date());
        assertEquals(original.createdAt(), retrieved.createdAt());
    }

    /*
     * =========================================================================
     * HELPER METHOD
     * =========================================================================
     * Creates a JPA entity for test setup. Using JPA entity directly in test setup
     * is fine - it bypasses our repository so we can test the repository in
     * isolation.
     */
    private ExpenseJpaEntity createEntity(UUID publicId, String description, String amount) {
        ExpenseJpaEntity entity = new ExpenseJpaEntity();
        entity.setPublicId(publicId);
        entity.setUser(testUser);
        entity.setCategory(ExpenseCategory.OTHER);
        entity.setDescription(description);
        entity.setAmount(new BigDecimal(amount));
        entity.setDate(LocalDate.now());
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}
