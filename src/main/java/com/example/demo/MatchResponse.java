package com.example.demo;

public class MatchResponse {
    private Long    matchId;
    private String  name;
    private Integer avatarIndex;

    public MatchResponse(Long matchId, String name, Integer avatarIndex) {
        this.matchId     = matchId;
        this.name        = name;
        this.avatarIndex = avatarIndex;
    }

    // Lägg till dessa:
    public Long getMatchId() {
        return matchId;
    }

    public String getName() {
        return name;
    }

    public Integer getAvatarIndex() {
        return avatarIndex;
    }
}
