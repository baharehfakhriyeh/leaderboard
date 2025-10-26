# Leaderboard
**Leaderboard** is a project provides an in-memory scoring board.
It can be used in other projects as a submodule to manage scoring of any entities with only a few changes.

# Features
- Leaderboard scores get updated with **Apache Kafka** when player score is updated.
- **Concurrency** have already considered. It can be tested in available **integration test** classes.
- Player data is saved in **PostgreSQL** while Leaderboard data is managed in a **concurrent map**.

# Future improvements
- Implement UI with **React**.
