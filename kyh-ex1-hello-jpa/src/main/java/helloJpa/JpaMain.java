package helloJpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            System.out.println("========================");
//            Member member = new Member();
//            member.setName("kim");
//            member.setHomeAddress(new Address("homeCity", "street", "10000"));
//
//            em.persist(member);
//            em.flush();
//            em.clear();

            List<Member> result = em.createQuery(
                "SELECT m FROM Member m WHERE m.name like '%kim%'", Member.class).getResultList();

            ///////////
            // Criteria

            // Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            // 루트 클래스 (조회를 시작할 클래스)
            Root<Member> m = query.from(Member.class);

            // 쿼리 생성
            CriteriaQuery<Member> cq =
                query.select(m).where(cb.equal(m.get("username"), "kim"));
            List<Member> resultList = em.createQuery(cq).getResultList();

            for (Member mm : resultList) {
                System.out.println("mm: " + mm.getName());
            }

            // Criteria
            ///////////

            ///////////
            // QueryDSL

            // QueryDSL
            ///////////

            List nativeSqlResultList = em.createNativeQuery(
                    "SELECT ID, NAME FROM MEMBER WHERE NAME = 'kim'", Member.class)
                .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
