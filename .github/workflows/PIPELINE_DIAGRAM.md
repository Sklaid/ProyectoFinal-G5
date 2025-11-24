# CI/CD Pipeline Architecture Diagram

## Complete Pipeline Flow (Part 1 + Part 2)

```mermaid
graph TB
    Start([Pipeline Trigger<br/>Push/PR/Manual]) --> BuildStage
    
    subgraph BuildStage[Stage 1: Build]
        B1[Job 1: Build Backend<br/>Maven Package]
        B2[Job 2: Build Frontend<br/>npm build]
    end
    
    BuildStage --> TestStage
    
    subgraph TestStage[Stage 2: Test & Analyze]
        T1[Job 3: Unit Tests<br/>Backend + Frontend]
        T2[Job 4: SonarQube<br/>Code Quality]
        T3[Job 5: Security Scan<br/>OWASP + npm audit]
    end
    
    TestStage --> DeployStage
    
    subgraph DeployStage[Stage 3: Deploy Pre-Prod]
        D1[Job 6: Deploy<br/>Docker Compose]
        D2[Health Checks<br/>Backend + Frontend]
    end
    
    DeployStage --> IntegrationStage
    
    subgraph IntegrationStage[Stage 4: Integration Testing]
        I1[Job 7: Integration Tests<br/>Maven verify]
        I2[Job 8: API Tests<br/>Newman/Postman]
    end
    
    IntegrationStage --> PublishStage
    
    subgraph PublishStage[Stage 5: Publish Artifacts]
        P1[Job 9: Nexus<br/>Maven deploy]
        P2[Git Tag<br/>Version + Build]
    end
    
    DeployStage --> E2EStage
    
    subgraph E2EStage[Stage 6: E2E Testing]
        E1[Job 10: Functional Tests<br/>Selenium WebDriver]
        E2[Job 11: Performance Tests<br/>Apache JMeter]
    end
    
    IntegrationStage --> Summary
    PublishStage --> Summary
    E2EStage --> Summary
    
    Summary[Job 12: Pipeline Summary<br/>Status Report]
    
    Summary --> Success{All Critical<br/>Jobs Pass?}
    Success -->|Yes| End([✅ Pipeline Success])
    Success -->|No| Fail([❌ Pipeline Failed])
    
    style Start fill:#e1f5ff
    style End fill:#d4edda
    style Fail fill:#f8d7da
    style BuildStage fill:#fff3cd
    style TestStage fill:#cfe2ff
    style DeployStage fill:#d1ecf1
    style IntegrationStage fill:#e7d4f5
    style PublishStage fill:#d4edda
    style E2EStage fill:#f8d7da
    style Summary fill:#e2e3e5
```

## Job Dependencies

```mermaid
graph LR
    B1[Build Backend] --> T1[Unit Tests]
    B2[Build Frontend] --> T1
    
    T1 --> T2[SonarQube]
    T1 --> T3[Security Scan]
    
    B1 --> D1[Deploy Pre-Prod]
    B2 --> D1
    T1 --> D1
    T2 --> D1
    T3 --> D1
    
    D1 --> I1[Integration Tests]
    D1 --> I2[API Tests]
    D1 --> E1[Functional Tests]
    D1 --> E2[Performance Tests]
    
    I1 --> P1[Publish Artifacts]
    I2 --> P1
    
    B1 --> S[Summary]
    B2 --> S
    T1 --> S
    T2 --> S
    T3 --> S
    D1 --> S
    I1 --> S
    I2 --> S
    P1 --> S
    E1 --> S
    E2 --> S
    
    style B1 fill:#fff3cd
    style B2 fill:#fff3cd
    style T1 fill:#cfe2ff
    style T2 fill:#cfe2ff
    style T3 fill:#cfe2ff
    style D1 fill:#d1ecf1
    style I1 fill:#e7d4f5
    style I2 fill:#e7d4f5
    style P1 fill:#d4edda
    style E1 fill:#f8d7da
    style E2 fill:#f8d7da
    style S fill:#e2e3e5
```

