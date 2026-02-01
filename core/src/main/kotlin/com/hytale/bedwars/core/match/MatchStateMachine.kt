package com.hytale.bedwars.core.match

class MatchStateMachine {
    private val transitions: Map<MatchState, Set<MatchState>> =
        mapOf(
            MatchState.LOBBY to setOf(MatchState.STARTING),
            MatchState.STARTING to setOf(MatchState.LOBBY, MatchState.LOADING_MAP),
            MatchState.LOADING_MAP to setOf(MatchState.TELEPORTING, MatchState.DESTROYING),
            MatchState.TELEPORTING to setOf(MatchState.INGAME, MatchState.DESTROYING),
            MatchState.INGAME to setOf(MatchState.ENDING, MatchState.DESTROYING),
            MatchState.ENDING to setOf(MatchState.DESTROYING),
            MatchState.DESTROYING to emptySet(),
        )

    fun canTransition(
        from: MatchState,
        to: MatchState,
    ): Boolean {
        return transitions[from]?.contains(to) == true
    }
}
