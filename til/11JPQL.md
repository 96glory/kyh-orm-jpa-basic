# JPQL (Java Persistence Query Language)

- JPQL은 엔티티 객체를 대상으로 쿼리하는 객체지향 쿼리 언어다.
- JPQL은 SQL을 추상화하여 특정 데이터베이스의 SQL에 의존하지 않는다.
- JPQL은 결국 SQL로 변환된다.
- JPQL 수행 후 자동으로 영속성 컨텍스트 플러시가 호출된다.

## 기본 문법

- JPQL 문법

```
select_문 :: =
  select_절
  from_절
  [where_절]
  [groupby_절]
  [having_절]
  [orderby_절]
update_문 :: = update_절 [where_절]
delete_문 :: = delete_절 [where_절]
```

- `select m from Member as m where m.age > 18`

  - JPQL 키워드는 대소문자 구분 X (select, from, as, where)
  - 엔티티와 엔티티의 속성은 대소문자 구분 O (Member, age)
    - 엔티티는 테이블 이름과 다르다.
    - 엔티티의 별칭은 필수다. (as는 생략 가능)

- 집합과 정렬
  - `COUNT(m)`, `SUM(m.age)`, `AVG(m.age)`, `MAX(m.age)`, `MIN(m.age)`
  - `GROUP BY`, `HAVING`
  - `ORDER BY`

## 쿼리 API

- 엔티티 매니저의 메서드 `createQuery`를 통해 `JPQL`을 `Query` 객체로 만들 수 있다.

  - `TypeQuery` : 반환 타입이 명확할 때 사용
    - ```java
      TypedQuery<Member> query =
          em.createQuery("SELECT m FROM Member m", Member.class);
      ```
  - `Query` : 반환 타입이 명확하지 않을 때 사용
    - ```java
      Query query =
          em.createQuery("SELECT m.username, m.age FROM Member m");
      ```

- 만들어진 `Query` 객체에는 결과 조회 API가 있다.
  - `query.getResultList()`
    - 결과가 하나 이상일 때, 리스트를 반환한다.
    - 결과가 없으면, 빈 리스트를 반환한다.
  - `query.getSingleResult()`
    - 결과가 정확히 단 하나여야 한다. 이때, 단일 객체를 반환한다.
    - 결과가 없거나 둘 이상일 때, Exception 발생

```java
List<Member> resultList = em.createQuery("select m from Member m", Member.class)
    .getResultList();
```

- 파라미터 바인딩

  - 이름 기준

    - ```java
      String sql = "SELECT m FROM Member m WHERE m.username = :username";

       // ...

      query.setParameter("username", "glory");
      ```

  - 위치 기준

    - ```java
      String sql = "SELECT m FROM Member m WHERE m.username = ?1";

      // ...

      query.setParameter(1, "glory");
      ```

## 프로젝션

- 프로젝션 : SELECT 절에 조회할 대상을 지정하는 것
- 프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입

  - `SELECT m FROM Member m` -> 엔티티 프로젝션
  - `SELECT m.team FROM Member m` -> 엔티티 프로젝션
  - `SELECT m.address FROM Member m` -> 임베디드 타입 프로젝션
  - `SELECT m.username, m.age FROM Member m` -> 스칼라 타입 프로젝션

- 여러 개의 스칼라 타입을 프로젝션 하는 방법

  - `SELECT m.username, m.age FROM Member m`

  1. Query 타입으로 조회

  ```java
  Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
  List resultList = query.getResultList();

  Iterator iterator = resultList.iterator();
  while (iterator.hasNext()) {
      Object[] row = (Object[]) iterator.next();
      String username = (String) row[0];
      Integer age = (Integer) row[1];
  }
  ```

  2. Object[] 타입으로 조회

  ```java
  List<Object[]> resultList =
      em.createQuery("SELECT m.username, m.age FROM Member m").getResuiltList();

  for (Object[] row : resultList) {
      String username = (String) row[0];
      Integer age = (Integer) row[1];
  }
  ```

  3. new 명령어로 조회

  ```java
  // 새 DTO 클래스를 만들어준다.
  // 패키지 명을 포함한 전체 클래스 명을 입력해야 하고, 순서와 타입이 일치하는 생성자를 만들어야 한다.
  List<MemberDTO> resultListWithDTO = em.createQuery(
        "select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
    .getResultList();

  for (MemberDTO member : resultListWithDTO) {
      String username = member.getUsername();
      Integer age = member.getAge();
  }
  ```

