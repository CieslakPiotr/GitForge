# GitForge

A self-hosted Git hosting platform built with Java 21 and Spring Boot 4: real `git clone` and `git push` over HTTPS, plus repositories, pull requests, code review and issues on top.

## About this project

GitHub-style platforms look simple from the outside, but under the hood they combine a network protocol, a storage engine, a permission model, a merge engine and an event system. GitForge rebuilds that stack end to end to show how those pieces fit together:

- **Standard Git clients work unchanged.** Repositories are real bare Git repos served over the Git smart HTTP protocol via [Eclipse JGit](https://www.eclipse.org/jgit/).
- **Pull requests with three merge strategies** (merge commit, squash, rebase), computed in memory with no working tree.
- **Branch protection** enforced at push time through pre-receive hooks.
- **Reliable events** through a transactional outbox, driving notifications, search indexing and webhooks.
- **A modular monolith** with enforced module boundaries, designed so the Git storage layer can later be split into its own service.

## Features

| Area | Feature | Status |
| --- | --- | --- |
| Git | Clone, fetch, push over HTTPS (personal access tokens) | 🔲 Planned |
| Git | SSH transport (`git@host:owner/repo.git`) | 🔲 Planned |
| Accounts | Users, organizations, teams, personal access tokens | 🔲 Planned |
| Repos | Create, fork, browse tree / blob / commits / blame, compare | 🔲 Planned |
| Access | Per-repo permissions: read, triage, write, maintain, admin | 🔲 Planned |
| Pull requests | Open (incl. cross-fork), review, line comments, merge / squash / rebase | 🔲 Planned |
| Protection | Protected branches, required approvals, no force-push | 🔲 Planned |
| Issues | Issues, labels, comments, `fixes #12` auto-close | 🔲 Planned |
| Platform | Notifications, webhooks, code search | 🔲 Planned |
| UI | Vue 3 + Vuetify single-page app | 🔲 Planned |

## Tech stack

**Backend:** Java 21 (virtual threads), Spring Boot 4, Spring Security, Spring Data JPA, Spring Modulith, Eclipse JGit, Flyway, MapStruct
**Data:** PostgreSQL 17, Redis 7 (locks, cache, rate limits)
**Infrastructure:** Docker, Docker Compose, nginx, GitHub Actions
**Observability:** Micrometer, Prometheus, Grafana, OpenTelemetry
**Testing:** JUnit 5, AssertJ, Mockito, Testcontainers, RestAssured, ArchUnit
**Frontend (planned):** Vue 3, Vuetify 3, TypeScript, Vite, Pinia

## License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.