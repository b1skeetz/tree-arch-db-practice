import Entity.Category;
import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;

public class FindTree {

    public static void main(String[] args) {
        // Введите название категории: Процессоры

        // Произвести поиск категории по введённому названию и вывести её и её вложенные категории.

        // Процессоры
        // - Intel
        // - AMD

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите название категории: ");
        String categoryName = scanner.nextLine();

        try{
            TypedQuery<Category> ifCategoryExistsQuery = manager.createQuery("select c from Category c " +
                    "where c.category_name = ?1", Category.class);
            ifCategoryExistsQuery.setParameter(1, categoryName);
            Category selectedCategory = ifCategoryExistsQuery.getSingleResult();

            TypedQuery<Category> innerCategoriesQuery = manager.createQuery("select c from Category c " +
                    "where c.leftKey > ?1 and c.rightKey < ?2", Category.class);
            innerCategoriesQuery.setParameter(1, selectedCategory.getLeftKey());
            innerCategoriesQuery.setParameter(2, selectedCategory.getRightKey());
            List<Category> innerCategories = innerCategoriesQuery.getResultList();

            System.out.println(categoryName);
            for (Category innerCategory : innerCategories) {
                System.out.println("- " + innerCategory);
            }

        }catch (NoResultException e){
            System.out.println("Неправильно введено название категории!");
        }

        manager.close();
        factory.close();
    }
}