## 페이징 API

- `setFirstResult(int startPosition)` : 조회 시작 위치 (0부터 시작)
- `setMaxResults(int maxResult)` : 조회할 데이터 수

```java
String jpql = "select m from Member m order by m.name desc";
List<Member> resultList = em.createQuery(jpql, Member.class)
                            .setFirstResult(10)
                            .setMaxResults(20)
                            .getResultList();
```

## 조인

- 내부 조인 : `SELECT m FROM Member m [INNER] JOIN m.team t`
- 외부 조인 : `SELECT m FROM Member m LEFT [OUTER] JOIN m.team t`
- 세타 조인 : `SELECT count(m) FROM Member m, Team t WHERE m.username = t.name`

### ON 절을 이용한 조인

- 조인 대상 필터링

  - 예시 : 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인

  - ```
    JPQL:
          SELECT m, t FROM Member m JOIN m.team t on t.name = 'A'

    SQL:
          SELECT m.*, t.*
          FROM   Member m
          INNER JOIN Team t
          ON m.TEAM_ID = t.id and t.name='A'
    ```

- 연관관계 없는 엔티티 외부 조인

  - 예시 : 회원의 이름과 팀의 이름이 같은 대상 외부 조인

  - ```
    JPQL:
          SELECT m, t FROM Member m JOIN Team t on m.username = t.name
    SQL:
          SELECT m._, t._
          FROM   Member m
          INNER JOIN Team t
          ON m.username = t.name
    ```

## 서브 쿼리

- 예시 : 나이가 평균보다 많은 회원

  ```
  SELECT m FROM Member m
  WHERE m.age > (SELECT AVG(m2.age) FROM Member m2)
  ```

- 서브 쿼리 함수
  - `[NOT] EXISTS (subquery)`: 서브쿼리에 결과가 존재하면 참
    - `{ALL | ANY | SOME} (subquery)`
      - `ALL` : 모두 만족하면 참
      - `ANY`, `SOME` : 같은 의미, 조건을 하나라도 만족하면 참
  - `[NOT] IN (subquery)` : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

```
# 팀 A 소속인 회원
SELECT m FROM Member m
WHERE EXISTS (SELECT t FROM m.team t WHERE t.name = '팀A')

# 전체 상품 각각의 재고보다 주문량이 많은 주문들
SELECT o FROM Order o
WHERE o.orderAmount > ALL (SELECT p.stockAmount FROM Product p)

# 어떤 팀이든 팀에 소속된 회원
SELECT m FROM Member m
WHERE m.team = ANY (SELECT t FROM Team t)
```

## SQL과 유사한 JPQL 문법

- EXISTS, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, IS NULL
- CONCAT
- SUBSTRING
- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE
- ABS, SQRT, MOD
- SIZE, INDEX
- CASE 문

  ```
  SELECT
    CASE WHEN m.age <= 10 THEN '학생요금'
         WHEN m.age >= 60 THEN '경로요금'
         ELSE '일반요금'
    END
  FROM Member m
  ```

  ```
  SELECT
    CASE t.name
        WHEN '팀A' THEn '인센티브110%'
        WHEN '팀B' THEN '인센티브120%'
        ELSE '인센티브105%'
    END
  FROM Team t
  ```

- COALESCE : 하나씩 조회해서 null이 아니면 반환

  ```
  SELECT coalesce(m.username, '이름 없는 회원') FROM Member m
  ```

- NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환

  ```
  SELECT NULLIF(m.username, '관리자') FROM Member m
  ```

## 사용자 정의 함수 호출

- 하이버네이트는 사용 전 방언에 추가해야 한다.
- 사용할 방언을 상속받아 구현하고, 이를 `persistence.xml`에 명시하여 상속받은 방언을 사용하면 된다.

```java
public class MyH2Dialect extends H2Dialect {

    public MyH2Dialect() {
        registerFunction("group_concat",
            new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }

}
```

```
SELECT function('group_concat', i.name) FROM Item i
```
