import jakarta.persistence.*;
import Entity.Category;

import java.util.List;
import java.util.Scanner;

public class CreateCategory {

    public static void main(String[] args) {
        // Введите id родительской категории: 2
        // Введите название новой категории: МЦСТ

        // Процессоры
        // - Intel
        // - AMD
        // - МЦСТ (new)

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ID родительской категории: ");
        int parentId = Integer.parseInt(scanner.nextLine());

        try{
            manager.getTransaction().begin();
            TypedQuery<Category> selectParentCategoryQuery = manager.createQuery("select c from Category c " +
                    "where c.id = ?1", Category.class);
            selectParentCategoryQuery.setParameter(1, parentId);
            Category parentCategory = selectParentCategoryQuery.getSingleResult();

            System.out.print("Введите название новой категории: ");
            String newName = scanner.nextLine();
            Category newCategory = new Category();
            newCategory.setName(newName);
            newCategory.setHierarchyLevel(parentCategory.getHierarchyLevel() + 1);
            newCategory.setLeftKey(parentCategory.getRightKey());
            newCategory.setRightKey(parentCategory.getRightKey() + 1);

            /*TypedQuery<Category> categoriesForUpdateQuery = manager.createQuery("select c from Category c " +
                    "where c.leftKey >= ?1 or c.rightKey >= ?1", Category.class);
            categoriesForUpdateQuery.setParameter(1, parentCategory.getRightKey());
            List<Category> updateCategories = categoriesForUpdateQuery.getResultList();
            for (int i = 0; i < updateCategories.size(); i++) {
                if(updateCategories.get(i).equals(parentCategory)){
                    updateCategories.get(i).setRightKey(parentCategory.getRightKey() + 2);
                    updateCategories.get(i).setRightKey(parentCategory.getRightKey());
                    continue;
                }
                if(updateCategories.get(i).getLeftKey() < parentCategory.getRightKey()
                        && updateCategories.get(i).getRightKey() > parentCategory.getRightKey()){
                    updateCategories.get(i).setRightKey(updateCategories.get(i).getRightKey() + 2);
                    continue;
                }
                updateCategories.get(i).setRightKey(updateCategories.get(i).getRightKey() + 2);
                updateCategories.get(i).setLeftKey(updateCategories.get(i).getLeftKey() + 2);
                manager.persist(updateCategories.get(i));
            }*/

            Query updateCategories = manager.createQuery("update Category c set c.leftKey = c.leftKey + 2, c.rightKey = c.rightKey + 2" +
                    "where c.leftKey > ?1 and c.rightKey > ?1");
            updateCategories.setParameter(1, parentCategory.getRightKey());
            Query updateParent = manager.createQuery("update Category c set c.rightKey = c.rightKey + 2" +
                    "where c.id = ?1 or (c.leftKey < ?2 and c.rightKey > ?2)");
            updateParent.setParameter(1, parentCategory.getId());
            updateParent.setParameter(2, parentCategory.getRightKey());
            updateCategories.executeUpdate();
            updateParent.executeUpdate();
            manager.persist(newCategory);

            manager.getTransaction().commit();
        }catch (NoResultException e){
            System.out.println("Введен неверный ID категории!");
        }catch(Exception e){
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            throw new RuntimeException();
        }

        manager.close();
        factory.close();
    }
}
