package by.bsuir.football.entity.enums;

public enum EventType {
    GOAL {
        @Override
        public String toString() {
            return "Goal";
        }
    },
    RED_CARD {
        @Override
        public String toString() {
            return "Red Card";
        }
    },
    YELLOW_CARD {
        @Override
        public String toString() {
            return "Yellow Card";
        }
    },
    PENALTY {
        @Override
        public String toString() {
            return "Penalty";
        }
    }
}