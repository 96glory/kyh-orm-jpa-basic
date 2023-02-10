package helloJpa;

import java.util.List;
import java.util.Set;
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
            Member member = new Member();
            member.setName("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("chicken");
            member.getFavoriteFoods().add("sushi");
            member.getFavoriteFoods().add("ramen");

            member.getAddressHistory().add(new AddressEntity(new Address("old1", "street", "10000")));
            member.getAddressHistory().add(new AddressEntity(new Address("old2", "street", "10000")));

            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());

            List<Address> addressHistory = findMember.getAddressHistory();
            for (Address address : addressHistory) {
                System.out.println("address.getCity() = " + address.getCity());
            }

            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            // 값 타입 수정
            Address oldAddress = findMember.getHomeAddress();
            findMember.setHomeAddress(
                new Address("newCity", oldAddress.getStreet(), oldAddress.getZipcode()));

            // 값 타입 컬렉션 수정 (chicken을 pizza로 변경)
            findMember.getFavoriteFoods().remove("chicken");
            findMember.getFavoriteFoods().add("pizza");

            // 임베디드 타입 컬렉션 수정
            findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
            findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
