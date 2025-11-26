package com.assignment.smartloading.decision;

import java.util.List;

public class DecisionTreeBuilder {

    public static DecisionTreeNode buildTree() {

        // Leaf A — User Likes
        DecisionTreeNode likedLeaf = new DecisionTreeNode("Liked Leaf", null)
                .outcome(new DecisionOutcome(
                        DecisionOutcomeType.LIKES,
                        List.of(),
                        "Using liked categories"
                ));

        // Leaf B — User Click Behavior
        DecisionTreeNode clickLeaf = new DecisionTreeNode("Click Leaf", null)
                .outcome(new DecisionOutcome(
                        DecisionOutcomeType.CLICKS,
                        List.of(),
                        "Using click-based behavior"
                ));

        // Leaf C — Global Categories
        DecisionTreeNode globalLeaf = new DecisionTreeNode("Global Leaf", null)
                .outcome(new DecisionOutcome(
                        DecisionOutcomeType.GLOBAL,
                        List.of("popular","clothing","shoes","bags","headphones"),
                        "Using global fallback"
                ));

        // Node: has behavior?
        DecisionTreeNode nodeBehavior =
                new DecisionTreeNode("Has Click Behavior?", DecisionContext::hasBehavior)
                        .trueNode(clickLeaf)
                        .falseNode(globalLeaf);

        // Node: has likes?
        DecisionTreeNode nodeLikes =
                new DecisionTreeNode("Has Liked Categories?", DecisionContext::hasLikes)
                        .trueNode(likedLeaf)
                        .falseNode(nodeBehavior);

        return nodeLikes;
    }
}
