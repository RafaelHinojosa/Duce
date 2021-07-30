package duce;

import java.util.Comparator;
import duce.models.MatchingUser;

public class ScoreComparator implements Comparator<MatchingUser> {
    public int compare(MatchingUser user1, MatchingUser user2) {
        return user1.getScore() - user2.getScore();
    }
}