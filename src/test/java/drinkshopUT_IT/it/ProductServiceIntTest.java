package drinkshopUT_IT.it;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceIntTest {

    private static final String TEST_FILE = "data/products_test.txt";
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        new java.io.FileWriter(TEST_FILE, false).close();

        productValidator = new ProductValidator();
        productRepo = new FileProductRepository(TEST_FILE);
        productService = new ProductService(productRepo, productValidator);
    }

    @AfterEach
    void tearDown() {
        productService = null;
        productRepo = null;
        productValidator = null;
    }

    @Test
    @Order(1)
    void testAddValid_EverythingReal() {
        Product product = new Product(998, "IceTea", 3.5, CategorieBautura.ICED_COFFEE, TipBautura.BASIC);
        int sizeBefore = productRepo.findAll().size();

        try {
            productService.addProduct(product);
        } catch (Exception e) {
            fail("Nu trebuia sa arunce exceptie: " + e.getMessage());
        }

        Assertions.assertEquals(sizeBefore + 1, productRepo.findAll().size());
        Assertions.assertEquals(sizeBefore + 1, productService.getAllProducts().size());
    }

    @Test
    @Order(2)
    void testAddInvalid_IdNegativ_EverythingReal() {
        Product product = new Product(-1, "Test", 2.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        int sizeBefore = productRepo.findAll().size();

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        Assertions.assertEquals(sizeBefore, productRepo.findAll().size());
    }

    @Test
    @Order(3)
    void testAddInvalid_TotulInvalid_EverythingReal() {
        Product product = new Product(-1, "", -3.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        Assertions.assertTrue(ex.getMessage().contains("ID invalid"));
        Assertions.assertTrue(ex.getMessage().contains("Numele nu poate fi gol"));
        Assertions.assertTrue(ex.getMessage().contains("Pret invalid"));
    }
}