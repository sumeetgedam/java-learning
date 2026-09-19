package com.learning.core.classes;

import java.util.ArrayList;
import java.util.List;

public class ImmutableTeam {

    private final String name;
    private final List<String> members;

    public ImmutableTeam(
            String name,
            List<String> members
    ) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Team name is required"
            );
        }
        this.name = name;
        this.members = List.copyOf(members);
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public ImmutableTeam addMember(String member) {
        List<String> updatedMembers = new ArrayList<>(members);
        updatedMembers.add(member);
        return new ImmutableTeam(name, updatedMembers);
    }
}
