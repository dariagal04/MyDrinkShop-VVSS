package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService service;
    private Repository<Integer, Product> repo;
    private Product product;

    static class TestResult {
        String testName;
        String type;
        String input;
        String expected;
        String actual;

        TestResult(String testName, String type, String input, String expected, String actual) {
            this.testName = testName;
            this.type = type;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
        }
    }

    static List<TestResult> results = new ArrayList<>();

    static class InMemoryRepo extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }

    @BeforeEach
    void setUp() {
        repo = new InMemoryRepo();
        service = new ProductService(repo);
    }

    @AfterEach
    void tearDown() {
        service = null;
        repo = null;
        product = null;
    }

    // ECP tests
    @Test
    @DisplayName("ECP - valid product 1")
    void addProduct_ValidProduct1() {
        product = new Product(1, "Cafea", 10,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        try {
            service.addProduct(product);
            results.add(new TestResult(
                    "addProduct_ValidProduct1",
                    "ECP",
                    "id=1, name=Cafea, price=10",
                    "No exception",
                    "Passed"
            ));
        } catch (ValidationException e) {
            results.add(new TestResult(
                    "addProduct_ValidProduct1",
                    "ECP",
                    "id=1, name=Cafea, price=10",
                    "No exception",
                    "Failed"
            ));
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("ECP - valid product 2")
    void addProduct_ValidProduct2() {
        product = new Product(2, "Ceai", 8,
                CategorieBautura.TEA,
                TipBautura.BASIC);

        try {
            service.addProduct(product);
            results.add(new TestResult(
                    "addProduct_ValidProduct2",
                    "ECP",
                    "id=2, name=Ceai, price=8",
                    "No exception",
                    "Passed"
            ));
        } catch (ValidationException e) {
            results.add(new TestResult(
                    "addProduct_ValidProduct2",
                    "ECP",
                    "id=2, name=Ceai, price=8",
                    "No exception",
                    "Failed"
            ));
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("ECP - invalid id")
    void addProduct_InvalidId() {
        product = new Product(0, "Cafea", 10,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        assertThrows(ValidationException.class, () -> service.addProduct(product));
        results.add(new TestResult(
                "addProduct_InvalidId",
                "ECP",
                "id=0, name=Cafea, price=10",
                "ValidationException",
                "Passed"
        ));
    }

    @Test
    @DisplayName("ECP - invalid price 0")
    void addProduct_PriceZero() {
        product = new Product(1, "Cafea", 0,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        assertThrows(ValidationException.class, () -> service.addProduct(product));
        results.add(new TestResult(
                "addProduct_PriceZero",
                "ECP",
                "id=1, name=Cafea, price=0",
                "ValidationException",
                "Passed"
        ));
    }

    // BVA tests
    @Test
    @DisplayName("BVA - valid price boundary")
    void addProduct_PriceValid_BVA() {
        product = new Product(1, "Cafea", 0.01,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        try {
            service.addProduct(product);
            results.add(new TestResult(
                    "addProduct_PriceValid_BVA",
                    "BVA",
                    "price=0.01",
                    "No exception",
                    "Passed"
            ));
        } catch (ValidationException e) {
            results.add(new TestResult(
                    "addProduct_PriceValid_BVA",
                    "BVA",
                    "price=0.01",
                    "No exception",
                    "Failed"
            ));
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("BVA - valid id boundary")
    void addProduct_IdValid_BVA() {
        product = new Product(1, "Cafea", 10,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        try {
            service.addProduct(product);
            results.add(new TestResult(
                    "addProduct_IdValid_BVA",
                    "BVA",
                    "id=1",
                    "No exception",
                    "Passed"
            ));
        } catch (ValidationException e) {
            results.add(new TestResult(
                    "addProduct_IdValid_BVA",
                    "BVA",
                    "id=1",
                    "No exception",
                    "Failed"
            ));
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("BVA - invalid ids")
    void addProduct_IdInvalid_BVA(int id) {
        product = new Product(id, "Cafea", 10,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        assertThrows(ValidationException.class, () -> service.addProduct(product));
        results.add(new TestResult(
                "addProduct_IdInvalid_BVA",
                "BVA",
                "id=" + id,
                "ValidationException",
                "Passed"
        ));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.01})
    @DisplayName("BVA - invalid prices")
    void addProduct_PriceInvalid_BVA(double price) {
        product = new Product(1, "Cafea", price,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        assertThrows(ValidationException.class, () -> service.addProduct(product));
        results.add(new TestResult(
                "addProduct_PriceInvalid_BVA",
                "BVA",
                "price=" + price,
                "ValidationException",
                "Passed"
        ));
    }

    @AfterAll
    static void printResults() {
        System.out.println("\n===== TEST RESULTS =====");
        System.out.printf("%-30s %-5s %-25s %-20s %-10s\n", "Test Name", "Type", "Input", "Expected", "Actual");
        for (TestResult r : results) {
            System.out.printf("%-30s %-5s %-25s %-20s %-10s\n",
                    r.testName, r.type, r.input, r.expected, r.actual);
        }
        System.out.println("========================\n");
    }
}