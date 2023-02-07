# 영속성 컨텍스트 및 영속성 관리

## 영속성 컨텍스트 (Persistence Context)

- **엔티티를 영구 저장하는 환경**
  - `EntityManager.persist(entity);`
- 영속성 컨텍스트는 논리적인 개념이다.
- EntityManger를 통해 영속성 컨텍스트에 접근할 수 있다. (`persist`, `detach`, `clear`, ...)
- J2SE 환경에서는 EntityManager와 영속성 컨텍스트가 1:1 관계고, J2EE/Spring과 같은 컨테이너 환경에서는 EntityManager와 영속성 컨텍스트가 N:1 관계다.
- 이점 : 1차 캐시, 동일성(identity) 보장, 트랜잭션을 지원하는 쓰기 지연(transactional write-behind), 변경 감지(dirty checking), 지연 로딩 (lazy loading)

## 엔티티의 생명 주기

- ![Entity Lifecycle](./image/03001.png)
  - 비영속 (New/Transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
  - 영속 (Managed) : 영속성 컨텍스트에 관리되고 있는 상태
  - 준영속 (Detached) : 영속성 컨텍스트에 저장되었다가 분리된 상태
  - 삭제 (Removed) : 삭제된 상태

```java
// 비영속
Member member = new Member(1L, "glory");

// 영속 : 영속성 컨텍스트가 member를 관리하도록 지정
em.persist(member);

// 준영속 : member를 영속성 컨텍스트로부터 분리함
em.detach(member);

// 삭제 : Entity 삭제
em.remove(member);
```
