package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository {
    private List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        productData.add(product);
        return product;
    }

    public Product findById(String id) {
        return productData.stream()
                .filter(p -> p.getProductId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Optional<Product> update(String id, Product product) {
        for (int i = 0; i < productData.size(); i++) {
            if (productData.get(i).getProductId().equals(id)) {
                productData.set(i, product);
                return Optional.of(productData.get(i));
            }
        }
        return Optional.empty();
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    public void delete(String id) {
        boolean removed = productData.removeIf(p -> p.getProductId().equals(id));
        if (!removed) {
            throw new NoSuchElementException();
        }
    }

}
