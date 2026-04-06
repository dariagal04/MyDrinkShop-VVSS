package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StocServiceWBTTest {

    private StocService stocService;

    static class InMemoryStocRepo extends AbstractRepository<Integer, Stoc> {
        @Override
        protected Integer getId(Stoc entity) { return entity.getId(); }
    }

    @BeforeEach
    void setUp() {
        InMemoryStocRepo repo = new InMemoryStocRepo();
        repo.save(new Stoc(1, "lapte",  10, 1));
        repo.save(new Stoc(2, "banane",  5, 1));
        repo.save(new Stoc(3, "apa",     2, 1));
        repo.save(new Stoc(4, "sare",    1, 1));

        stocService = new StocService(repo);
    }

    // F02_TC01
    @Test
    @DisplayName("F02_TC01 - reteta null → IllegalArgumentException")
    void TC01_reteta_null() {
        assertThrows(IllegalArgumentException.class,
                () -> stocService.consuma(null));
    }

    // F02_TC02
    @Test
    @DisplayName("F02_TC02 - reteta cu lista goala → IllegalArgumentException")
    void TC02_reteta_ingrediente_goale() {
        Reteta reteta = new Reteta(1, new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> stocService.consuma(reteta));
    }

    // F02_TC03
    // Stoc insuficient: apa=2 dar avem nevoie de 5, sare=1 dar avem nevoie de 3
    @Test
    @DisplayName("F02_TC03 - stoc insuficient → IllegalStateException")
    void TC03_stoc_insuficient() {
        List<IngredientReteta> ingrediente = List.of(
                new IngredientReteta("apa",  5.0),   // disponibil 2 < necesar 5
                new IngredientReteta("sare", 3.0)    // disponibil 1 < necesar 3
        );
        Reteta reteta = new Reteta(2, ingrediente);

        assertThrows(IllegalStateException.class,
                () -> stocService.consuma(reteta));
    }

    // F02_TC04
    // stoc suficient: lapte=10, necesar=3
    @Test
    @DisplayName("F02_TC04 - 1 ingredient, stoc suficient → stoc actualizat")
    void TC04_un_ingredient_stoc_suficient() {
        List<IngredientReteta> ingrediente = List.of(
                new IngredientReteta("lapte", 3.0)
        );
        Reteta reteta = new Reteta(3, ingrediente);

        assertDoesNotThrow(() -> stocService.consuma(reteta));

        // Verificam ca stocul s-a redus: 10 - 3 = 7
        double stocRamas = stocService.getAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("lapte"))
                .mapToDouble(Stoc::getCantitate)
                .sum();
        assertEquals(7.0, stocRamas, 0.001);
    }

    // F02_TC05
    // stoc suficient: lapte=10 necesar=2, banane=5 necesar=2
    @Test
    @DisplayName("F02_TC05 - 2 ingrediente, stoc suficient → stoc actualizat")
    void TC05_doua_ingrediente_stoc_suficient() {
        List<IngredientReteta> ingrediente = List.of(
                new IngredientReteta("lapte",  2.0),
                new IngredientReteta("banane", 2.0)
        );
        Reteta reteta = new Reteta(4, ingrediente);

        assertDoesNotThrow(() -> stocService.consuma(reteta));

        double stocLapte = stocService.getAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("lapte"))
                .mapToDouble(Stoc::getCantitate).sum();
        double stocBanane = stocService.getAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("banane"))
                .mapToDouble(Stoc::getCantitate).sum();

        assertEquals(8.0, stocLapte,  0.001);
        assertEquals(3.0, stocBanane, 0.001);
    }
}