package iliker.entity;

public enum  ActionSwitch {
    JOIN {
        @Override
        public String getName() {
            return "参与活动";
        }
    },
    EXIT {
        @Override
        public String getName() {
            return "退出活动";
        }
    };
    public abstract String getName();
    }
