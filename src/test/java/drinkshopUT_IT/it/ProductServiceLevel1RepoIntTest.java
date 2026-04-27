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
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceLevel1RepoIntTest {

    private static final String TEST_FILE = "data/products_test.txt";
    private Product product;
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        new java.io.FileWriter(TEST_FILE, false).close();

        productValidator = new ProductValidator();
        productRepo = new FileProductRepository(TEST_FILE);
        product = mock(Product.class);

        productService = new ProductService(productRepo, productValidator);
    }

    @AfterEach
    void tearDown() {
        productService = null;
        productRepo = null;
        productValidator = null;
        product = null;
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepo() {
        when(product.getId()).thenReturn(997);
        when(product.getNume()).thenReturn("Sprite");
        when(product.getPret()).thenReturn(4.5);
        when(product.getCategorie()).thenReturn(CategorieBautura.CLASSIC_COFFEE);
        when(product.getTip()).thenReturn(TipBautura.BASIC);

        int sizeBefore = productRepo.findAll().size();

        try {
            productService.addProduct(product);
        } catch (Exception e) {
            fail("Nu trebuia sa arunce exceptie: " + e.getMessage());
        }
        Assertions.assertEquals(sizeBefore + 1, productRepo.findAll().size());

        verify(product, atLeastOnce()).getId();
        verify(product, atLeastOnce()).getNume();
        verify(product, atLeastOnce()).getPret();
    }

    @Test
    @Order(2)
    void testAddInvalid_IdNegativ_withRealRepo() {
        when(product.getId()).thenReturn(-5);
        when(product.getNume()).thenReturn("Pepsi");
        when(product.getPret()).thenReturn(3.0);

        int sizeBefore = productRepo.findAll().size();

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        Assertions.assertEquals(sizeBefore, productRepo.findAll().size());

        verify(product, times(1)).getId();
    }

    @Test
    @Order(3)
    void testAddInvalid_PretZero_withRealRepo() {
        when(product.getId()).thenReturn(100);
        when(product.getNume()).thenReturn("Apa");
        when(product.getPret()).thenReturn(0.0);

        int sizeBefore = productRepo.findAll().size();

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        Assertions.assertEquals(sizeBefore, productRepo.findAll().size());
        verify(product, atLeastOnce()).getPret();
    }
}