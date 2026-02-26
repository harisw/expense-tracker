package com.harisw.springexpensetracker.infrastructure.persistence;

import com.harisw.springexpensetracker.domain.auth.AuthProvider;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaEntity;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.envelope.EnvelopeJpaEntity;
import com.harisw.springexpensetracker.infrastructure.persistence.envelope.EnvelopeJpaRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseJpaEntity;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseJpaRepository;
import com.harisw.springexpensetracker.infrastructure.persistence.expense.ExpenseRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
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
@Import(ExpenseRepositoryImpl.class)
class ExpenseRepositoryIntegrationTest {

    /*
     * =========================================================================
     * TESTCONTAINERS SETUP
     * =========================================================================
     *
     * @Container - Marks this as a container managed by Testcontainers
     * static    - Shared across all tests in this class (faster)
     *
     * The container starts a real PostgreSQL database in Docker. Testcontainers
     * automatically: 1. Pulls the postgres:15 image (if not cached) 2. Starts
     * the container before tests 3. Stops and removes the container after tests
     */
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    /*
     * =========================================================================
     * DEPENDENCY INJECTION
     * =========================================================================
     *
     * @Autowired injects the real beans from Spring context.
     * - ExpenseRepository:     Our domain repository (ExpenseRepositoryImpl)
     * - ExpenseJpaRepository:  Spring Data JPA repository (for test setup)
     * - EnvelopeJpaRepository: For creating test envelopes (FK dependency)
     * - UserJpaRepository:     For creating test users (FK dependency)
     */
    @Autowired
    private ExpenseRepository repository;
    @Autowired
    private ExpenseJpaRepository jpaRepository;
    @Autowired
    private EnvelopeJpaRepository envelopeJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    private EnvelopeJpaEntity testEnvelope;

    @BeforeEach
    void setUp() {
        // Clean in FK-safe order: expenses -> envelopes -> users
        jpaRepository.deleteAll();
        envelopeJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        UserJpaEntity testUser = new UserJpaEntity();
        testUser.setPublicId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPasswordHash("hashed");
        testUser.setAuthProvider(AuthProvider.LOCAL);
        testUser.setCreatedAt(Instant.now());
        testUser = userJpaRepository.save(testUser);

        testEnvelope = new EnvelopeJpaEntity();
        testEnvelope.setPublicId(UUID.randomUUID());
        testEnvelope.setUser(testUser);
        testEnvelope.setName("Groceries");
        testEnvelope.setTemplate(false);
        testEnvelope.setBudget(new BigDecimal("500.00"));
        testEnvelope.setCanNotify(false);
        testEnvelope.setCreatedAt(Instant.now());
        testEnvelope = envelopeJpaRepository.save(testEnvelope);
    }

    /*
     * =========================================================================
     * TEST: Save and retrieve
     * =========================================================================
     * Verifies the full round-trip: Domain -> JPA Entity -> Database -> Domain
     */
    @Test
    void save_shouldPersistExpenseAndGenerateId() {
        // given
        Expense expense = new Expense(null, testEnvelope.getId(), UUID.randomUUID(),
                "Lunch at restaurant", new Money(new BigDecimal("25.50")), LocalDate.of(2024, 1, 15), Instant.now());

        // when
        Expense saved = repository.save(expense);

        // then
        assertNotNull(saved.id(), "Database should generate an ID");
        assertEquals(testEnvelope.getId(), saved.envelopeId());
        assertEquals(expense.publicId(), saved.publicId());
        assertEquals(expense.description(), saved.description());
        assertEquals(0, expense.amount().amount().compareTo(saved.amount().amount()));
        assertEquals(expense.date(), saved.date());
        assertTrue(jpaRepository.findById(saved.id()).isPresent());
    }

    /*
     * =========================================================================
     * TEST: Find by public ID and envelope ID
     * =========================================================================
     * Verifies the custom query method works correctly
     */
    @Test
    void findByPublicIdAndEnvelopeId_shouldReturnExpenseWhenExists() {
        // given
        UUID publicId = UUID.randomUUID();
        jpaRepository.save(createEntity(publicId, "Test expense", "30.00"));

        // when
        Optional<Expense> result = repository.findByPublicIdAndEnvelopeId(publicId, testEnvelope.getId());

        // then
        assertTrue(result.isPresent());
        assertEquals(publicId, result.get().publicId());
        assertEquals("Test expense", result.get().description());
    }