## Parallel Execution Strategy

```mermaid
gantt
    title Pipeline Execution Timeline (Parallel Jobs)
    dateFormat mm:ss
    axisFormat %M:%S
    
    section Build
    Build Backend       :b1, 00:00, 3m
    Build Frontend      :b2, 00:00, 3m
    
    section Test
    Unit Tests          :t1, after b1 b2, 4m
    SonarQube          :t2, after t1, 3m
    Security Scan      :t3, after b1 b2, 4m
    
    section Deploy
    Deploy Pre-Prod    :d1, after t1 t2 t3, 4m
    
    section Integration
    Integration Tests  :i1, after d1, 3m
    API Tests          :i2, after d1, 3m
    
    section E2E
    Functional Tests   :e1, after d1, 8m
    Performance Tests  :e2, after d1, 8m
    
    section Publish
    Publish Artifacts  :p1, after i1 i2, 2m
    
    section Summary
    Pipeline Summary   :s1, after i1 i2 p1 e1 e2, 1m
```

## Branch-Specific Behavior

```mermaid
graph TB
    Trigger{Branch Type?}
    
    Trigger -->|feature/*| Feature[Jobs 1-5 Only<br/>Build + Test + Analyze]
    Trigger -->|develop| Develop[Jobs 1-12<br/>Full Pipeline]
    Trigger -->|release/*| Release[Jobs 1-12<br/>Full Pipeline]
    Trigger -->|main| Main[Jobs 1-12<br/>+ Production Deploy<br/>Task 19]
    
    Feature --> FeatureEnd([Quick Validation<br/>~15 min])
    Develop --> DevelopEnd([Full Validation<br/>~35 min])
    Release --> ReleaseEnd([Release Validation<br/>~35 min])
    Main --> MainEnd([Production Deploy<br/>~50 min])
    
    style Feature fill:#fff3cd
    style Develop fill:#cfe2ff
    style Release fill:#e7d4f5
    style Main fill:#d4edda
```

## Artifact Flow

```mermaid
graph LR
    subgraph Build
        B1[Backend JAR] --> A1[Artifact Storage]
        B2[Frontend Dist] --> A1
    end
    
    subgraph Test
        A1 --> T1[Coverage Reports]
        A1 --> T2[Test Results]
    end
    
    subgraph Deploy
        A1 --> D1[Docker Images]
        D1 --> D2[Running Containers]
    end
    
    subgraph Publish
        A1 --> N1[Nexus Repository]
        N1 --> N2[Versioned Artifacts]
    end
    
    subgraph Reports
        T1 --> R1[GitHub Artifacts]
        T2 --> R1
        D2 --> R2[Test Reports]
        R2 --> R1
    end
    
    style A1 fill:#fff3cd
    style D2 fill:#d1ecf1
    style N2 fill:#d4edda
    style R1 fill:#e2e3e5
```

## Health Check Flow

```mermaid
sequenceDiagram
    participant GHA as GitHub Actions
    participant DC as Docker Compose
    participant PG as PostgreSQL
    participant BE as Backend
    participant FE as Frontend
    
    GHA->>DC: docker-compose up -d postgres
    DC->>PG: Start container
    
    loop Health Check (60s timeout)
        GHA->>PG: pg_isready?
        PG-->>GHA: Ready/Not Ready
    end
    
    GHA->>DC: docker-compose up -d backend
    DC->>BE: Start container
    
    loop Health Check (5min timeout)
        GHA->>BE: GET /actuator/health
        BE-->>GHA: UP/DOWN
    end
    
    GHA->>DC: docker-compose up -d frontend
    DC->>FE: Start container
    
    loop Health Check (75s timeout)
        GHA->>FE: GET /
        FE-->>GHA: 200/Error
    end
    
    GHA->>GHA: All services healthy ✅
```

