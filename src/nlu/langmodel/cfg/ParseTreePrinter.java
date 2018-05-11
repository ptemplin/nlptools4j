package nlu.langmodel.cfg;

import java.util.LinkedList;

public class ParseTreePrinter {

    public static void printTreeByLevel(ParseNode root) {
        LinkedList<ParseNodeDepth> toVisit = new LinkedList<>();
        toVisit.add(new ParseNodeDepth(root, 0));

        int currentLevel = 0;
        while (!toVisit.isEmpty()) {
            ParseNodeDepth current = toVisit.removeFirst();
            if (currentLevel != current.depth) {
                currentLevel++;
                System.out.println();
            }
            System.out.print(String.format("%s   ", current.node.rule));

            for (ParseNode node : current.node.children) {
                toVisit.addLast(new ParseNodeDepth(node, currentLevel + 1));
            }
        }
        System.out.println();
    }

    public static void printTreeWithTabs(ParseNode root) {
        printTreeWithTabsInternal(root, 0);
    }

    private static void printTreeWithTabsInternal(ParseNode root, int depth) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= depth; i++) {
            builder.append("  ");
        }
        System.out.println(builder.toString() + root.rule);

        for (ParseNode node : root.children) {
            printTreeWithTabsInternal(node, depth + 1);
        }
    }

    private static class ParseNodeDepth {
        public ParseNode node;
        public int depth;
        public ParseNodeDepth(ParseNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

}
