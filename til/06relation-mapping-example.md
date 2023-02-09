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

      @Setter(AccessLevel.NONE)
      @ManyToOne
      @JoinColumn(name = "TEAM_ID", insertable = false, updateable = false)
      private Team team;

  }
  ```

## 일대일 | 1:1

## 다대다 | N:N
