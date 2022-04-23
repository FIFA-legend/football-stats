package by.bsuir.football.entity.enums;

public enum Tournament {
    UCL {
        @Override
        public String toString() {
            return "UEFA CHAMPIONS LEAGUE";
        }
    },
    EUROPA_LEAGUE {
        @Override
        public String toString() {
            return "UEFA Europe League";
        }
    },
    EPL {
        @Override
        public String toString() {
            return "English Premier League";
        }
    },
    SPANISH_LEAGUE {
        @Override
        public String toString() {
            return "La Liga";
        }
    },
    GERMAN_LEAGUE {
        @Override
        public String toString() {
            return "Bundesliga";
        }
    },
    FRENCH_LEAGUE {
        @Override
        public String toString() {
            return "Ligue 1";
        }
    },
    ITALIAN_LEAGUE {
        @Override
        public String toString() {
            return "Serie A";
        }
    },
    FRIENDLY {
        @Override
        public String toString() {
            return "Friendly Match";
        }
    }
}