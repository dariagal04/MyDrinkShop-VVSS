package drinkshopUT_IT.ut.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceMockitoTest {

    private Product product;
    private ProductValidator validator;
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        product = mock(Product.class);
        validator = mock(ProductValidator.class);
        productRepo = mock(Repository.class);

        productService = new ProductService(productRepo, validator);
    }

    @AfterEach
    public void tearDown() {
        productService = null;
        productRepo = null;
        validator = null;
        product = null;
    }

    @Test
    @Order(1)
    public void testGetAll() {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);

        when(productRepo.findAll()).thenReturn(Arrays.asList(p1, p2));

        assert 2 == productService.getAllProducts().size();

        verify(productRepo, times(1)).findAll();
        verify(validator, never()).validate(any());
    }

    @Test
    @Order(2)
    public void testAddInvalidProduct() {

        when(product.getId()).thenReturn(-1);

        doThrow(new ValidationException("ID invalid!"))
                .when(validator).validate(product);

        try {
            productService.addProduct(product);
        } catch (Exception e) {
            assert e instanceof ValidationException;
        }

        verify(validator, times(1)).validate(product);
        verify(productRepo, never()).save(any());
    }

    @Test
    @Order(3)
    public void testAddValidProduct() {

        doNothing().when(validator).validate(product);
        when(productRepo.save(product)).thenReturn(product);

        try {
            productService.addProduct(product);
        } catch (Exception e) {
            fail("Nu trebuia sa arunce exceptie");
        }

        verify(validator, times(1)).validate(product);
        verify(productRepo, times(1)).save(product);
    }
}