    @Test
    void findByPublicIdAndEnvelopeId_shouldReturnEmptyWhenNotExists() {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when
        Optional<Expense> result = repository.findByPublicIdAndEnvelopeId(nonExistentId, testEnvelope.getId());

        // then
        assertTrue(result.isEmpty());
    }

    /*
     * =========================================================================
     * TEST: Find all by envelope ID
     * =========================================================================
     */
    @Test
    void findByEnvelopeId_shouldReturnAllExpensesForEnvelope() {
        // given
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 1", "10.00"));
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 2", "20.00"));
        jpaRepository.save(createEntity(UUID.randomUUID(), "Expense 3", "30.00"));

        // when
        List<Expense> result = repository.findByEnvelopeId(testEnvelope.getId());

        // then
        assertEquals(3, result.size());
    }

    @Test
    void findByEnvelopeId_shouldReturnEmptyListWhenNoExpenses() {
        // when
        List<Expense> result = repository.findByEnvelopeId(testEnvelope.getId());

        // then
        assertTrue(result.isEmpty());
    }

    /*
     * =========================================================================
     * TEST: Delete by public ID and user ID
     * =========================================================================
     * Uses a single JPQL DELETE traversing envelope.user.id — no SELECT needed
     */
    @Test
    void deleteByPublicIdAndUserId_shouldReturnTrueAndRemoveExpense() {
        // given
        UUID publicId = UUID.randomUUID();
        jpaRepository.save(createEntity(publicId, "To be deleted", "50.00"));
        assertTrue(jpaRepository.findByPublicId(publicId).isPresent());

        // when
        boolean deleted = repository.deleteByPublicIdAndUserId(publicId, testEnvelope.getUser().getId());

        // then
        assertTrue(deleted);
        assertTrue(jpaRepository.findByPublicId(publicId).isEmpty());
    }

    @Test
    void deleteByPublicIdAndUserId_shouldReturnFalseWhenNotFound() {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when
        boolean deleted = repository.deleteByPublicIdAndUserId(nonExistentId, testEnvelope.getUser().getId());

        // then
        assertFalse(deleted);
    }

    /*
     * =========================================================================
     * TEST: Mapper correctness
     * =========================================================================
     * Verifies that all fields are correctly mapped between Domain and JPA Entity
     */
    @Test
    void mapper_shouldCorrectlyConvertAllFields() {
        // given
        UUID publicId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2024, 6, 15);
        Instant createdAt = Instant.parse("2024-06-15T10:30:00Z");

        Expense original = new Expense(null, testEnvelope.getId(), publicId,
                "Concert tickets", new Money(new BigDecimal("150.00")), date, createdAt);

        // when
        repository.save(original);
        Expense retrieved = repository.findByPublicIdAndEnvelopeId(publicId, testEnvelope.getId()).orElseThrow();

        // then
        assertEquals(publicId, retrieved.publicId());
        assertEquals(testEnvelope.getId(), retrieved.envelopeId());
        assertEquals(original.description(), retrieved.description());
        assertEquals(0, new BigDecimal("150.00").compareTo(retrieved.amount().amount()));
        assertEquals(original.date(), retrieved.date());
        assertEquals(original.createdAt(), retrieved.createdAt());
    }

    /*
     * =========================================================================
     * HELPER METHOD
     * =========================================================================
     * Creates a JPA entity for test setup. Using JPA entity directly in test
     * setup is fine — it bypasses our repository so we can test it in isolation.
     */
    private ExpenseJpaEntity createEntity(UUID publicId, String description, String amount) {
        ExpenseJpaEntity entity = new ExpenseJpaEntity();
        entity.setPublicId(publicId);
        entity.setEnvelope(testEnvelope);
        entity.setDescription(description);
        entity.setAmount(new BigDecimal(amount));
        entity.setDate(LocalDate.now());
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}
