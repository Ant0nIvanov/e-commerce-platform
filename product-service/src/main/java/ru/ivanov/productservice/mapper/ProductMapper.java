package ru.ivanov.productservice.mapper;

import org.springframework.stereotype.Service;
import ru.ivanov.productservice.dto.ProductDto;
import ru.ivanov.productservice.dto.request.CreateProductRequest;
import ru.ivanov.productservice.model.Product;
import ru.ivanov.productservice.model.enums.ProductCategory;

@Service
public class ProductMapper {
    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory().russianName,
                product.getPrice()
        );
    }

    public Product toEntity(CreateProductRequest request) {
        return new Product(
          request.name(),
          request.description(),
          ProductCategory.fromName(request.category()),
          request.price()
        );
    }
}
