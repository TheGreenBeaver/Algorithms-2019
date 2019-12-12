package lesson5;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class JavaGraphTasks {
    /**
     * Эйлеров цикл.
     * Средняя
     * <p>
     * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
     * Если в графе нет Эйлеровых циклов, вернуть пустой список.
     * Соседние дуги в списке-результате должны быть инцидентны друг другу,
     * а первая дуга в списке инцидентна последней.
     * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
     * Веса дуг никак не учитываются.
     * <p>
     * Пример:
     * <p>
     * G -- H
     * |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     * <p>
     * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
     * <p>
     * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
     * связного графа ровно по одному разу
     */
    // shouldExist == false means the list is empty
    public static List<Graph.Edge> findEulerLoop(Graph graph) {
        List<Graph.Edge> ans = new ArrayList<>();
        Set<Graph.Edge> notVisited = graph.getEdges();
        if (notVisited.size() >= 3) {
            Stack<Graph.Vertex> vStack = new Stack<>();
            Stack<Graph.Edge> eStack = new Stack<>();

            vStack.push(notVisited.iterator().next().getBegin());

            Graph.Vertex v;
            while (!vStack.isEmpty()) {
                v = vStack.peek();
                Map<Graph.Vertex, Graph.Edge> connections = graph.getConnections(v);

                for (Map.Entry<Graph.Vertex, Graph.Edge> e : connections.entrySet()) {
                    Graph.Edge edge = e.getValue();
                    if (notVisited.contains(edge)) {
                        notVisited.remove(edge);
                        vStack.push(e.getKey());
                        eStack.push(edge);
                        break;
                    }
                }

                if (v.equals(vStack.peek())) {
                    if (!eStack.isEmpty()){
                        Graph.Edge popped = eStack.pop();
                        if (ans.isEmpty() || connected(popped, ans.get(ans.size() - 1))) {
                            ans.add(popped);
                        } else {
                            break;
                        }
                    }
                    vStack.pop();
                }
            }

            if (!notVisited.isEmpty() || !connected(ans.get(0), ans.get(ans.size() - 1))) {
                ans.clear();
            }
        }
        return ans;
    }

    private static boolean connected(Graph.Edge first, Graph.Edge second) {
        Graph.Vertex fB = first.getBegin();
        Graph.Vertex fE = first.getEnd();
        Graph.Vertex sB = second.getBegin();
        Graph.Vertex sE = second.getEnd();

        return fB.equals(sB) || fB.equals(sE) || fE.equals(sE) || fE.equals(sB);
    }

    // Сложность алгоритма: O(graph.getEdges().size())

    // Оценка ресурсоёмкости: O(graph.getEdges().size() + graph.getVertices().size())

    /**
     * Минимальное остовное дерево.
     * Средняя
     *
     * Дан граф (получатель). Найти по нему минимальное остовное дерево.
     * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
     * вернуть любое из них. Веса дуг не учитывать.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Ответ:
     *
     *      G    H
     *      |    |
     * A -- B -- C -- D
     * |    |    |
     * E    F    I
     * |
     * J ------------ K
     */
    public static Graph minimumSpanningTree(Graph graph) {
        Set<Graph.Edge> necessaryEdges = new HashSet<>();
        Set<Graph.Vertex> notVisited = graph.getVertices();
        if (!notVisited.isEmpty()) {
            Graph.Vertex start = graph.getEdges().iterator().next().getBegin();
            Queue<Graph.Vertex> vQueue = new ArrayDeque<>(notVisited.size());
            vQueue.add(start);
            while (!vQueue.isEmpty()) {
                Graph.Vertex current = vQueue.poll();
                Map<Graph.Vertex, Graph.Edge> connections = graph.getConnections(current);
                Set<Graph.Vertex> neighbours = connections.keySet();
                for (Graph.Vertex n : neighbours) {
                    if (notVisited.contains(n)) {
                        vQueue.add(n);
                        necessaryEdges.add(connections.get(n));
                        notVisited.remove(n);
                    }
                }
            }
        }
        if (!notVisited.isEmpty()) {
            necessaryEdges.clear();
        }
        return new Graph() {
            @NotNull
            @Override
            public Set<Vertex> getVertices() {
                return graph.getVertices();
            }

            @NotNull
            @Override
            public Set<Edge> getEdges() {
                return necessaryEdges;
            }

            @Nullable
            @Override
            public Vertex get(String name) {
                for (Vertex v : graph.getVertices()) {
                    if (v.getName().equals(name)) {
                        return v;
                    }
                }
                return null;
            }

            @NotNull
            @Override
            public Map<Vertex, Edge> getConnections(@NotNull Vertex v) {
                Map<Vertex, Edge> res = new HashMap<>();
                for (Edge e : necessaryEdges) {
                    if (e.getBegin().equals(v)) {
                        res.put(e.getEnd(), e);
                    } else if (e.getEnd().equals(v)) {
                        res.put(e.getBegin(), e);
                    }
                }
                return res;
            }
        };
    }

    // Сложность алгоритма: аналогична таковой для bfs, т.е. O(graph.getEdges().size() + graph.getVertices().size())

    // Оценка ресурсоёмкости: O(graph.getVertices().size())


    /**
     * Максимальное независимое множество вершин в графе без циклов.
     * Сложная
     * <p>
     * Дан граф без циклов (получатель), например
     * <p>
     * G -- H -- J
     * |
     * A -- B -- D
     * |         |
     * C -- F    I
     * |
     * E
     * <p>
     * Найти в нём самое большое независимое множество вершин и вернуть его.
     * Никакая пара вершин в независимом множестве не должна быть связана ребром.
     * <p>
     * Если самых больших множеств несколько, приоритет имеет то из них,
     * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
     * <p>
     * В данном случае ответ (A, E, F, D, G, J)
     * <p>
     * Если на входе граф с циклами, бросить IllegalArgumentException
     * <p>
     * Эта задача может быть зачтена за пятый и шестой урок одновременно
     */
    public static Set<Graph.Vertex> largestIndependentVertexSet(Graph graph) {
        Map<Graph.Vertex, Set<Graph.Vertex>> iSets = new HashMap<>();
        Set<Graph.Vertex> notVisited = graph.getVertices();
        if (notVisited.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Graph.Vertex> ans = new HashSet<>();
        while (!notVisited.isEmpty()) {
            Graph.Vertex start = notVisited.iterator().next();
            if (dfs(graph, start, notVisited)) {
                ans.addAll(independentSet(graph, start, null, iSets));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return ans;
    }

    private static boolean dfs(Graph graph, Graph.Vertex current, Set<Graph.Vertex> notVisited) {
        notVisited.remove(current);
        Set<Graph.Vertex> neighbours = graph.getNeighbors(current);
        int visitedAmount = 0;
        for (Graph.Vertex n : neighbours) {
            if (!notVisited.contains(n) && visitedAmount++ > 1) {
                return false;
            }
        }
        for (Graph.Vertex n : neighbours) {
            if (notVisited.contains(n)) {
                return dfs(graph, n, notVisited);
            }
        }
        return true;
    }

    private static Set<Graph.Vertex> independentSet(Graph graph,
                                                    Graph.Vertex vertex,
                                                    Graph.Vertex parent,
                                                    Map<Graph.Vertex, Set<Graph.Vertex>> iSets) {
        if (iSets.get(vertex) != null) {
            return iSets.get(vertex);
        }

        Set<Graph.Vertex> childrenSum = new HashSet<>();
        Set<Graph.Vertex> gChildrenSum = new HashSet<>();
        Set<Graph.Vertex> children = graph.getNeighbors(vertex);
        if (parent != null) {
            children.remove(parent);
        }
        HashMap<Graph.Vertex, Set<Graph.Vertex>> grandChildren = new HashMap<>();
        for (Graph.Vertex child : children) {
            Set<Graph.Vertex> c = graph.getNeighbors(child);
            c.remove(vertex);
            grandChildren.put(child, c);
            childrenSum.addAll(independentSet(graph, child, vertex, iSets));
        }
        for (Map.Entry<Graph.Vertex, Set<Graph.Vertex>> grandChild : grandChildren.entrySet()) {
            for (Graph.Vertex gcv : grandChild.getValue()) {
                gChildrenSum.addAll(independentSet(graph, gcv, grandChild.getKey(), iSets));
            }
        }
        Set<Graph.Vertex> res;
        if (childrenSum.size() > gChildrenSum.size() + 1) {
            res = childrenSum;
            iSets.put(vertex, childrenSum);
        } else {
            gChildrenSum.add(vertex);
            res = gChildrenSum;
            iSets.put(vertex, gChildrenSum);
        }
        return res;
    }

    // Сложность алгоритма: O(graph.getEdges().size() + graph.getVertices().size())

    // Оценка ресурсоёмкости: O(graph.getVertices().size()^2)

    /**
     * Наидлиннейший простой путь.
     * Сложная
     * <p>
     * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
     * Простым считается путь, вершины в котором не повторяются.
     * Если таких путей несколько, вернуть любой из них.
     * <p>
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     * <p>
     * Ответ: A, E, J, K, D, C, H, G, B, F, I
     */
    public static Path longestSimplePath(Graph graph) {
        Set<Graph.Vertex> notVisitedL = graph.getVertices();
        Set<Graph.Vertex> notVisitedG = graph.getVertices();
        if (notVisitedL.isEmpty()) {
            return new Path();
        } else {
            List<Graph.Vertex> ans = new ArrayList<>();
            while (!notVisitedG.isEmpty()) {
                Graph.Vertex start = notVisitedG.iterator().next();
                Stack<Graph.Vertex> lRes = new Stack<>();
                pathDFS(graph, start, notVisitedL, notVisitedG, lRes, ans);
            }
            return new Path(ans, ans.size() - 1);
        }
    }

    private static void pathDFS(Graph graph,
                                Graph.Vertex current,
                                Set<Graph.Vertex> notVisitedL,
                                Set<Graph.Vertex> notVisitedG,
                                Stack<Graph.Vertex> path,
                                List<Graph.Vertex> result) {
        notVisitedL.remove(current);
        notVisitedG.remove(current);
        path.push(current);
        for (Graph.Vertex v : graph.getNeighbors(current)) {
            if (notVisitedL.contains(v)) {
                pathDFS(graph, v, notVisitedL, notVisitedG, path, result);
            }
        }
        if (path.size() > result.size()) {
            result.clear();
            result.addAll(path);
        }
        notVisitedL.add(current);
        path.pop();
    }
}

// Сложность алгоритма: O(graph.getEdges().size() + graph.getVertices().size())

// Оценка ресурсоёмкости: O(graph.getVertices().size()) (рекурсия)