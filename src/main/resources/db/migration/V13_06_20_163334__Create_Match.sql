CREATE TABLE tennis_match
(
    id      uuid PRIMARY KEY,
    player1 uuid NOT NULL REFERENCES player (id),
    player2 uuid NOT NULL REFERENCES player (id),
    winner  uuid REFERENCES player (id)
);

CREATE TABLE tennis_game
(
    match_id uuid PRIMARY KEY REFERENCES tennis_match (id),
    player1_games int,
    player2_games int
);