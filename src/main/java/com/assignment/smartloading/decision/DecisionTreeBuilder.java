package com.assignment.smartloading.decision;

public class DecisionTreeBuilder {

    public static DecisionNode buildTree(DecisionTreeEngine engine) {

        DecisionNode likesNode = new DecisionNode(
                "Rule 1: Personal Likes",
                ctx -> ctx.likedCategories != null && !ctx.likedCategories.isEmpty(),
                engine::applyLikes
        );

        DecisionNode clicksNode = new DecisionNode(
                "Rule 2: Personal Clicks",
                ctx -> ctx.clickedCategories != null && !ctx.clickedCategories.isEmpty(),
                engine::applyClicks
        );

        DecisionNode globalNode = new DecisionNode(
                "Rule 3: Global Fallback",
                ctx -> true,
                engine::applyGlobal
        );

        //continue to search product if not yet hit maximum
        likesNode.nextIfTrue(clicksNode).nextIfFalse(clicksNode);
        clicksNode.nextIfTrue(globalNode).nextIfFalse(globalNode);

        return likesNode;
    }
}
