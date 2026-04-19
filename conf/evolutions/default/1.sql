# --- !Ups

CREATE TABLE beers (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  style      VARCHAR(100) NOT NULL,
  brewery    VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE judgings (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  beer_id    BIGINT NOT NULL,
  judge_name VARCHAR(100) NOT NULL,
  aroma      INT NOT NULL,
  appearance INT NOT NULL,
  flavor     INT NOT NULL,
  mouthfeel  INT NOT NULL,
  overall    INT NOT NULL,
  notes      VARCHAR(1000),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_beer FOREIGN KEY (beer_id) REFERENCES beers(id) ON DELETE CASCADE,
  CONSTRAINT uq_judge_beer UNIQUE (beer_id, judge_name)
);

# --- !Downs

DROP TABLE IF EXISTS judgings;
DROP TABLE IF EXISTS beers;