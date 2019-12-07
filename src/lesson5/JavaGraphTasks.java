package lesson5;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

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
        ArrayList<Graph.Edge> ans = new ArrayList<>(Collections.emptyList());
        Set<Graph.Edge> edges = graph.getEdges();
        if (edges.size() >= 3) {
            Stack<Graph.Vertex> vStack = new Stack<>();
            Stack<Graph.Edge> eStack = new Stack<>();

            vStack.push(edges.iterator().next().getBegin());
            HashMap<Graph.Edge, Boolean> visited = new HashMap<>();
            for (Graph.Edge e : edges) {
                visited.put(e, false);
            }

            Graph.Vertex v;
            int visitedAmount = 0;
            while (!vStack.isEmpty()) {
                v = vStack.peek();
                Map<Graph.Vertex, Graph.Edge> connections = graph.getConnections(v);

                for (Map.Entry<Graph.Vertex, Graph.Edge> e : connections.entrySet()) {
                    Graph.Edge edge = e.getValue();
                    if (!visited.get(edge)) {
                        visited.put(edge, true);
                        vStack.push(e.getKey());
                        eStack.push(edge);
                        visitedAmount++;
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

            if (visitedAmount != edges.size() || !connected(ans.get(0), ans.get(ans.size() - 1))) {
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
        Set<Graph.Vertex> vertices = graph.getVertices();
        int visitedAmount = 1;
        HashMap<Graph.Vertex, Boolean> visited = new HashMap<>();
        if (!vertices.isEmpty()) {
            Graph.Vertex start = graph.getEdges().iterator().next().getBegin();
            for (Graph.Vertex v: vertices) {
                visited.put(v, v.equals(start));
            }
            ArrayBlockingQueue<Graph.Vertex> vQueue = new ArrayBlockingQueue<>(vertices.size());
            vQueue.add(start);
            while (!vQueue.isEmpty()) {
                Graph.Vertex current = vQueue.poll();
                Map<Graph.Vertex, Graph.Edge> connections = graph.getConnections(current);
                Set<Graph.Vertex> neighbours = connections.keySet();
                for (Graph.Vertex n : neighbours) {
                    if (!visited.get(n)) {
                        vQueue.add(n);
                        necessaryEdges.add(connections.get(n));
                        visited.put(n, true);
                        visitedAmount++;
                    }
                }
            }
        }
        if (visitedAmount != visited.size()) {
            necessaryEdges.clear();
        }
        return new Graph() {
            @NotNull
            @Override
            public Set<Vertex> getVertices() {
                return vertices;
            }

            @NotNull
            @Override
            public Set<Edge> getEdges() {
                return necessaryEdges;
            }

            @Nullable
            @Override
            public Vertex get(String name) {
                for (Vertex v : vertices) {
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
        iSets = new HashMap<>();
        Set<Graph.Vertex> vertices = graph.getVertices();
        if (vertices.isEmpty()) {
            return Collections.emptySet();
        }
        HashMap<Graph.Vertex, Boolean> visited = new HashMap<>();
        for (Graph.Vertex v : vertices) {
            visited.put(v, false);
        }
        passed = 0;
        Set<Graph.Vertex> ans = new HashSet<>();
        while (passed != vertices.size()) {
            Graph.Vertex start = null;
            for (Graph.Vertex v : vertices) {
                if (!visited.get(v)) {
                    start = v;
                    break;
                }
            }
            if (dfs(graph, start, visited)) {
                ans.addAll(independentSet(graph, start, null));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return ans;
    }

    private static int passed;

    private static boolean dfs(Graph graph, Graph.Vertex current, HashMap<Graph.Vertex, Boolean> visited) {
        visited.put(current, true);
        passed++;
        Set<Graph.Vertex> neighbours = graph.getNeighbors(current);
        int visitedAmount = 0;
        for (Graph.Vertex n : neighbours) {
            if (visited.get(n) && visitedAmount++ > 1) {
                return false;
            }
        }
        for (Graph.Vertex n : neighbours) {
            if (!visited.get(n)) {
                return dfs(graph, n, visited);
            }
        }
        return true;
    }

    private static HashMap<Graph.Vertex, Set<Graph.Vertex>> iSets;

    private static Set<Graph.Vertex> independentSet(Graph graph, Graph.Vertex vertex, Graph.Vertex parent) {
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
            childrenSum.addAll(independentSet(graph, child, vertex));
        }
        for (Map.Entry<Graph.Vertex, Set<Graph.Vertex>> grandChild : grandChildren.entrySet()) {
            for (Graph.Vertex gcv : grandChild.getValue()) {
                gChildrenSum.addAll(independentSet(graph, gcv, grandChild.getKey()));
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
//        path = new ArrayList<>();
//
//        Set<Graph.Vertex> connectedVertices = new HashSet<>();
//        Set<Graph.Edge> allEdges = graph.getEdges();
//        for (Graph.Edge e : allEdges) {
//            connectedVertices.add(e.getBegin());
//            connectedVertices.add(e.getEnd());
//        }
//        visited = new HashMap<>();
//        for (Graph.Vertex v : connectedVertices) {
//            visited.put(v, false);
//        }
//        int visitedAmount = 0;
//        while (visitedAmount != connectedVertices.size()) {
//            Graph.Vertex start = null;
//            for (Graph.Vertex v : connectedVertices) {
//                if (!visited.get(v)) {
//                    start = v;
//                    break;
//                }
//            }
//            currentPath.clear();
//            last = null;
//            dfs(graph, start);
//        }
//        return new Path(path, path.size());
        throw new NotImplementedError();
    }

//    private static Stack<Graph.Vertex> currentPath = new Stack<>();
//    private static List<Graph.Vertex> path;
//    private static HashMap<Graph.Vertex, Boolean> visited;
//    private static Graph.Vertex last;
//
//    private static void dfs(Graph graph,
//                     Graph.Vertex current
//                     ) {
//        currentPath.push(current);
//        visited.put(current, true);
//        Set<Graph.Vertex> neighbours = graph.getNeighbors(current);
//        for (Graph.Vertex n : neighbours) {
//            if (!visited.get(n) && !n.equals(last)) {
//                last = null;
//                dfs(graph, n);
//            }
//        }
//        if (path.size() < currentPath.size()) {
//            path = new ArrayList<>(currentPath);
//        }
//        last = current;
//        visited.put(currentPath.pop(), false);
//    }
}
