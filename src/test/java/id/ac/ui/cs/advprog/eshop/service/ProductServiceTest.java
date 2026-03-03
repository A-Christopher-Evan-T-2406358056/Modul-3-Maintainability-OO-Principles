package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void testCreateProduct() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        when(productRepository.create(product)).thenReturn(product);

        Product savedProduct = productService.create(product);

        assertEquals(product.getProductId(), savedProduct.getProductId());
        verify(productRepository, times(1)).create(product);
    }

    @Test
    void testFindAllIfEmpty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyIterator());

        List<Product> productList = productService.findAll();

        assertTrue(productList.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");

        Product product2 = new Product();
        product2.setProductId("a0f9de46-90b1-437d-a0bf-d0821dde9096");
        product2.setProductName("Sampo Cap Usep");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2).iterator());

        List<Product> productList = productService.findAll();

        assertEquals(2, productList.size());
        assertEquals(product1.getProductId(), productList.getFirst().getProductId());
        assertEquals(product2.getProductId(), productList.getLast().getProductId());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testEditProductByIdSuccessfully() {
        String id = "some-valid-id";
        Product updatedProduct = new Product();
        updatedProduct.setProductName("test");
        updatedProduct.setProductQuantity(103);

        when(productRepository.update(id, updatedProduct)).thenReturn(Optional.of(updatedProduct));

        assertDoesNotThrow(() -> productService.update(id, updatedProduct));

        verify(productRepository, times(1)).update(id, updatedProduct);
    }

    @Test
    void testEditProductByIdThrowsException() {
        String invalidId = "invalid-id";
        Product productDto = new Product();

        when(productRepository.update(invalidId, productDto)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> productService.update(invalidId, productDto));
        verify(productRepository, times(1)).update(invalidId, productDto);
    }

    @Test
    void testRemoveProductByIdSuccessfully() {
        String id = "some-valid-id";

        doNothing().when(productRepository).delete(id);

        assertDoesNotThrow(() -> productService.deleteProductById(id));

        verify(productRepository, times(1)).delete(id);
    }

    @Test
    void testRemoveProductByIdThrowsException() {
        String invalidId = "invalid-id";

        doThrow(new NoSuchElementException()).when(productRepository).delete(invalidId);

        assertThrows(NoSuchElementException.class, () -> productService.deleteProductById(invalidId));
        verify(productRepository, times(1)).delete(invalidId);
    }

    @Test
    void testFindByIdSuccessfully() {
        String id = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        Product product = new Product();
        product.setProductId(id);
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);

        when(productRepository.findById(id)).thenReturn(product);

        Product foundProduct = productService.findById(id);

        assertNotNull(foundProduct);
        assertEquals(product.getProductId(), foundProduct.getProductId());
        assertEquals(product.getProductName(), foundProduct.getProductName());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testFindByIdThrowsException() {
        String invalidId = "invalid-id";

        when(productRepository.findById(invalidId)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> productService.findById(invalidId));
        verify(productRepository, times(1)).findById(invalidId);
    }
}