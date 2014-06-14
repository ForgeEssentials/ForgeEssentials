package com.forgeessentials.api.questioner;

public enum AnswerEnum {
    YES("yes", true),
    ACCEPT("accept", true),
    ALLOW("accept", true),
    GIVE("accept", true),
    NO("accept", false),
    DECLINE("accept", false),
    DENY("accept", false),
    TAKE("accept", false);

    String name;
    boolean affirmative;

    AnswerEnum(String name, boolean affirmative)
    {
        this.name = name;
    }
}
