```mermaid
---
title : 구독 상태
---
stateDiagram-v2
    [*] --> NONE
    NONE --> REGULAR: 구독 안함 → 일반 구독
    NONE --> PREMIUM: 구독 안함 → 프리미엄 구독
    REGULAR --> PREMIUM: 일반 구독 → 프리미엄 구독
    PREMIUM --> REGULAR: 프리미엄 구독 → 일반 구독
    PREMIUM --> NONE: 프리미엄 구독 → 구독 안함
    REGULAR --> NONE: 일반 구독 → 구독 안함

```
