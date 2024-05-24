import networkx as nx
import matplotlib.pyplot as plt


def plot_multigraph(solution):
    num_nodes = len(solution)

    # Create a multigraph
    G = nx.MultiGraph()

    # Add nodes to the graph
    G.add_nodes_from(range(num_nodes))

    # Add edges to the graph based on the solution
    for i in range(num_nodes):
        for j in range(num_nodes):
            multiplicity = solution[i][j]
            for _ in range(multiplicity):
                G.add_edge(i, j)

    # Plot the graph
    pos = nx.circular_layout(G)

    # Draw nodes
    nx.draw_networkx_nodes(G, pos, node_size=700, node_color="skyblue", alpha=0.8)

    # Draw edges
    nx.draw_networkx_edges(G, pos, width=1.0, alpha=0.5)

    # Draw node labels (node indices)
    labels = {i: str(i) for i in range(num_nodes)}
    nx.draw_networkx_labels(G, pos, labels=labels, font_size=8, font_color="black")

    # Draw edge labels (multiplicity)
    edge_labels = {(i, j): solution[i][j] for i in range(num_nodes) for j in range(num_nodes) if solution[i][j] > 0}
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=8, font_color="red")

    plt.show()

