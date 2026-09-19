package com.learning.core.testing;

import com.learning.core.classes.ImmutableTeam;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmutableTeamTest {

    @Test
    void shouldProtectAgainstInputListMutation() {
        List<String> members = new ArrayList<>(List.of("Alex"));

        ImmutableTeam team = new ImmutableTeam("Platform", members);
        members.add("Jordan");

        assertEquals(
                List.of("Alex"),
                team.getMembers()
        );
    }

    @Test
    void shouldReturnANewTeamWhenAddingMember() {
        ImmutableTeam original = new ImmutableTeam(
                "Platform",
                List.of("Alex")
        );
        ImmutableTeam updated = original.addMember("Jordan");
        assertEquals(
                List.of("Alex"),
                original.getMembers()
        );

        assertEquals(
                List.of("Alex", "Jordan"),
                updated.getMembers()
        );

    }

    @Test
    void shouldRejectExternalModification() {
        ImmutableTeam team = new ImmutableTeam(
                "Platform",
                List.of("Alex")
        );

        assertThrows(
                UnsupportedOperationException.class,
                ()->team.getMembers().add("Jordan")
        );
    }
}
