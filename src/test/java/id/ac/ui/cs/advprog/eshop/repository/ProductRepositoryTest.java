package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;
    Random rng;
    final int seed = 0;

    @BeforeEach
    void setUp() {
        rng = new Random(seed);
    }

    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("a0f9de46-90b1-437d-a0bf-d0821dde9096");
        product2.setProductName("Sampo Cap Usep");
        product2.setProductQuantity(50);
        productRepository.create(product2);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());
        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());
        assertFalse(productIterator.hasNext());
    }

    private Product[] createNDummyProducts(int n) {
        Product[] dummyProducts = new Product[n];
        for (int i = 0; i < n; i++) {
            Product product = new Product();
            product.setProductName("Product" + i);
            product.setProductQuantity(rng.nextInt(0, 1000000));
            dummyProducts[i] = product;
            productRepository.create(product);
        }
        return dummyProducts;
    }

    @Test
    void testFindProductByIdFromMoreThanOneProduct() {
        Product[] product = createNDummyProducts(3);

        assertDoesNotThrow(() -> productRepository.findById(product[0].getProductId()));
        assertDoesNotThrow(() -> productRepository.findById(product[1].getProductId()));
        assertDoesNotThrow(() -> productRepository.findById(product[2].getProductId()));

        assertEquals(product[0], productRepository.findById(product[0].getProductId()));
        assertEquals(product[1], productRepository.findById(product[1].getProductId()));
        assertEquals(product[2], productRepository.findById(product[2].getProductId()));

        Product newProduct = new Product();

        assertThrows(NoSuchElementException.class, () -> productRepository.findById(newProduct.getProductId()));
    }

    @Test
    void testFindProductByIdFromEmptyRepository() {
        Product product = new Product();
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product.getProductId()));
    }

    @Test
    void testRemoveProductByIdFromMoreThanOneProduct() {
        Product[] product = createNDummyProducts(3);

        assertDoesNotThrow(() -> productRepository.delete(product[0].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[0].getProductId()));
        assertDoesNotThrow(() -> productRepository.findById(product[1].getProductId()));
        assertDoesNotThrow(() -> productRepository.findById(product[2].getProductId()));

        assertDoesNotThrow(() -> productRepository.delete(product[1].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[0].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[1].getProductId()));
        assertDoesNotThrow(() -> productRepository.findById(product[2].getProductId()));

        assertDoesNotThrow(() -> productRepository.delete(product[2].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[0].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[1].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(product[2].getProductId()));

        assertThrows(NoSuchElementException.class, () -> productRepository.delete(product[0].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.delete(product[1].getProductId()));
        assertThrows(NoSuchElementException.class, () -> productRepository.delete(product[2].getProductId()));
    }

    @Test
    void testRemoveProductByIdFromEmptyRepository() {
        Product product = new Product();
        assertThrows(NoSuchElementException.class, () -> productRepository.delete(product.getProductId()));
    }

    @Test
    void testUpdateProductSuccessfully() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        updatedProduct.setProductName("Sampo Cap Bango");
        updatedProduct.setProductQuantity(200);

        Optional<Product> result = productRepository.update(product.getProductId(), updatedProduct);

        assertTrue(result.isPresent());
        assertEquals("Sampo Cap Bango", result.get().getProductName());
        assertEquals(200, result.get().getProductQuantity());

        Product savedProduct = productRepository.findById(product.getProductId());
        assertEquals("Sampo Cap Bango", savedProduct.getProductName());
        assertEquals(200, savedProduct.getProductQuantity());
    }

    @Test
    void testUpdateProductNotFound() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("invalid-id");
        updatedProduct.setProductName("Sampo Cap Bango");
        updatedProduct.setProductQuantity(200);

        Optional<Product> result = productRepository.update(updatedProduct.getProductId(), updatedProduct);

        assertFalse(result.isPresent());
    }
}