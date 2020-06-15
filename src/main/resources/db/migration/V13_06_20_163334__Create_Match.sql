CREATE TABLE tennis_match
(
    id      uuid PRIMARY KEY,
    player1 uuid NOT NULL REFERENCES player (id),
    player2 uuid NOT NULL REFERENCES player (id),
    winner  uuid REFERENCES player (id)
);

CREATE TABLE tennis_set
(
    match_id      uuid REFERENCES tennis_match (id),
    set           int NOT NULL,
    player1_games int NOT NULL,
    player2_games int NOT NULL
);