## Test Execution Flow

```mermaid
graph TB
    Deploy[Pre-Prod Deployed] --> TestGate{Test Type}
    
    TestGate -->|Integration| IT[Integration Tests]
    TestGate -->|API| API[Newman Tests]
    TestGate -->|Functional| FT[Selenium Tests]
    TestGate -->|Performance| PT[JMeter Tests]
    
    IT --> ITR[Test Results]
    API --> APIR[Newman Reports]
    FT --> FTR[Screenshots + Reports]
    PT --> PTR[JMeter Reports]
    
    ITR --> Artifacts[GitHub Artifacts]
    APIR --> Artifacts
    FTR --> Artifacts
    PTR --> Artifacts
    
    Artifacts --> Review[Manual Review]
    
    style Deploy fill:#d1ecf1
    style IT fill:#e7d4f5
    style API fill:#e7d4f5
    style FT fill:#f8d7da
    style PT fill:#f8d7da
    style Artifacts fill:#e2e3e5
```

## Error Handling Flow

```mermaid
graph TB
    Job[Job Execution] --> Check{Success?}
    
    Check -->|Yes| Next[Continue to Next Job]
    Check -->|No| Critical{Critical Job?}
    
    Critical -->|Yes| Fail[❌ Fail Pipeline]
    Critical -->|No| Continue[⚠️ Continue with Warning]
    
    Fail --> Notify[Send Notifications]
    Continue --> CollectArtifacts[Collect Artifacts]
    
    CollectArtifacts --> Summary[Pipeline Summary]
    Notify --> Summary
    
    Summary --> Report[Generate Report]
    
    style Job fill:#cfe2ff
    style Fail fill:#f8d7da
    style Continue fill:#fff3cd
    style Summary fill:#e2e3e5
```

## Critical vs Non-Critical Jobs

### Critical Jobs (Pipeline fails if these fail)
```
✅ Job 1: Build Backend
✅ Job 2: Build Frontend
✅ Job 3: Unit Tests
✅ Job 4: SonarQube Analysis
✅ Job 5: Security Scan
✅ Job 6: Deploy Pre-Prod
```

### Non-Critical Jobs (Pipeline continues with warnings)
```
⚠️ Job 7: Integration Tests
⚠️ Job 8: API Tests
⚠️ Job 9: Publish Artifacts
⚠️ Job 10: Functional Tests
⚠️ Job 11: Performance Tests
```

## Deployment Environments

```mermaid
graph LR
    Code[Source Code] --> Dev[Development<br/>docker-compose.dev.yml]
    Dev --> PreProd[Pre-Production<br/>GitHub Actions]
    PreProd --> Prod[Production<br/>docker-compose.prod.yml<br/>Task 19]
    
    Dev -.->|Manual Testing| DevTest[Local Testing]
    PreProd -.->|Automated Testing| PreProdTest[CI/CD Testing]
    Prod -.->|Monitoring| ProdMonitor[Production Monitoring]
    
    style Dev fill:#fff3cd
    style PreProd fill:#d1ecf1
    style Prod fill:#d4edda
```

## Legend

- 🟨 **Yellow** - Build Stage
- 🟦 **Blue** - Test & Analysis Stage
- 🟩 **Cyan** - Deployment Stage
- 🟪 **Purple** - Integration Testing Stage
- 🟩 **Green** - Artifact Publishing Stage
- 🟥 **Red** - E2E Testing Stage
- ⬜ **Gray** - Summary & Reporting Stage

## Notes

1. **Parallel Execution**: Jobs within the same stage run in parallel when possible
2. **Fail Fast**: Critical jobs stop the pipeline immediately on failure
3. **Continue on Error**: Non-critical test jobs continue to collect all results
4. **Artifact Retention**: All artifacts are kept for 7 days
5. **Health Checks**: All services have retry logic with timeouts
6. **Manual Triggers**: Pipeline can be triggered manually with custom parameters
