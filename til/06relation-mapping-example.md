# 다양한 연관관계 매핑

## 연관관계 매핑 시 고려사항

- 다중성
  - 다대일 : `@ManyToOne`
  - 일대다 : `@OneToMany`
  - 일대일 : `@OneToOne`
  - 다대다 : `@ManyToMany`
- 단방향, 양방향
- 연관관계의 주인

## 다대일 | N:1

### 다대일 단방향

- ```java
  @Getter
  @Setter
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
  @Getter
  @Setter
  @Entity
  public class Team {

      @Id
      @GeneratedValue
      @Column(name = "TEAM_ID")
      private Long id;

      private String name;

  }
  ```

### 다대일 양방향

- 외래 키가 있는 쪽이 연관관계의 주인.
- 양쪽을 서로 참조하도록 개발.

- ```java
  @Getter
  @Setter
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
  @Getter
  @Setter
  @Entity
  public class Team {

      @Id
      @GeneratedValue
      @Column(name = "TEAM_ID")
      private Long id;

      private String name;

      @OneToMany(mappedBy = "team")
      private List<Member> members = new ArrayList<>();

  }
  ```

## 일대다 | 1:N

### 일대다 단방향

- 권장하지 않음. 다대일 양방향을 사용하자.
- 연관관계의 주인이 아닌 테이블에 외래 키가 있어, 추가적인 SQL이 발생한다.
- ```java
  @Getter
  @Setter
  @Entity
  public class Team {

      @Id
      @GeneratedValue
      @Column(name = "TEAM_ID")
      private Long id;

      private String name;

      @OneToMany
      @JoinColumn(name = "TEAM_ID")
      private List<Member> members = new ArrayList<>();

  }
  ```

- ```java
  @Getter
  @Setter
  @Entity
  public class Member {

      @Id
      @GeneratedValue
      private Long id;

      @Column(name = "USERNAME")
      private String name;

  }
  ```

### ~~일대다 양방향~~

- 권장하지 않음. 다대일 양방향을 사용하자.
- 두 엔티티 모두 연관관계의 주인으로 만들고, 한 엔티티를 읽기 전용으로 만드는 방법

- ```java
  @Getter
  @Setter
  @Entity
  public class Team {

      @Id
      @GeneratedValue
      @Column(name = "TEAM_ID")
      private Long id;

      private String name;

      @OneToMany
      @JoinColumn(name = "TEAM_ID")
      private List<Member> members = new ArrayList<>();

  }
  ```

- ```java
  @Getter
  @Setter
  @Entity
  public class Member {

      @Id
      @GeneratedValue
      private Long id;

      @Column(name = "USERNAME")
      private String name;

      @ManyToOne
      @JoinColumn(name = "TEAM_ID", insertable = false, updateable = false)
      private Team team;

  }
  ```

## 일대일 | 1:1

- 사용 방법은 다대일 양방향 매핑과 동일하다. `@ManyToOne`/`@OneToMany`를 `@OneToOne`으로 수정하면 된다.
- 외래 키가 있는 테이블이 반드시 연관관계의 주인이 되어야 한다. 외래 키가 없는 테이블이 주인이 되는 케이스는 JPA에서 지원하지 않는다.

- ```java
  @Getter
  @Setter
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

      @OneToOne
      @JoinColumn(name = "LOCKER_ID")
      private Locker locker;

  }
  ```

- ```java
  @Getter
  @Setter
  @Entity
  public class Locker {

      @Id
      @GeneratedValue
      @Column(name = "LOCKER_ID")
      private Long id;

      private String name;

      @OneToOne(mappedBy = "locker")
      private Member member;

  }
  ```

- 일대일 특성 상, 주 테이블에 외래 키를 둘 수 있고, 대상 테이블에 외래 키를 둘 수 있다. 어디에 두느냐에 따라 trade-off가 존재한다.
  - 주 테이블에 외래키를 둔 케이스
    - ![주 테이블에 외래키를 둔 케이스](./image/06001.png)
    - JPA 매핑이 편리하여, 객체지향 개발자가 선호한다.
    - 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능하다.
    - 단점 : 값이 없을 수 있어, 외래 키에 null을 허용해야 한다.
  - 대상 테이블에 외래키를 둔 케이스
    - ![대상 테이블에 외래키를 둔 케이스](./image/06002.png)
    - 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 쉽다.
    - 단점 : JPA 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩된다.

## ~~다대다 | N:N~~

- 실무에서 사용하면 안된다.
