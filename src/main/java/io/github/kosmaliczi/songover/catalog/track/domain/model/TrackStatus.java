package io.github.kosmaliczi.songover.catalog.track.domain.model;

public enum TrackStatus {
    DRAFT,
    PROCESSING,
    READY,
    FAILED,
    PUBLISHED,
    UNPUBLISHED,
    BLOCKED;

    public boolean canTransitionTo(TrackStatus target) {
        return switch (this) {
            case DRAFT, FAILED -> target == PROCESSING || target == BLOCKED;
            case PROCESSING -> target == READY || target == FAILED || target == BLOCKED;
            case READY, UNPUBLISHED -> target == PUBLISHED || target == BLOCKED;
            case PUBLISHED -> target == UNPUBLISHED || target == BLOCKED;
            case BLOCKED -> target == UNPUBLISHED;
        };
    }
}
