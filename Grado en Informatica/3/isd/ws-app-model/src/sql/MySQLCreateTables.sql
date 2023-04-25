-- ----------------------------------------------------------------------------
-- Model
-------------------------------------------------------------------------------
DROP TABLE Inscription;
DROP TABLE Run;

-- --------------------------------- Run ------------------------------------
CREATE TABLE Run (
    runId BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) COLLATE latin1_bin NOT NULL,
    city VARCHAR(255) COLLATE latin1_bin NOT NULL,
    startDate DATETIME NOT NULL,
    creationDate DATETIME NOT NULL,
    description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    price FLOAT NOT NULL,
    maxRunners INTEGER NOT NULL,
    numInscriptions INTEGER NOT NULL,
    CONSTRAINT RunPK PRIMARY KEY(runId),
    CONSTRAINT validPrices CHECK (price >= 0 AND price <= 1000),
    CONSTRAINT validMaxRunners CHECK (maxRunners > 0 AND maxRunners <= 1000000),
    CONSTRAINT validNumInscriptions CHECK (numInscriptions >= 0 AND numInscriptions <= 1000000)) ENGINE = InnoDB;

-- --------------------------------- Inscription ------------------------------------

CREATE TABLE Inscription (
    inscriptionId BIGINT NOT NULL AUTO_INCREMENT,
    runId BIGINT NOT NULL,
    runnerEmail VARCHAR(255) NOT NULL,
    inscriptionDate DATETIME NOT NULL,
    creditCardNumber VARCHAR(16),
    price FLOAT NOT NULL,
    dorsal INTEGER NOT NULL,
    dorsalPicked BIT NOT NULL,
    CONSTRAINT InscriptionPK PRIMARY KEY(inscriptionId),
    CONSTRAINT InscriptionRunIdFK FOREIGN KEY (runId)
        REFERENCES Run(runId) ON DELETE CASCADE,
    CONSTRAINT validDorsal CHECK (dorsal > 0 AND dorsal <= 1000000)) ENGINE = InnoDB;
