import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import jpql.Member;
import jpql.MemberDTO;
import jpql.MemberType;
import jpql.Team;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Team team1 = new Team();
            team1.setName("team1");
            em.persist(team1);

            Team team2 = new Team();
            team2.setName("team2");
            em.persist(team2);

            Member member1 = new Member();
            member1.setUsername("team1");
            member1.setTeam(team1);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(team2);
            em.persist(member2);

            String sql = "select t.members from Team t";

            List resultList = em.createQuery(sql).getResultList();

//            List<Member> resultList = em.createQuery("select m from Member m", Member.class)
//                .getResultList();
//            for (Member result : resultList) {
//                System.out.println("get members with getResultList: member: " + member.getUsername());
//            }

//            List<Object[]> resultListWithScalarType = em.createQuery("select m.username, m.age from Member m")
//                .getResultList();
//
//            Object[] resultWithScalarType = resultListWithScalarType.get(0);
//            System.out.println("username = " + resultWithScalarType[0]);
//            System.out.println("age = " + resultWithScalarType[1]);

//            List<MemberDTO> resultListWithDTO = em.createQuery(
//                    "select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
//                .getResultList();
//
//            for (MemberDTO memberDTO : resultListWithDTO) {
//                System.out.println("username: " + memberDTO.getUsername());
//                System.out.println("age: " + memberDTO.getAge());
//            }
//
//            List<Member> pagingList = em.createQuery("select m from Member m order by m.age desc",
//                    Member.class)
//                .setFirstResult(0)
//                .setMaxResults(10)
//                .getResultList();

//            String query = "select m.username, 'HELLO', TRUE from Member m "
//                + "where m.type = jpql.MemberType.ADMIN";
//            List<Object[]> resultListWithType = em.createQuery(query)
//                .getResultList();

//            String query2 = "select m.username, 'HELLO', TRUE from Member m "
//                + "where m.type = :userType";
//            List<Object[]> resultListWithType2 = em.createQuery(query2)
//                .setParameter("userType", MemberType.ADMIN)
//                .getResultList();
//
//            for (Object[] objects : resultListWithType2) {
//                System.out.println("objects = " + objects[0]);
//                System.out.println("objects = " + objects[1]);
//                System.out.println("objects = " + objects[2]);
//            }

//            String query2 = "select function('group_concat', m.username) FROM Member m";
//            List<String> resultListWithType2 = em.createQuery(query2, String.class)
//                .getResultList();
//            for (String s : resultListWithType2) {
//                System.out.println("ss = " + s);
//            }

//            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
//                .setParameter("username", "member1").getResultList();
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

//            int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();
//            System.out.println("resultCount = " + resultCount);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
