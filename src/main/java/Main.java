import jakarta.persistence.*;
import Entity.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        TypedQuery<Category> categoryTypedQuery = manager.createQuery("select c from Category c", Category.class);
        List<Category> categoryList = categoryTypedQuery.getResultList();

        for (Category category : categoryList) {
            for(int i = 0; i < category.getHierarchyLevel().intValue(); i++){
                System.out.print("- ");
            }
            System.out.println(category);
        }

        manager.close();
        factory.close();
    }
}
