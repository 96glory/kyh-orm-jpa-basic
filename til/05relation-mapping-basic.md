# 연관관계 매핑 기초

- 객체의 참조와 테이블의 외래키를 매핑해야 한다.

## 연관관계가 필요한 이유

- 테이블 구조를 그대로 객체로 구현하면, 객체지향 설계가 어려워진다.
- 테이블 연관관계를 토대로 그대로 객체에 구현하면 아래와 같은 구조가 된다.

  - ![as-is relation](./image/05001.png)
  - ```java
    @Entity
    public class Member {

        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "USERNAME")
        private String name;

        @Column(name = "TEAM_ID")
        private Long teamId;
    }
    ```

  - ```java
    @Entity
    public class Team {

        @Id
        @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;

        private String name;
    }
    ```

  - ```java
    // main 함수
    Team team = new Team();
    team.setName("TeamA");
    em.persist(team);

    Member member = new Member();
    member.setName("member1");
    member.setTeamId(team.getId());
    em.persist(member);

    // member 객체로부터 teamId를 가져와 다시 team 객체를 찾는 것은 객체지향적이지 않다.
    Member findMember = em.find(Member.class, member.etId());
    Long findTeamId = findMember.getTeamId();
    Team findTeam = em.find(Team.class, findTeamId);
    ```

- 결론 : 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.
  - 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다.
  - 객체는 참조를 사용해서 연관된 객체를 찾는다.
  - 위와 같은 차이점을, JPA가 제공하는 방향, 다중성, 연관관계의 주인을 통해 패러다임 불일치를 극복해보자.

## 단방향 연관관계

- 두 엔티티가 연관관계가 있으나 한 엔티티만 다른 엔티티의 참조 필드를 가지고 있으면, 이 연관관계를 단방향 연관관계라고 한다.
- 참조 : 두 엔티티가 서로의 엔티티의 참조 필드를 가지고 있으면, 이 연관관계를 양방향 연관관계라고 한다.

- `@ManyToOne`/`@OneToMany`, `@JoinColumn`을 사용하여 1:N, N:1 관계를 객체로 표현할 수 있다.
- `@ManyToOne` : N:1인 관계에서, N인 엔티티가 1인 엔티티를 참조하고자 하는 필드에 이 애노테이션을 붙인다.
- `@JoinColumn` : 참조 대상이 되는 엔티티의 PK 컬럼명을 명시한다. (property = "name")
- ![to-be relation](./image/05002.png)

  - ```java
    @Entity
    public class Member {

        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "USERNAME")
        private String name;

        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;
    }
    ```

  - ```java
    @Entity
    public class Team {

        @Id
        @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;

        private String name;
    }
    ```

  - ```java
    Team team = new Team();
    team.setName("TeamA");
    em.persist(team);

    Member member = new Member();
    member.setName("member1");
    member.setTeam(team);
    em.persist(member);

    em.flush();
    em.clear();

    // member에서 바로 team을 꺼내 사용할 수 있다.
    Member findMember = em.find(Member.class, member.getId());
    Team findTeam = findMember.getTeam();
    System.out.println("findTeam = " + findTeam.getName());
    ```
