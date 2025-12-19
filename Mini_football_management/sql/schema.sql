CREATE TYPE player_position_enum AS ENUM ('GK', 'DEF', 'MIDF', 'STR');
CREATE TYPE continent_enum AS ENUM ('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');

CREATE TABLE team (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    continent continent_enum NOT NULL
);

CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    position player_position_enum NOT NULL,
    id_team INT,
    CONSTRAINT fk_team
        FOREIGN KEY (id_team)
        REFERENCES team(id)
        ON DELETE SET NULL
);

INSERT INTO team (id, name, continent) VALUES
(1, 'Real Madrid', 'EUROPA'),
(2, 'FC Barcelone', 'EUROPA'),
(3, 'Atletico Madrid', 'EUROPA'),
(5, 'Inter Miami', 'AMERICA');

INSERT INTO player (id, name, age, position, id_team) VALUES
(1, 'Thibaut Courtois', 31, 'GK', 1),
(2, 'Dani Carvajal', 32, 'DEF', 1),
(3, 'Jude Bellingham', 21, 'MIDF', 1);
