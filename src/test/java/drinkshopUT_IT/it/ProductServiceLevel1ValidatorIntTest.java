package drinkshopUT_IT.it;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceLevel1ValidatorIntTest {

    private Product product;
    private ProductValidator productValidator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        productValidator = new ProductValidator();
        productRepo = mock(Repository.class);

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
    void testAddValid_withRealValidator() {
        when(product.getId()).thenReturn(1);
        when(product.getNume()).thenReturn("Cola");
        when(product.getPret()).thenReturn(5.0);
        when(productRepo.save(product)).thenReturn(product);

        try {
            productService.addProduct(product);
        } catch (Exception e) {
            fail("Nu trebuia sa arunce exceptie: " + e.getMessage());
        }
        verify(productRepo, times(1)).save(product);
        verify(product, atLeastOnce()).getId();
        verify(product, atLeastOnce()).getNume();
        verify(product, atLeastOnce()).getPret();
    }

    @Test
    @Order(2)
    void testAddInvalid_IdNegativ_withRealValidator() {
        when(product.getId()).thenReturn(-1);
        when(product.getNume()).thenReturn("Cola");
        when(product.getPret()).thenReturn(5.0);

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        verify(productRepo, never()).save(any());
        verify(product, atLeastOnce()).getId();
    }


    @Test
    @Order(3)
    void testAddInvalid_NumeGol_withRealValidator() {
        when(product.getId()).thenReturn(2);
        when(product.getNume()).thenReturn("");
        when(product.getPret()).thenReturn(5.0);

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        verify(productRepo, never()).save(any());
    }

    @Test
    @Order(4)
    void testAddInvalid_PretNegativ_withRealValidator() {
        when(product.getId()).thenReturn(3);
        when(product.getNume()).thenReturn("Fanta");
        when(product.getPret()).thenReturn(-2.0);
        when(product.getCategorie()).thenReturn(CategorieBautura.CLASSIC_COFFEE);
        when(product.getTip()).thenReturn(TipBautura.BASIC);

        Assertions.assertThrows(ValidationException.class, () -> {
            productService.addProduct(product);
        });

        verify(productRepo, never()).save(any());
        verify(product, atLeastOnce()).getPret();
    }
}