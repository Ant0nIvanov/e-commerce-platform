package ru.ivanov.productservice.model.enums;


public enum ProductCategory {
    ACCESSORIES("Аксессуары"),
    ELECTRONICS("Электроника и гаджеты"),
    FOOD("Продукты питания"),
    HOME("Дом и сад"),
    BEAUTY("Красота и уход"),
    TOYS("Детские товары и игрушки"),
    SPORTS("Спорт и активный отдых"),
    PETS("Товары для животных"),
    KITCHEN("Кухонные принадлежности"),
    AUTO("Автотовары"),
    HOBBY("Хобби и творчество");

    public final String russianName;

    ProductCategory(String russianName) {
        this.russianName = russianName;
    }

    public static ProductCategory fromName(String name) {
        for(ProductCategory category : ProductCategory.values()) {
            if (category.russianName.equals(name)) {
                return category;
            }
        }
        // todo
        throw new IllegalArgumentException("no category find by name %s".formatted(name));
    }
}
