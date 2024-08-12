# ARTINUS - Channel Subscription Service



## 1. 설계

### 1.1. 구독 상태 관리

[구독 상태를 State Diagram으로 표현](./docs/subscription-state-diagram.md)

![](./docs/state-diagram.png)





### 1.2. 레이어드 아키텍처(Layered Architecture)

프로젝트는 전형적인 레이어드 아키텍처를 사용하여 설계되었다. 이는 Presentation Layer (Controller), Business Logic Layer (Service), Data Access Layer (Repository)로 구성되어 있다. 각 레이어는 명확히 분리되어 있어 책임이 구분되며, 향후 확장이나 유지보수가 용이하다.



## 2. 구현

### 2.1. 유한 상태 기계 (Finite State Machine)





### 2.2. 다중화 환경에서 동시성 이슈 고려

#### 2.2.1. JPA Persistence Layer (영속성 계층) 에서의 선택지



**1. 비관적 락 (Pessimistic Locking)**

비관적 잠금은 데이터에 접근하는 동안 다른 트랜잭션이 해당 데이터에 접근하지 못하도록 하는 방법이다. JPA에서는 @Lock 애노테이션을 사용하여 비관적 잠금을 설정할 수 있다.

- **PESSIMISTIC_READ**: 데이터를 읽는 동안 다른 트랜잭션이 데이터를 수정할 수 없게 한다.
- **PESSIMISTIC_WRITE**: 데이터를 읽는 동안 다른 트랜잭션이 데이터를 읽거나 수정할 수 없게 한다.



**2. 낙관적 락 (Optimistic Locking)**

낙관적 잠금은 트랜잭션이 커밋될 때만 잠금을 확인하고, 중간에 다른 트랜잭션이 데이터를 변경했는지 확인하여 충돌이 발생하면 롤백하는 방식이다. JPA에서는 `@Version` 애노테이션을 사용하여 낙관적 잠금을 구현할 수 있다.

이 방식에서는 version 필드가 트랜잭션마다 증가하며, 트랜잭션이 완료될 때 다른 트랜잭션이 같은 데이터를 변경했는지 여부를 확인한다. 만약 충돌이 발생하면`OptimisticLockException`이 발생한다.



**3. @Transactional Isolation Level 조정**

Spring의 @Transactional 애노테이션을 사용하여 트랜잭션의 격리 수준(Isolation Level)을 조정할 수 있다. 격리 수준을 높이면 동시성 문제를 줄일 수 있지만, 동시에 성능 저하를 유발할 수 있다.

- **READ_COMMITTED**: 기본 설정. 하나의 트랜잭션이 커밋된 데이터만 읽을 수 있다.
- **REPEATABLE_READ**: 트랜잭션이 시작될 때 한 번 읽은 데이터를 다른 트랜잭션이 수정할 수 없다.
- **SERIALIZABLE**: 가장 엄격한 격리 수준으로, 모든 트랜잭션이 직렬화된 것처럼 동작하여 동시성 문제를 최소화한다.



**4. 데이터베이스 수준에서의 락 (Native SQL Locks)**

데이터베이스의 FOR UPDATE와 같은 네이티브 SQL 문을 사용하여 특정 레코드에 대해 잠금을 적용할 수 있다. 이 방법은 JPA가 제공하는 잠금 메커니즘 외에 추가적인 잠금을 제공할 수 있다.



**5. 분산 락 (Distributed Lock)**

만약 여러 애플리케이션 인스턴스가 동시에 접근할 수 있는 환경이라면, Redis나 Zookeeper와 같은 외부 도구를 이용해 분산 잠금을 적용할 수 있다. 이는 JPA를 넘어선 잠금 전략이지만, 클러스터 환경에서의 동시성 문제를 효과적으로 해결할 수 있다.



#### 2.2.2. State Machine



- **다중화 환경에서 동시성 이슈 고려** : Redis를 이용한 State Machine Persist를 통해 다중화 환경에서 상태를 일관성 있게 관리할 수 있다. 이는 여러 인스턴스에서 동일한 사용자의 상태를 변경할 때 발생할 수 있는 동시성 문제를 해결한다.
- **StateMachine Runtime Persist**: Redis를 사용하여 StateMachine의 상태를 영속화함으로써, 상태 전이 중에 시스템이 재시작되거나 인스턴스가 변경되는 상황에서도 상태를 안전하게 복구할 수 있다.



## 3. 테스트



