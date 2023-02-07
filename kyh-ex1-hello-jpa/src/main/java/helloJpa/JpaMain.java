package helloJpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // member는 비영속 상태
            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA");

            // member는 영속 상태 => 101L의 member는 1차 캐시에 저장된다.
            System.out.println("=== BEFORE ===");
            em.persist(member);
            System.out.println("=== AFTER ===");

            // 아래 find할 때 SELECT 쿼리를 수행하지 않고, 1차 캐시의 member를 가져오게 된다.
            Member findMember = em.find(Member.class, 101L);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
