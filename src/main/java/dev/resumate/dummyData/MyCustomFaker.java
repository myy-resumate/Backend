package dev.resumate.dummyData;

import net.datafaker.Faker;

public class MyCustomFaker extends Faker {
    public ResumeFromFile resumeFromFile() {
        return getProvider(ResumeFromFile.class, ResumeFromFile::new, this);
    }
}
