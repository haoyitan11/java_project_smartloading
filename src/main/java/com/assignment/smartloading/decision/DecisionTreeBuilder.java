package com.assignment.smartloading.decision;

public class DecisionTreeBuilder {

    public static DecisionNode buildTree(DecisionTreeEngine engine) {

        DecisionNode likesNode = new DecisionNode(
                "Rule 1: Fill up to 8 from Personal Likes",
                ctx -> ctx.likedCategories != null && !ctx.likedCategories.isEmpty(),
                engine::applyLikes
        );

        DecisionNode clicksNode = new DecisionNode(
                "Rule 2: Fill up to 4 from Personal Clicks",
                ctx -> ctx.clickedCategories != null && !ctx.clickedCategories.isEmpty(),
                engine::applyClicks
        );

        DecisionNode globalNode = new DecisionNode(
                "Rule 3: Fill up to 3 from Global Likes+Clicks",
                ctx -> true,
                engine::applyGlobal
        );

        likesNode.next(clicksNode).next(globalNode);

        return likesNode;
    }
}
